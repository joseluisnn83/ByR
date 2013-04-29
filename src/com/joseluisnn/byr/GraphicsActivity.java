package com.joseluisnn.byr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Toast;
import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;
import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.GraphXLabelFormat;
import com.joseluisnn.objetos.ValoresElementosGraficas;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

public class GraphicsActivity extends Activity {

	private static final int ENTERO_FECHA_DIA_DE_HOY = 0;
	private static final int ENTERO_FECHA_5_ANYOS_ANTES = 1;
	private static final int ENTERO_FECHA_5_MESES_ANTES = 2;
	private static final int ENTERO_FECHA_31_DIAS_ANTES = 3;

	/*
	 * Variable para saber que se devuelven valores de la actividad a la que
	 * llamo a través del método startAvivityForResult :
	 * PreferencesGraphicsActivity
	 */
	private static final int RESULT_SETTINGS = 1;
	/*
	 * Variable para dibujar la gráfica
	 */
	private XYPlot mySimpleXYPlot;
	private PointF minXY;
	private PointF maxXY;
	/*
	 * Definition of the touch states
	 */
	static final int NONE = 0;
	static final int ONE_FINGER_DRAG = 1;
	static final int TWO_FINGERS_DRAG = 2;
	int mode = NONE;
	PointF firstFinger;
	float lastScrolling;
	float distBetweenFingers;
	float lastZooming;
	/*
	 * Variable para la BASE DE DATOS
	 */
	private DBAdapter dba;
	/*
	 * Variable para leer el archivo de configuración de la gráfica
	 */
	private SharedPreferences preferenceConfiguracionPrivate;
	private SingletonConfigurationSharedPreferences singleton_csp;
	/*
	 * Listas para los Ingresos y los Gastos
	 */
	ArrayList<ValoresElementosGraficas> listadoValoresIngresos;
	ArrayList<ValoresElementosGraficas> listadoValoresGastos;
	ArrayList<Double> ingresosHistoricos;
	ArrayList<Double> gastosHistoricos;
	ArrayList<Double> balanceHistoricos;
	ArrayList<String> leyendaEjeX;
	/*
	 * Variables para saber el Mínimo y Máximo valor de la gráfica
	 */
	private Double minCantidad;
	private Double maxCantidad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/*
		 * Quitamos barra de titulo de la Actividad Debe ser ejecutada esta
		 * instruccion antes del setContentView para que no cargue las imágenes
		 */
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_graphics);
		/*
		 * Inicializamos el objeto XYPlot búscandolo desde el layout:
		 */
		mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		// mySimpleXYPlot.setOnTouchListener(this);
		/*
		 * Instancio la Base de Datos
		 */
		dba = DBAdapter.getInstance(this);
		/*
		 * Abro la Base de Datos en modo lectura
		 */
		dba.openREAD();
		/*
		 * Instancio el objeto SharedPreference para leer las opciones de
		 * configuración y así dibujar la gráfica
		 */
		singleton_csp = new SingletonConfigurationSharedPreferences();
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION_GRAFICAS,
				Context.MODE_PRIVATE);

		// Creo por primera vez los ArrayList de valores
		listadoValoresIngresos = new ArrayList<ValoresElementosGraficas>();
		listadoValoresGastos = new ArrayList<ValoresElementosGraficas>();
		ingresosHistoricos = new ArrayList<Double>();
		gastosHistoricos = new ArrayList<Double>();
		balanceHistoricos = new ArrayList<Double>();

		/*
		 * Pasos para dibujar la gráfica: 1) Leo del fichero de configuración
		 * que tipo de gráfica se quiere representar (Anual,mensual,diaria) y
		 * que tipo de valores (Histórico,previsión) y cargo los valores en las
		 * listas 2) Preparar las series para mostrarlas como líneas gráficas 3)
		 * Configuro la grafica 4) La dibujo
		 */
		cargarListasDeValores();

		if (listadoValoresIngresos.isEmpty() && listadoValoresGastos.isEmpty()) {
			mySimpleXYPlot.clear();
			Toast.makeText(getApplicationContext(),
					"No hay datos para poder mostrar gŕaficas.",
					Toast.LENGTH_SHORT).show();
		} else {
			prepararLineasGraficas();
			configurarGrafica();
			dibujarGrafica();
		}

		// Cierro la Base de datos
		dba.close();

	}

	/*
	 * Método que carga las lista de valores según si el usuario ha elegido
	 * valores históricos o con previsión
	 */
	private void cargarListasDeValores() {

		switch (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPVALORESGRAFICA, 0)) {
		case 0: // HISTORICO
			cargarValoresHistoricos();
			break;
		case 1: // CON PREVISION
			cargarValoresPrevision();
			break;
		}
	}

	private void cargarValoresHistoricos() {

		int enteroFechaInicial;
		int enteroFechaFinal;
		Integer integerFechaInicial;
		Integer integerFechaFinal;
		// Indice que va a recorrer las listas de ingresos y gastos
		int indiceAux = 0;
		// Variables para los valores mínimos y máximos
		Double max = Double.MIN_VALUE;
		Double min = Double.MAX_VALUE;

		if (!listadoValoresIngresos.isEmpty()) {
			listadoValoresIngresos.clear();
		}
		if (!listadoValoresGastos.isEmpty()) {
			listadoValoresGastos.clear();
		}
		if (!ingresosHistoricos.isEmpty()) {
			ingresosHistoricos.clear();
		}
		if (!gastosHistoricos.isEmpty()) {
			gastosHistoricos.clear();
		}
		if (!balanceHistoricos.isEmpty()) {
			balanceHistoricos.clear();
		}

		/*
		 * accedo a la BD para que me devuelva los ingresos y gastos en sus
		 * respectivas Listas
		 */
		switch (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0)) {
		case 0: // ANUAL
			enteroFechaInicial = obtenerEnteroFecha(ENTERO_FECHA_5_ANYOS_ANTES);
			integerFechaInicial = Integer.valueOf(("" + enteroFechaInicial)
					.substring(0, 4));
			enteroFechaFinal = obtenerEnteroFecha(ENTERO_FECHA_DIA_DE_HOY);
			integerFechaFinal = Integer.valueOf(("" + enteroFechaFinal)
					.substring(0, 4));

			listadoValoresIngresos = dba.listadoValoresAnualesGraficas(
					enteroFechaInicial, enteroFechaFinal, "ingreso");
			listadoValoresGastos = dba.listadoValoresAnualesGraficas(
					enteroFechaInicial, enteroFechaFinal, "gasto");

			if (!listadoValoresIngresos.isEmpty()) {
				// Relleno los valores de los ingresos para mostrarlo en la
				// grafica
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {

					if (Integer.valueOf(
							listadoValoresIngresos.get(indiceAux).getFecha())
							.intValue() == i
							&& indiceAux < listadoValoresIngresos.size()) {

						ingresosHistoricos.add(listadoValoresIngresos.get(
								indiceAux).getCantidad());

						if (listadoValoresIngresos.get(indiceAux).getCantidad() >= max) {
							max = listadoValoresIngresos.get(indiceAux)
									.getCantidad();
						}

						if (listadoValoresIngresos.get(indiceAux).getCantidad() <= min) {
							min = listadoValoresIngresos.get(indiceAux)
									.getCantidad();
						}

						indiceAux++;
					} else {
						ingresosHistoricos.add(0.0);
					}
				}
			}

			// Inicializo indiceAux a 0 para utilizarlo de nuevo
			indiceAux = 0;

			if (!listadoValoresGastos.isEmpty()) {
				// Relleno los valores de los gastos para mostrarlos en la
				// grafica
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {

					if (Integer.valueOf(
							listadoValoresGastos.get(indiceAux).getFecha())
							.intValue() == i
							&& indiceAux < listadoValoresGastos.size()) {

						gastosHistoricos.add(listadoValoresGastos
								.get(indiceAux).getCantidad());

						if (listadoValoresGastos.get(indiceAux).getCantidad() >= max) {
							max = listadoValoresGastos.get(indiceAux)
									.getCantidad();
						}

						if (listadoValoresGastos.get(indiceAux).getCantidad() <= min) {
							min = listadoValoresGastos.get(indiceAux)
									.getCantidad();
						}

						indiceAux++;
					} else {
						gastosHistoricos.add(0.0);
					}
				}
			}

			if (leyendaEjeX == null) {
				leyendaEjeX = new ArrayList<String>();
			} else {
				leyendaEjeX.clear();
			}

			if (!listadoValoresIngresos.isEmpty()
					&& !listadoValoresGastos.isEmpty()) {
				// Me creo el ArrayList para la leyenda del ejeX
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {
					leyendaEjeX.add("" + i);
				}

				// Relleno el balance a partir de los ingresos y gastos
				for (int i = 0; i < ingresosHistoricos.size(); i++) {
					balanceHistoricos.add(ingresosHistoricos.get(i)
							- gastosHistoricos.get(i));
				}

				// Veo si hay minimo y maximo en el balance
				for (int i = 0; i < balanceHistoricos.size(); i++) {

					if (balanceHistoricos.get(i) >= max) {
						max = balanceHistoricos.get(i);
					}

					if (balanceHistoricos.get(i) <= min) {
						min = balanceHistoricos.get(i);
					}

				}

			}

			break;
		case 1: // MENSUAL

			enteroFechaInicial = obtenerEnteroFecha(ENTERO_FECHA_5_MESES_ANTES);
			integerFechaInicial = Integer.valueOf(("" + enteroFechaInicial)
					.substring(0, 6));
			enteroFechaFinal = obtenerEnteroFecha(ENTERO_FECHA_DIA_DE_HOY);
			integerFechaFinal = Integer.valueOf(("" + enteroFechaFinal)
					.substring(0, 6));

			listadoValoresIngresos = dba.listadoValoresMensualesGraficas(
					enteroFechaInicial, enteroFechaFinal, "ingreso");
			listadoValoresGastos = dba.listadoValoresMensualesGraficas(
					enteroFechaInicial, enteroFechaFinal, "gasto");

			ArrayList<Integer> rangoFechas = obtenerRangoFechasMensuales(
					enteroFechaInicial, enteroFechaFinal, 0);

			if (!listadoValoresIngresos.isEmpty()) {
				// Relleno los valores de los ingresos para mostrarlo en la
				// grafica
				for (int i = 0; i < rangoFechas.size(); i++) {

					if (indiceAux < listadoValoresIngresos.size()) {
						if (Integer.valueOf(
								listadoValoresIngresos.get(indiceAux)
										.getFecha()).intValue() == rangoFechas
								.get(i).intValue()) {

							ingresosHistoricos.add(listadoValoresIngresos.get(
									indiceAux).getCantidad());

							if (listadoValoresIngresos.get(indiceAux)
									.getCantidad() >= max) {
								max = listadoValoresIngresos.get(indiceAux)
										.getCantidad();
							}

							if (listadoValoresIngresos.get(indiceAux)
									.getCantidad() <= min) {
								min = listadoValoresIngresos.get(indiceAux)
										.getCantidad();
							}

							indiceAux++;
						} else {
							ingresosHistoricos.add(0.0);
						}
					}
				}
			}

			// Inicializo indiceAux a 0 para utilizarlo de nuevo
			indiceAux = 0;

			if (!listadoValoresGastos.isEmpty()) {
				// Relleno los valores de los gastos para mostrarlos en la
				// grafica
				for (int i = 0; i < rangoFechas.size(); i++) {

					if (indiceAux < listadoValoresGastos.size()) {
						if (Integer.valueOf(
								listadoValoresGastos.get(indiceAux).getFecha())
								.intValue() == rangoFechas.get(i).intValue()) {

							gastosHistoricos.add(listadoValoresGastos.get(
									indiceAux).getCantidad());

							if (listadoValoresGastos.get(indiceAux)
									.getCantidad() >= max) {
								max = listadoValoresGastos.get(indiceAux)
										.getCantidad();
							}

							if (listadoValoresGastos.get(indiceAux)
									.getCantidad() <= min) {
								min = listadoValoresGastos.get(indiceAux)
										.getCantidad();
							}

							indiceAux++;
						} else {
							gastosHistoricos.add(0.0);
						}
					}else{
						gastosHistoricos.add(0.0);
					}
				}
			}

			if (leyendaEjeX == null) {
				leyendaEjeX = new ArrayList<String>();
			} else {
				leyendaEjeX.clear();
			}

			if (!listadoValoresIngresos.isEmpty()
					&& !listadoValoresGastos.isEmpty()) {
				// Me creo el ArrayList para la leyenda del ejeX
				for (int i = 0; i < rangoFechas.size(); i++) {
					// leyendaEjeX.add("" + i);
					leyendaEjeX.add(obtenerLeyendaMensualEjeX(rangoFechas
							.get(i)));
				}

				// Relleno el balance a partir de los ingresos y gastos
				for (int i = 0; i < ingresosHistoricos.size(); i++) {
					balanceHistoricos.add(ingresosHistoricos.get(i)
							- gastosHistoricos.get(i));
				}

				// Veo si hay minimo y maximo en el balance
				for (int i = 0; i < balanceHistoricos.size(); i++) {

					if (balanceHistoricos.get(i) >= max) {
						max = balanceHistoricos.get(i);
					}

					if (balanceHistoricos.get(i) <= min) {
						min = balanceHistoricos.get(i);
					}

				}

			}

			break;
		case 2: // DIARIO
			break;
		}

		maxCantidad = max;
		minCantidad = min;

	}

	/*
	 * Método donde devuelvo el rango de fechas mensuales para poder obtener los
	 * valores de las gráficas que son 0
	 */
	private ArrayList<Integer> obtenerRangoFechasMensuales(
			int enteroFechaInicial, int enteroFechaFinal, int tipoValor) {

		ArrayList<Integer> rango = new ArrayList<Integer>();
		String nuevaFecha = "";
		String day = "";

		switch (tipoValor) {
		case 0: // Histórico
			/*
			 * Al ser histórico lo que ha elegido el usuario lo que devuelvo es
			 * un array de integer de 6 items, los cinco meses anteriores al
			 * actual y el actual
			 */
			rango.add(Integer.valueOf(enteroFechaInicial));

			for (int i = 0; i <= 4; i++) {

				if (Integer.valueOf(
						("" + rango.get(i).intValue()).substring(4, 6))
						.intValue() == 12) {

					nuevaFecha = (Integer.valueOf(
							("" + rango.get(i).intValue()).substring(0, 4))
							.intValue() + 1)
							+ "01";
					rango.add(Integer.valueOf(nuevaFecha));

				} else {

					day = ""
							+ (Integer.valueOf(
									("" + rango.get(i).intValue()).substring(4,
											6)).intValue() + 1);
					if (day.length() == 1) {
						day = "0" + day;
					}
					nuevaFecha = ""
							+ Integer.valueOf(("" + rango.get(i).intValue())
									.substring(0, 4)) + day;
					rango.add(Integer.valueOf(nuevaFecha));

				}
			}

			break;
		case 1: // Previsión
			/*
			 * Al ser previsión lo que ha elegido el usuario lo que devuelvo es
			 * un array de integer de 9 items, los cinco meses anteriores la
			 * parte histórico del mes actual, la parte previsión del mes actual
			 * y los dos meses de previsión siguientes al actual
			 */

			break;

		}

		return rango;
	}

	/*
	 * Método que me devuelve en un String la fecha que le paso por parámetro
	 * para escribir la leyenda mensual del eje X
	 */
	private String obtenerLeyendaMensualEjeX(Integer date) {

		String fechaComprobacion = "" + date.intValue();
		String fecha = "";

		switch (Integer.valueOf(fechaComprobacion.substring(4, 6)).intValue()) {
		case 1:
			fecha = "Ene. ";
			break;
		case 2:
			fecha = "Feb. ";
			break;
		case 3:
			fecha = "Mar. ";
			break;
		case 4:
			fecha = "Abr. ";
			break;
		case 5:
			fecha = "May. ";
			break;
		case 6:
			fecha = "Jun. ";
			break;
		case 7:
			fecha = "Jul. ";
			break;
		case 8:
			fecha = "Ago. ";
			break;
		case 9:
			fecha = "Sep. ";
			break;
		case 10:
			fecha = "Oct. ";
			break;
		case 11:
			fecha = "Nov. ";
			break;
		case 12:
			fecha = "Dic. ";
			break;
		}

		fecha += Integer.valueOf(fechaComprobacion.substring(0, 4)).intValue();

		return fecha;
	}

	private void cargarValoresPrevision() {

		switch (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0)) {
		case 0: // ANUAL
			break;
		case 1: // MENSUAL
			break;
		case 2: // DIARIO
			break;
		}

	}

	/*
	 * Método donde le asigno las series a la gráfica y defino el color de las
	 * líneas y puntos
	 */
	private void prepararLineasGraficas() {

		// Serie de INGRESOS
		XYSeries seriesIngresos = new SimpleXYSeries(ingresosHistoricos,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "INGRESOS");

		mySimpleXYPlot.addSeries(seriesIngresos, new LineAndPointFormatter(
				getResources().getColor(R.color.LightGreen), // Color de la
																// línea
				getResources().getColor(R.color.verde), // Color del punto
				null, // Color del relleno
				new PointLabelFormatter(Color.WHITE))); // Color de los valores
														// del punto

		// Serie de GASTOS
		XYSeries seriesGastos = new SimpleXYSeries(gastosHistoricos,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "GASTOS");

		mySimpleXYPlot.addSeries(seriesGastos, new LineAndPointFormatter(
				getResources().getColor(R.color.LightRed), // Color de la línea
				getResources().getColor(R.color.rojo), // Color del punto
				null, // Relleno
				new PointLabelFormatter(Color.WHITE))); // Color de los valores
														// del punto

		// Serie de BALANCE
		XYSeries seriesBalance = new SimpleXYSeries(balanceHistoricos,
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "BALANCE");

		mySimpleXYPlot.addSeries(seriesBalance, new LineAndPointFormatter(
				getResources().getColor(R.color.CalendarioAzulCabeceraEnd), // Color
																			// de
																			// la
																			// línea
				getResources().getColor(R.color.azul), // Color del punto
				null, // Relleno
				new PointLabelFormatter(Color.WHITE))); // Color de los valores
														// del punto

	}

	/*
	 * Método para dibujar la gráfica
	 */
	private void dibujarGrafica() {

		// Set of internal variables for keeping track of the boundaries
		// Calcula los máximos y mínimos para dibujar la gráfica
		mySimpleXYPlot.calculateMinMaxVals();
		minXY = new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),
				mySimpleXYPlot.getCalculatedMinY().floatValue());
		maxXY = new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),
				mySimpleXYPlot.getCalculatedMaxY().floatValue());

		mySimpleXYPlot.redraw();

	}

	/*
	 * Método que me devuelve en un entero el primer dia de hace 5 Años
	 */
	private int obtenerEnteroFecha(int tipoFecha) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		Calendar c = Calendar.getInstance();

		switch (tipoFecha) {
		case ENTERO_FECHA_DIA_DE_HOY:
			break;
		case ENTERO_FECHA_5_ANYOS_ANTES:
			// Obtengo de la variable Calendar la fecha del primer día de hace 5
			// años
			// antes
			c.add(Calendar.YEAR, -5);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			break;
		case ENTERO_FECHA_5_MESES_ANTES:
			// Obtengo de la variable Calendar la fecha del primer día de hace 5
			// meses
			// antes
			c.add(Calendar.MONTH, -5);
			c.set(Calendar.DATE, 1);
			break;
		case ENTERO_FECHA_31_DIAS_ANTES:
			break;
		default:
			break;
		}
		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}

	@Override
	protected void onPause() {
		// TODO Cierro la BD en OnPause() cuando la Actividad no está en el foco
		if (dba.isOpen()) {
			dba.close();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Abro la base de datos mientras la Actividad está Activa
		if (!dba.isOpen()) {
			dba.openREADWRITE();
		}
		super.onResume();
	}

	/*
	 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
	 * Auto-generated method stub switch (event.getAction() &
	 * MotionEvent.ACTION_MASK) { case MotionEvent.ACTION_DOWN: // Start gesture
	 * firstFinger = new PointF(event.getX(), event.getY()); mode =
	 * ONE_FINGER_DRAG; break; case MotionEvent.ACTION_UP: case
	 * MotionEvent.ACTION_POINTER_UP: //When the gesture ends, a thread is
	 * created to give inertia to the scrolling and zoom Timer t = new Timer();
	 * t.schedule(new TimerTask() {
	 * 
	 * @Override public void run() { while(Math.abs(lastScrolling)>1f ||
	 * Math.abs(lastZooming-1)<1.01){ lastScrolling*=.8; scroll(lastScrolling);
	 * lastZooming+=(1-lastZooming)*.2; zoom(lastZooming);
	 * mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
	 * mySimpleXYPlot.redraw(); //mySimpleXYPlot.redraw(); } } }, 0);
	 * 
	 * case MotionEvent.ACTION_POINTER_DOWN: // second finger distBetweenFingers
	 * = spacing(event); // the distance check is done to avoid false alarms if
	 * (distBetweenFingers > 5f) { mode = TWO_FINGERS_DRAG; } break; case
	 * MotionEvent.ACTION_MOVE: if (mode == ONE_FINGER_DRAG) { PointF
	 * oldFirstFinger=firstFinger; firstFinger=new PointF(event.getX(),
	 * event.getY()); lastScrolling=oldFirstFinger.x-firstFinger.x;
	 * scroll(lastScrolling);
	 * lastZooming=(firstFinger.y-oldFirstFinger.y)/mySimpleXYPlot.getHeight();
	 * if (lastZooming<0) lastZooming=1/(1-lastZooming); else lastZooming+=1;
	 * zoom(lastZooming); mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
	 * BoundaryMode.AUTO); mySimpleXYPlot.redraw();
	 * 
	 * } else if (mode == TWO_FINGERS_DRAG) { float oldDist =distBetweenFingers;
	 * distBetweenFingers=spacing(event);
	 * lastZooming=oldDist/distBetweenFingers; zoom(lastZooming);
	 * mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
	 * mySimpleXYPlot.redraw(); } break; } return true; }
	 * 
	 * private void zoom(float scale) { float domainSpan = maxXY.x - minXY.x;
	 * float domainMidPoint = maxXY.x - domainSpan / 2.0f; float offset =
	 * domainSpan * scale / 2.0f; minXY.x=domainMidPoint- offset;
	 * maxXY.x=domainMidPoint+offset; }
	 * 
	 * private void scroll(float pan) { float domainSpan = maxXY.x - minXY.x;
	 * float step = domainSpan / mySimpleXYPlot.getWidth(); float offset = pan *
	 * step; minXY.x+= offset; maxXY.x+= offset; }
	 * 
	 * private float spacing(MotionEvent event) { float x = event.getX(0) -
	 * event.getX(1); float y = event.getY(0) - event.getY(1); return
	 * FloatMath.sqrt(x * x + y * y); }
	 */

	/*
	 * Método para configurar el diseño de la Gráfica
	 */
	private void configurarGrafica() {

		// Título de la Gráfica
		mySimpleXYPlot.setTitle("Gráfica EJEMPLO");
		// Título ejeY
		mySimpleXYPlot.setRangeLabel("Catindad(€)");
		// Título ejeX
		mySimpleXYPlot.setDomainLabel("");

		mySimpleXYPlot.getGraphWidget().setMarginTop(10);
		mySimpleXYPlot.getGraphWidget().setMarginRight(10);
		mySimpleXYPlot.getGraphWidget().setPaddingLeft(10);

		// Pongo el Plot como un rectangulo con bordes con esquinas
		mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);

		// Me modifica el margin y padding del Cuadro general
		// mySimpleXYPlot.setPlotMargins(10, 10, 10, 10);
		// mySimpleXYPlot.setPlotPadding(10, 10, 10, 10);

		// Configuro la leyenda
		// configurarLeyenda();

		// Rango de valores ejeX a mostrar
		if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 0
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 0) {
			// Entra aquí si es Historico y Anual
			//mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE,leyendaEjeX.size());
			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

			// Le pongo el formato al ejeX (meses)
			mySimpleXYPlot.setDomainValueFormat(new GraphXLabelFormat(
					leyendaEjeX));

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 1
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 0) {
			// Entra aquí si es Historico y Mensual
			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

			// Le pongo el formato al ejeX (meses)
			mySimpleXYPlot.setDomainValueFormat(new GraphXLabelFormat(
				leyendaEjeX));

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 2
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 0) {
			// Entra aquí si es Historico y Diario

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 0
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 1) {
			// Entra aquí si es Con Prevision y Anual

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 1
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 1) {
			// Entra aquí si es Con Prevision y Mensual

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 2
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 1) {
			// Entra aquí si es Con Prevision y Diario

		}

		// Pongo los límites del ejeY (Cantidad en €)
		mySimpleXYPlot.setUserRangeOrigin(0);
		mySimpleXYPlot.setRangeBoundaries(minCantidad - 100, maxCantidad + 100,
				BoundaryMode.FIXED);
		mySimpleXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 50);

	}

	/*
	 * Método para configurar la leyenda
	 */
	private void configurarLeyenda() {

		// use a 2x2 grid:
		mySimpleXYPlot.getLegendWidget().setTableModel(
				new DynamicTableModel(2, 2));

		// adjust the legend size so there is enough room
		// to draw the new legend grid:
		mySimpleXYPlot.getLegendWidget().setSize(
				new SizeMetrics((float) 0.2, SizeLayoutType.RELATIVE,
						(float) 0.2, SizeLayoutType.RELATIVE));

		// add a semi-transparent black background to the legend
		// so it's easier to see overlaid on top of our plot:
		Paint bgPaint = new Paint();
		bgPaint.setColor(Color.BLACK);
		bgPaint.setStyle(Paint.Style.FILL);
		bgPaint.setAlpha(50);
		mySimpleXYPlot.getLegendWidget().setBackgroundPaint(bgPaint);
		// adjust the padding of the legend widget to look a little nicer:
		mySimpleXYPlot.getLegendWidget().setPadding(1, 1, 1, 1);
		// reposition the grid so that it rests above the bottom-left
		// edge of the graph widget:

		mySimpleXYPlot.position(mySimpleXYPlot.getLegendWidget(),
				(float) -0.225, XLayoutStyle.RELATIVE_TO_RIGHT, (float) -0.3,
				YLayoutStyle.RELATIVE_TO_BOTTOM);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * TODO Método donde recojo los parámetros de configuración de la
		 * gráfica de la actividad PreferenceGraphicsActivity
		 */
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_SETTINGS && resultCode == Activity.RESULT_OK
				&& data != null) {
			/*
			 * int year = data.getExtras().getInt("anyo"); int month =
			 * data.getExtras().getInt("mes"); boolean ling =
			 * data.getExtras().getBoolean("lineaIngresos");
			 * 
			 * Toast.makeText( getApplicationContext(),
			 * "Configuración gráfica terminada. Mes: " + month + ", Año: " +
			 * year + " LIng:" + ling, Toast.LENGTH_SHORT).show();
			 */
			dba.openREAD();

			cargarListasDeValores();

			dba.close();

			mySimpleXYPlot.clear();

			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()) {
				// mySimpleXYPlot.clear();
				Toast.makeText(getApplicationContext(),
						"No hay datos para poder mostrar gŕaficas.",
						Toast.LENGTH_SHORT).show();
			} else {
				prepararLineasGraficas();
				configurarGrafica();
				dibujarGrafica();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Cargo el menu de las gráficas
		getMenuInflater().inflate(R.menu.menu_configuracion_graphics, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Lanzo la Actividad PreferencesGraphicsActivity al ser pulsado el
		// item del menu
		switch (item.getItemId()) {

		case R.id.menu_settings_graphics:
			Intent i = new Intent(this, PreferencesGraphicsActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;
		}

		return true;
	}

}