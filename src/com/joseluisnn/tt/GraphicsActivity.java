package com.joseluisnn.tt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.joseluisnn.tt.R;
import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.GraphXLabelFormat;
import com.joseluisnn.objetos.ValoresElementosGraficas;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

public class GraphicsActivity extends Activity implements OnTouchListener {

	private static final int ENTERO_FECHA_DIA_DE_HOY = 0;
	private static final int ENTERO_FECHA_5_ANYOS_ANTES = 1;
	private static final int ENTERO_FECHA_5_MESES_ANTES = 2;
	private static final int ENTERO_FECHA_15_DIAS_ANTES = 3;
	private static final int ENTERO_FECHA_2_ANYOS_DESPUES = 4;
	private static final int ENTERO_FECHA_2_ANYOS_ANTES = 5;
	private static final int ENTERO_FECHA_DIA_DE_AYER = 6;
	private static final int ENTERO_FECHA_7_MESES_DESPUES = 7;
	private static final int ENTERO_FECHA_10_DIAS_ANTES = 8;
	private static final int ENTERO_FECHA_3_MESES_ANTES = 9;
	private static final int ENTERO_FECHA_3_DIAS_ANTES = 10;
	private static final int ENTERO_FECHA_10_DIAS_DESPUES = 11;
	/*
	 * Variables que me indican la catidad de puntos(años) que voy a mostrar en
	 * la gráfica anual de previsión
	 */
	private static final int NUMERO_ANYOS_HISTORICOS_PREVISON = 3;
	private static final int NUMERO_ANYOS_FUTUROS_PREVISON = 3;
	/*
	 * Variables que me indican la catidad de puntos(meses) que voy a mostrar en
	 * la gráfica mensual de previsión
	 */
	private static final int NUMERO_MESES_HISTORICOS_PREVISON = 4;
	private static final int NUMERO_MESES_FUTUROS_PREVISON = 8;
	/*
	 * Variables que me indican la catidad de puntos(días) que voy a mostrar en
	 * la gráfica diaria de previsión
	 */
	private static final int NUMERO_DIAS_HISTORICOS_PREVISON = 3;
	private static final int NUMERO_DIAS_FUTUROS_PREVISON = 12;
	/*
	 * Variable que me indican la catidad de puntos(días) que voy a mostrar en
	 * la gráfica diaria de Histórico
	 */
	private static final int NUMERO_DIAS_HISTORICO = 15;

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
	 * Variables para obtener los valores Listas para los Ingresos y los Gastos
	 */
	ArrayList<ValoresElementosGraficas> listadoValoresIngresos;
	ArrayList<ValoresElementosGraficas> listadoValoresGastos;
	ArrayList<ValoresElementosGraficas> listadoValoresIngresosPrevision;
	ArrayList<ValoresElementosGraficas> listadoValoresGastosPrevision;
	ArrayList<Double> ingresos;
	ArrayList<Double> gastos;
	ArrayList<Double> balance;
	ArrayList<String> leyendaEjeX;
	Double acumIngresosPrevision;
	Double acumGastosPrevision;
	Double acumBalancePrevision;
	/*
	 * Variables para saber el Mínimo y Máximo valor de la gráfica
	 */
	private int minCantidad;
	private int maxCantidad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		
		/*
		 * Quitamos barra de titulo de la Actividad Debe ser ejecutada esta
		 * instruccion antes del setContentView para que no cargue las imágenes
		 */
		setContentView(R.layout.activity_graphics);

		/*
		 * Inicializamos el objeto XYPlot búscandolo desde el layout:
		 */
		mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
		mySimpleXYPlot.setOnTouchListener(this);
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
		listadoValoresIngresosPrevision = new ArrayList<ValoresElementosGraficas>();
		listadoValoresGastosPrevision = new ArrayList<ValoresElementosGraficas>();
		ingresos = new ArrayList<Double>();
		gastos = new ArrayList<Double>();
		balance = new ArrayList<Double>();

		/*
		 * Pasos para dibujar la gráfica: 1) Leo del fichero de configuración
		 * que tipo de gráfica se quiere representar (Anual,mensual,diaria) y
		 * que tipo de valores (Histórico,previsión) y cargo los valores en las
		 * listas 2) Preparar las series para mostrarlas como líneas gráficas 3)
		 * Configuro la grafica 4) La dibujo
		 */
		cargarListasDeValores();

		if (preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPLINEINGRESOS, false) == false
				&& preferenceConfiguracionPrivate.getBoolean(
						singleton_csp.KEY_CBPLINEGASTOS, false) == false
				&& preferenceConfiguracionPrivate.getBoolean(
						singleton_csp.KEY_CBPLINEBALANCE, false) == false) {

			lanzarAdvertencia("Debe seleccionar alguna gráfica en la configuración para mostrar.");

			mySimpleXYPlot.setVisibility(View.INVISIBLE);

		} else {
			prepararLineasGraficas();
			configurarGrafica();
			dibujarGrafica();

			mySimpleXYPlot.setVisibility(View.VISIBLE);

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

	/*
	 * Método que me carga las listas de valores históricos
	 */
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
		if (!ingresos.isEmpty()) {
			ingresos.clear();
		}
		if (!gastos.isEmpty()) {
			gastos.clear();
		}
		if (!balance.isEmpty()) {
			balance.clear();
		}

		/*
		 * accedo a la BD para que me devuelva los ingresos y gastos en sus
		 * respectivas Listas
		 */
		switch (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0)) {
		case 0: // ANUAL
			enteroFechaInicial = obtenerEnteroFechaHistorico(ENTERO_FECHA_5_ANYOS_ANTES);
			integerFechaInicial = Integer.valueOf(("" + enteroFechaInicial)
					.substring(0, 4));
			enteroFechaFinal = obtenerEnteroFechaHistorico(ENTERO_FECHA_DIA_DE_AYER);
			integerFechaFinal = Integer.valueOf(("" + enteroFechaFinal)
					.substring(0, 4));

			listadoValoresIngresos = dba.listadoValoresAnualesGraficas(
					enteroFechaInicial, enteroFechaFinal, "ingreso");
			listadoValoresGastos = dba.listadoValoresAnualesGraficas(
					enteroFechaInicial, enteroFechaFinal, "gasto");

			// INGRESOS
			if (!listadoValoresIngresos.isEmpty()) {
				// Relleno los valores de los ingresos para mostrarlo en la
				// grafica
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {

					if (indiceAux < listadoValoresIngresos.size()) {

						if (Integer.valueOf(
								listadoValoresIngresos.get(indiceAux)
										.getFecha()).intValue() == i) {

							ingresos.add(listadoValoresIngresos.get(indiceAux)
									.getCantidad());

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
							ingresos.add(0.0);
							if (0.0 > max) {
								max = 0.0;
							}
							if (0.0 < min) {
								min = 0.0;
							}
						}
					} else {
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				}
			} else {
				// No hay valores devueltos por la BD
				// relleno los ingresos a 0
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {
					ingresos.add(0.0);
				}
			}

			// Inicializo indiceAux a 0 para utilizarlo de nuevo
			indiceAux = 0;

			// GASTOS
			if (!listadoValoresGastos.isEmpty()) {
				// Relleno los valores de los gastos para mostrarlos en la
				// grafica
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {

					if (indiceAux < listadoValoresGastos.size()) {

						if (Integer.valueOf(
								listadoValoresGastos.get(indiceAux).getFecha())
								.intValue() == i) {

							gastos.add(listadoValoresGastos.get(indiceAux)
									.getCantidad());

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
							gastos.add(0.0);
							if (0.0 > max) {
								max = 0.0;
							}
							if (0.0 < min) {
								min = 0.0;
							}
						}
					} else {
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				}
			} else {
				// No hay valores devueltos por la BD
				// relleno los ingresos a 0
				for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
						.intValue(); i++) {
					gastos.add(0.0);
				}
			}

			if (leyendaEjeX == null) {
				leyendaEjeX = new ArrayList<String>();
			} else {
				leyendaEjeX.clear();
			}

			// Me creo el ArrayList para la leyenda del ejeX
			for (int i = integerFechaInicial.intValue(); i <= integerFechaFinal
					.intValue(); i++) {
				leyendaEjeX.add("" + i);
			}

			// Relleno el balance a partir de los ingresos y gastos y la
			// serie auxiliar
			for (int i = 0; i < ingresos.size(); i++) {
				balance.add(ingresos.get(i) - gastos.get(i));
			}

			// Veo si hay minimo y maximo en el balance
			for (int i = 0; i < balance.size(); i++) {

				if (balance.get(i) >= max) {
					max = balance.get(i);
				}

				if (balance.get(i) <= min) {
					min = balance.get(i);
				}
			}

			break;
		case 1: // MENSUAL

			enteroFechaInicial = obtenerEnteroFechaHistorico(ENTERO_FECHA_5_MESES_ANTES);
			// integerFechaInicial = Integer.valueOf(("" + enteroFechaInicial)
			// .substring(0, 6));
			enteroFechaFinal = obtenerEnteroFechaHistorico(ENTERO_FECHA_DIA_DE_AYER);
			// integerFechaFinal = Integer.valueOf(("" + enteroFechaFinal)
			// .substring(0, 6));

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

							ingresos.add(listadoValoresIngresos.get(indiceAux)
									.getCantidad());

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
							ingresos.add(0.0);
							if (0.0 > max) {
								max = 0.0;
							}
							if (0.0 < min) {
								min = 0.0;
							}
						}
					} else {
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				}
			} else {
				for (int i = 0; i < rangoFechas.size(); i++) {
					ingresos.add(0.0);
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

							gastos.add(listadoValoresGastos.get(indiceAux)
									.getCantidad());

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
							gastos.add(0.0);
							if (0.0 > max) {
								max = 0.0;
							}
							if (0.0 < min) {
								min = 0.0;
							}
						}
					} else {
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				}
			} else {

				for (int i = 0; i < rangoFechas.size(); i++) {
					gastos.add(0.0);
				}
			}

			if (leyendaEjeX == null) {
				leyendaEjeX = new ArrayList<String>();
			} else {
				leyendaEjeX.clear();
			}

			// Me creo el ArrayList para la leyenda del ejeX
			for (int i = 0; i < rangoFechas.size(); i++) {
				// leyendaEjeX.add("" + i);
				leyendaEjeX.add(obtenerLeyendaMensualEjeX(rangoFechas.get(i)));
			}

			// Relleno el balance a partir de los ingresos y gastos
			for (int i = 0; i < ingresos.size(); i++) {
				balance.add(ingresos.get(i) - gastos.get(i));
			}

			// Veo si hay minimo y maximo en el balance
			for (int i = 0; i < balance.size(); i++) {

				if (balance.get(i) >= max) {
					max = balance.get(i);
				}

				if (balance.get(i) <= min) {
					min = balance.get(i);
				}
			}

			break;
		case 2: // DIARIO

			enteroFechaInicial = obtenerEnteroFechaHistorico(ENTERO_FECHA_15_DIAS_ANTES);
			integerFechaInicial = Integer.valueOf(("" + enteroFechaInicial));
			enteroFechaFinal = obtenerEnteroFechaHistorico(ENTERO_FECHA_DIA_DE_AYER);
			integerFechaFinal = Integer.valueOf(("" + enteroFechaFinal));

			listadoValoresIngresos = dba.listadoValoresDiariosGraficas(
					enteroFechaInicial, enteroFechaFinal, "ingreso");
			listadoValoresGastos = dba.listadoValoresDiariosGraficas(
					enteroFechaInicial, enteroFechaFinal, "gasto");

			int dia = enteroFechaInicial;

			// INGRESOS
			if (!listadoValoresIngresos.isEmpty()) {
				// Relleno los valores de los ingresos para mostrarlo en la
				// grafica los 15 días que queremos mostrar de INGRESOS
				for (int i = 0; i < NUMERO_DIAS_HISTORICO; i++) {

					if (indiceAux < listadoValoresIngresos.size()) {

						if (Integer.valueOf(
								listadoValoresIngresos.get(indiceAux)
										.getFecha()).intValue() == dia) {

							ingresos.add(listadoValoresIngresos.get(indiceAux)
									.getCantidad());

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
							ingresos.add(0.0);
							if (0.0 > max) {
								max = 0.0;
							}
							if (0.0 < min) {
								min = 0.0;
							}
						}
					} else {
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
					dia = obtenerDiaSiguiente(dia);
				}
			} else {
				for (int i = 0; i < NUMERO_DIAS_HISTORICO; i++) {
					ingresos.add(0.0);
				}
			}

			// Inicializo indiceAux a 0 y dia para utilizarlos de nuevo
			dia = enteroFechaInicial;
			indiceAux = 0;

			// GASTOS
			if (!listadoValoresGastos.isEmpty()) {
				// Relleno los valores de los ingresos para mostrarlo en la
				// grafica los 15 días que queremos mostrar de GASTOS
				for (int i = 0; i < NUMERO_DIAS_HISTORICO; i++) {

					if (indiceAux < listadoValoresGastos.size()) {

						if (Integer.valueOf(
								listadoValoresGastos.get(indiceAux).getFecha())
								.intValue() == dia) {

							gastos.add(listadoValoresGastos.get(indiceAux)
									.getCantidad());

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
							gastos.add(0.0);
							if (0.0 > max) {
								max = 0.0;
							}
							if (0.0 < min) {
								min = 0.0;
							}
						}
					} else {
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
					dia = obtenerDiaSiguiente(dia);
				}
			} else {
				for (int i = 0; i < NUMERO_DIAS_HISTORICO; i++) {
					gastos.add(0.0);
				}
			}

			if (leyendaEjeX == null) {
				leyendaEjeX = new ArrayList<String>();
			} else {
				leyendaEjeX.clear();
			}

			// Inicializo dia para utilizarla de nuevo
			dia = enteroFechaInicial;

			// Me creo el ArrayList para la leyenda del ejeX
			for (int i = 0; i < NUMERO_DIAS_HISTORICO; i++) {
				leyendaEjeX.add(obtenerLeyendaDiariaEjeX(dia));
				dia = obtenerDiaSiguiente(dia);
			}

			// Relleno el balance a partir de los ingresos y gastos
			for (int i = 0; i < ingresos.size(); i++) {
				balance.add(ingresos.get(i) - gastos.get(i));
			}

			// Veo si hay minimo y maximo en el balance
			for (int i = 0; i < balance.size(); i++) {

				if (balance.get(i) >= max) {
					max = balance.get(i);
				}

				if (balance.get(i) <= min) {
					min = balance.get(i);
				}
			}

			break;
		}

		// Actualizo los valores mínimos y máximos de la gráfica
		maxCantidad = max.intValue();
		minCantidad = min.intValue();

	}

	/*
	 * Método que me carga las listas de valores históricos y de previsión
	 */
	private void cargarValoresPrevision() {

		if (!listadoValoresIngresos.isEmpty()) {
			listadoValoresIngresos.clear();
		}
		if (!listadoValoresGastos.isEmpty()) {
			listadoValoresGastos.clear();
		}
		if (!ingresos.isEmpty()) {
			ingresos.clear();
		}
		if (!gastos.isEmpty()) {
			gastos.clear();
		}
		if (!balance.isEmpty()) {
			balance.clear();
		}

		/*
		 * accedo a la BD para que me devuelva los ingresos y gastos en sus
		 * respectivas Listas
		 */
		switch (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0)) {
		case 0: // ANUAL

			cargarValoresAnualesPrevision();
			break;

		case 1: // MENSUAL

			cargarValoresMensualesPrevision();
			break;

		case 2: // DIARIO

			cargarValoresDiariosPrevision();
			break;
		}

	}

	/*
	 * Método que me cargar los Arrays de valores de prevision para mostrar la
	 * grafica
	 */
	private void cargarValoresAnualesPrevision() {

		int enteroFechaInicial;
		int enteroFechaUmbralHistorico; // en el histórico sera el día de ayer
		int enteroFechaUmbralPrevision; // en el histórico sera el día de hoy
		int enteroFechaFinal;
		Integer integerFechaInicial;
		Integer integerFechaUmbralHistorico;
		Integer integerFechaUmbralPrevision;
		Integer integerFechaFinal;
		// Indice que va a recorrer las listas de ingresos y gastos
		int indiceAux = 0;
		// Variables para los valores mínimos y máximos
		Double max = Double.MIN_VALUE;
		Double min = Double.MAX_VALUE;

		enteroFechaInicial = obtenerEnteroFechaPrevision(ENTERO_FECHA_2_ANYOS_ANTES);
		integerFechaInicial = Integer.valueOf(("" + enteroFechaInicial)
				.substring(0, 4));
		enteroFechaUmbralHistorico = obtenerEnteroFechaPrevision(ENTERO_FECHA_DIA_DE_AYER);
		integerFechaUmbralHistorico = Integer
				.valueOf(("" + enteroFechaUmbralHistorico).substring(0, 4));
		enteroFechaUmbralPrevision = obtenerEnteroFechaPrevision(ENTERO_FECHA_DIA_DE_HOY);
		integerFechaUmbralPrevision = Integer
				.valueOf(("" + enteroFechaUmbralHistorico).substring(0, 4));
		enteroFechaFinal = obtenerEnteroFechaPrevision(ENTERO_FECHA_2_ANYOS_DESPUES);
		integerFechaFinal = Integer.valueOf(("" + enteroFechaFinal).substring(
				0, 4));

		// primero obtengo los valores históricos a mostrar
		// el último valor es el historico del año actual
		listadoValoresIngresos = dba.listadoValoresAnualesGraficas(
				enteroFechaInicial, enteroFechaUmbralHistorico, "ingreso");
		listadoValoresGastos = dba.listadoValoresAnualesGraficas(
				enteroFechaInicial, enteroFechaUmbralHistorico, "gasto");
		// ahora obtengo las valores de previsión a mostrar
		// el primer valor es la previsión del año actual
		listadoValoresIngresosPrevision = dba.listadoValoresAnualesGraficas(
				enteroFechaUmbralPrevision, enteroFechaFinal, "ingreso");
		listadoValoresGastosPrevision = dba.listadoValoresAnualesGraficas(
				enteroFechaUmbralPrevision, enteroFechaFinal, "gasto");

		// Obtengo los acumulados históricos para calcular el balance
		acumIngresosPrevision = dba.obtenerAcumulados(
				enteroFechaUmbralHistorico, "ingreso");
		acumGastosPrevision = dba.obtenerAcumulados(enteroFechaUmbralHistorico,
				"gasto");
		acumBalancePrevision = acumIngresosPrevision - acumGastosPrevision;

		// INGRESOS HISTORICOS
		// inserto en el Arrays ingresos los valores historicos
		if (!listadoValoresIngresos.isEmpty()) {
			// Relleno los valores de los ingresos historicos para mostrarlo en
			// la
			// grafica
			for (int i = integerFechaInicial.intValue(); i <= integerFechaUmbralHistorico
					.intValue(); i++) {

				if (indiceAux < listadoValoresIngresos.size()) {

					if (Integer.valueOf(
							listadoValoresIngresos.get(indiceAux).getFecha())
							.intValue() == i) {

						ingresos.add(listadoValoresIngresos.get(indiceAux)
								.getCantidad());

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
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					ingresos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}

		} else {
			// Al estar vacío la lista de ingresos relleno los valores
			// a cero
			for (int i = integerFechaInicial.intValue(); i <= integerFechaUmbralHistorico
					.intValue(); i++) {
				ingresos.add(0.0);
			}
		}

		indiceAux = 0;
		// INGRESOS PREVISION
		// inserto en el Arrays ingresos los valores de
		if (!listadoValoresIngresosPrevision.isEmpty()) {
			// Relleno los valores de los ingresos para mostrarlo en la
			// grafica
			for (int i = integerFechaUmbralPrevision.intValue(); i <= integerFechaFinal
					.intValue(); i++) {

				if (indiceAux < listadoValoresIngresosPrevision.size()) {
					if (Integer.valueOf(
							listadoValoresIngresosPrevision.get(indiceAux)
									.getFecha()).intValue() == i) {

						ingresos.add(listadoValoresIngresosPrevision.get(
								indiceAux).getCantidad());

						if (listadoValoresIngresosPrevision.get(indiceAux)
								.getCantidad() >= max) {
							max = listadoValoresIngresosPrevision
									.get(indiceAux).getCantidad();
						}

						if (listadoValoresIngresosPrevision.get(indiceAux)
								.getCantidad() <= min) {
							min = listadoValoresIngresosPrevision
									.get(indiceAux).getCantidad();
						}

						indiceAux++;
					} else {
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					ingresos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			// Al no haber valores de prevision le inserto 0 en la prevision
			// del año actual y el siguiente
			for (int i = integerFechaUmbralPrevision.intValue(); i <= integerFechaFinal
					.intValue(); i++) {
				ingresos.add(0.0);
			}
		}

		// GASTOS HISTORICOS
		// Inicializo indiceAux a 0 para utilizarlo de nuevo
		indiceAux = 0;

		if (!listadoValoresGastos.isEmpty()) {
			// Relleno los valores de los gastos para mostrarlos en la
			// grafica
			for (int i = integerFechaInicial.intValue(); i <= integerFechaUmbralHistorico
					.intValue(); i++) {

				if (indiceAux < listadoValoresGastos.size()) {

					if (Integer.valueOf(
							listadoValoresGastos.get(indiceAux).getFecha())
							.intValue() == i) {

						gastos.add(listadoValoresGastos.get(indiceAux)
								.getCantidad());

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
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					gastos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			for (int i = integerFechaInicial.intValue(); i <= integerFechaUmbralHistorico
					.intValue(); i++) {
				gastos.add(0.0);
			}
		}

		// GASTOS PREVISION
		indiceAux = 0;
		// inserto en el Arrays gastos los valores de prevision
		if (!listadoValoresGastosPrevision.isEmpty()) {
			// Relleno los valores de los gastos para mostrarlo en la
			// grafica
			for (int i = integerFechaUmbralPrevision.intValue(); i <= integerFechaFinal
					.intValue(); i++) {

				if (indiceAux < listadoValoresGastosPrevision.size()) {

					if (Integer.valueOf(
							listadoValoresGastosPrevision.get(indiceAux)
									.getFecha()).intValue() == i) {

						gastos.add(listadoValoresGastosPrevision.get(indiceAux)
								.getCantidad());

						if (listadoValoresGastosPrevision.get(indiceAux)
								.getCantidad() >= max) {
							max = listadoValoresGastosPrevision.get(indiceAux)
									.getCantidad();
						}

						if (listadoValoresGastosPrevision.get(indiceAux)
								.getCantidad() <= min) {
							min = listadoValoresGastosPrevision.get(indiceAux)
									.getCantidad();
						}

						indiceAux++;
					} else {
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					gastos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			// Al no haber valores de prevision le inserto 0 en la prevision
			// del año actual y el siguiente
			for (int i = integerFechaUmbralPrevision.intValue(); i <= integerFechaFinal
					.intValue(); i++) {
				gastos.add(0.0);
			}
		}

		if (leyendaEjeX == null) {
			leyendaEjeX = new ArrayList<String>();
		} else {
			leyendaEjeX.clear();
		}

		// LEYENDA EJEX
		// Me creo el ArrayList para la leyenda del ejeX Historico
		for (int i = integerFechaInicial.intValue(); i < integerFechaUmbralHistorico
				.intValue(); i++) {
			leyendaEjeX.add("" + i);
		}
		// Dibujo los datos umbrales que parten el año en dos
		// Historico Año actual : Previsión Año Actual
		leyendaEjeX.add(integerFechaUmbralHistorico.intValue() + "-Hist.");
		leyendaEjeX.add(integerFechaUmbralPrevision.intValue() + "-Prev.");
		// Me creo el ArrayList para la leyenda del ejeX Previsión
		for (int i = integerFechaUmbralPrevision.intValue() + 1; i <= integerFechaFinal
				.intValue(); i++) {
			leyendaEjeX.add("" + i);
		}

		// leyendaEjeX.add("" + integerFechaFinal.intValue());

		// BALANCE
		// Relleno el balance historico a partir de los ingresos y gastos
		// historicos
		for (int i = 0; i < NUMERO_ANYOS_HISTORICOS_PREVISON; i++) {
			balance.add(ingresos.get(i) - gastos.get(i));
		}

		// Relleno el balance de prevision a partir de los ingresos y gastos
		// acumulados historicos y los valores futuros
		for (int i = NUMERO_ANYOS_HISTORICOS_PREVISON; i < (NUMERO_ANYOS_HISTORICOS_PREVISON + NUMERO_ANYOS_FUTUROS_PREVISON); i++) {
			acumBalancePrevision = acumBalancePrevision
					+ (ingresos.get(i) - gastos.get(i));
			balance.add(acumBalancePrevision);
		}

		// Veo si hay minimo y maximo en el balance
		for (int i = 0; i < balance.size(); i++) {

			if (balance.get(i) >= max) {
				max = balance.get(i);
			}

			if (balance.get(i) <= min) {
				min = balance.get(i);
			}

		}

		// Actualizo los mínimos y máximos de la gráfica
		maxCantidad = max.intValue();
		minCantidad = min.intValue();

	}

	/*
	 * Método para cargar los valores mensuales de previsión en los ArrayList
	 * para mostrar en la Gráfica
	 */
	private void cargarValoresMensualesPrevision() {

		int enteroFechaInicial;
		int enteroFechaUmbralHistorico; // en el histórico sera el día de ayer
		int enteroFechaUmbralPrevision; // en el histórico sera el día de hoy
		int enteroFechaFinal;

		// Indice que va a recorrer las listas de ingresos y gastos
		int indiceAux = 0;
		// Variables para los valores mínimos y máximos
		Double max = Double.MIN_VALUE;
		Double min = Double.MAX_VALUE;

		enteroFechaInicial = obtenerEnteroFechaPrevision(ENTERO_FECHA_3_MESES_ANTES);
		enteroFechaUmbralHistorico = obtenerEnteroFechaPrevision(ENTERO_FECHA_DIA_DE_AYER);
		enteroFechaUmbralPrevision = obtenerEnteroFechaPrevision(ENTERO_FECHA_DIA_DE_HOY);
		enteroFechaFinal = obtenerEnteroFechaPrevision(ENTERO_FECHA_7_MESES_DESPUES);

		// primero obtengo los valores históricos a mostrar
		// el último valor es el historico del año actual
		listadoValoresIngresos = dba.listadoValoresMensualesGraficas(
				enteroFechaInicial, enteroFechaUmbralHistorico, "ingreso");
		listadoValoresGastos = dba.listadoValoresMensualesGraficas(
				enteroFechaInicial, enteroFechaUmbralHistorico, "gasto");
		// ahora obtengo las valores de previsión a mostrar
		// el primer valor es la previsión del año actual
		listadoValoresIngresosPrevision = dba.listadoValoresMensualesGraficas(
				enteroFechaUmbralPrevision, enteroFechaFinal, "ingreso");
		listadoValoresGastosPrevision = dba.listadoValoresMensualesGraficas(
				enteroFechaUmbralPrevision, enteroFechaFinal, "gasto");

		// Obtengo los acumulados históricos para calcular el balance
		acumIngresosPrevision = dba.obtenerAcumulados(
				enteroFechaUmbralHistorico, "ingreso");
		acumGastosPrevision = dba.obtenerAcumulados(enteroFechaUmbralHistorico,
				"gasto");
		acumBalancePrevision = acumIngresosPrevision - acumGastosPrevision;

		ArrayList<Integer> rangoFechasHistorico;
		ArrayList<Integer> rangoFechasPrevision;

		rangoFechasHistorico = obtenerRangoFechasMensuales(enteroFechaInicial,
				enteroFechaUmbralHistorico, 1);
		rangoFechasPrevision = obtenerRangoFechasMensuales(
				enteroFechaUmbralPrevision, enteroFechaFinal, 2);

		// INGRESOS HISTORICOS
		// Relleno primero los valores hostóricos para mostrar en la gráfica
		if (!listadoValoresIngresos.isEmpty()) {
			// Relleno los valores de los ingresos para mostrarlo en la
			// grafica
			for (int i = 0; i < rangoFechasHistorico.size(); i++) {

				if (indiceAux < listadoValoresIngresos.size()) {
					if (Integer.valueOf(
							listadoValoresIngresos.get(indiceAux).getFecha())
							.intValue() == rangoFechasHistorico.get(i)
							.intValue()) {

						ingresos.add(listadoValoresIngresos.get(indiceAux)
								.getCantidad());

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
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					ingresos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			for (int i = 0; i < rangoFechasHistorico.size(); i++) {
				ingresos.add(0.0);
			}
		}

		// INGRESOS PREVISIÓN
		// Relleno los valores de Previsión
		indiceAux = 0;
		// inserto en el Arrays ingresos los valores de prevision
		if (!listadoValoresIngresosPrevision.isEmpty()) {
			// Relleno los valores de los ingresos para mostrarlo en la
			// grafica
			for (int i = 0; i < rangoFechasPrevision.size(); i++) {

				if (indiceAux < listadoValoresIngresosPrevision.size()) {
					if (Integer.valueOf(
							listadoValoresIngresosPrevision.get(indiceAux)
									.getFecha()).intValue() == rangoFechasPrevision
							.get(i).intValue()) {

						ingresos.add(listadoValoresIngresosPrevision.get(
								indiceAux).getCantidad());

						if (listadoValoresIngresosPrevision.get(indiceAux)
								.getCantidad() >= max) {
							max = listadoValoresIngresosPrevision
									.get(indiceAux).getCantidad();
						}

						if (listadoValoresIngresosPrevision.get(indiceAux)
								.getCantidad() <= min) {
							min = listadoValoresIngresosPrevision
									.get(indiceAux).getCantidad();
						}

						indiceAux++;
					} else {
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					ingresos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			// Al no haber más valores de prevision le inserto 0 en la
			// prevision
			// del mes actual y los 7 siguientes
			for (int i = rangoFechasHistorico.size(); i < (rangoFechasHistorico
					.size() + rangoFechasPrevision.size()); i++) {
				ingresos.add(0.0);
			}
		}

		// Inicializo indiceAux a 0 para utilizarlo de nuevo
		indiceAux = 0;

		// GASTOS HISTORICOS
		// Primero inserto los valores históricos de los gastos
		if (!listadoValoresGastos.isEmpty()) {
			// Relleno los valores de los gastos para mostrarlos en la
			// grafica
			for (int i = 0; i < rangoFechasHistorico.size(); i++) {

				if (indiceAux < listadoValoresGastos.size()) {
					if (Integer.valueOf(
							listadoValoresGastos.get(indiceAux).getFecha())
							.intValue() == rangoFechasHistorico.get(i)
							.intValue()) {

						gastos.add(listadoValoresGastos.get(indiceAux)
								.getCantidad());

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
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					gastos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			for (int i = 0; i < rangoFechasHistorico.size(); i++) {
				gastos.add(0.0);
			}
		}

		// GASTOS PREVISIÓN
		// Relleno los valores de Previsión
		indiceAux = 0;
		// inserto en el Arrays gastos los valores de prevision
		if (!listadoValoresGastosPrevision.isEmpty()) {
			// Relleno los valores de los gastos para mostrarlo en la
			// grafica
			for (int i = 0; i < rangoFechasPrevision.size(); i++) {

				if (indiceAux < listadoValoresGastosPrevision.size()) {
					if (Integer.valueOf(
							listadoValoresGastosPrevision.get(indiceAux)
									.getFecha()).intValue() == rangoFechasPrevision
							.get(i).intValue()) {

						gastos.add(listadoValoresGastosPrevision.get(indiceAux)
								.getCantidad());

						if (listadoValoresGastosPrevision.get(indiceAux)
								.getCantidad() >= max) {
							max = listadoValoresGastosPrevision.get(indiceAux)
									.getCantidad();
						}

						if (listadoValoresGastosPrevision.get(indiceAux)
								.getCantidad() <= min) {
							min = listadoValoresGastosPrevision.get(indiceAux)
									.getCantidad();
						}

						indiceAux++;
					} else {
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					gastos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
			}
		} else {
			// Al no haber más valores de prevision le inserto 0 en la
			// prevision
			// del mes actual y los 7 siguientes
			for (int i = rangoFechasHistorico.size(); i < (rangoFechasHistorico
					.size() + rangoFechasPrevision.size()); i++) {
				gastos.add(0.0);
			}
		}

		// BALANCE
		if (leyendaEjeX == null) {
			leyendaEjeX = new ArrayList<String>();
		} else {
			leyendaEjeX.clear();
		}

		// Me creo el ArrayList para la parte de Histórico
		// de la leyenda del ejeX for (int i = 0; i <
		// rangoFechasHistorico.size() - 1; i++) {
		for (int i = 0; i < NUMERO_MESES_HISTORICOS_PREVISON - 1; i++) {
			// leyendaEjeX.add("" + i);
			leyendaEjeX.add(obtenerLeyendaMensualEjeX(rangoFechasHistorico
					.get(i)));
		}
		// inserto la parte histórica del mes actual
		leyendaEjeX.add("Hist.");
		// inserto la parte de previsión del mes actual
		leyendaEjeX.add("Prev.");
		// Me creo el ArrayList para la parte de Previsión
		// de la leyenda del ejeX
		for (int i = 1; i < NUMERO_MESES_FUTUROS_PREVISON; i++) {
			// leyendaEjeX.add("" + i);
			leyendaEjeX.add(obtenerLeyendaMensualEjeX(rangoFechasPrevision
					.get(i)));
		}

		// BALANCE
		// Relleno el balance historico a partir de los ingresos y gastos
		// historicos
		for (int i = 0; i < rangoFechasHistorico.size(); i++) {
			balance.add(ingresos.get(i) - gastos.get(i));
		}

		// Relleno el balance de prevision a partir de los ingresos y gastos
		// acumulados historicos y los valores futuros
		for (int i = rangoFechasHistorico.size(); i < rangoFechasHistorico
				.size() + rangoFechasPrevision.size(); i++) {
			acumBalancePrevision = acumBalancePrevision
					+ (ingresos.get(i) - gastos.get(i));
			balance.add(acumBalancePrevision);
		}

		// Veo si hay minimo y maximo en el balance
		for (int i = 0; i < balance.size(); i++) {

			if (balance.get(i) >= max) {
				max = balance.get(i);
			}

			if (balance.get(i) <= min) {
				min = balance.get(i);
			}
		}

		// Actualizo los mínimos y máximos de la gráfica
		maxCantidad = max.intValue();
		minCantidad = min.intValue();

	}

	/*
	 * Método que me carga en los ArrayList para mostrar la gráfica los valores
	 * de previsión diarios
	 */
	private void cargarValoresDiariosPrevision() {

		int enteroFechaInicial;
		int enteroFechaUmbralHistorico;
		int enteroFechaFinal;
		// Indice que va a recorrer las listas de ingresos y gastos
		int indiceAux = 0;
		// Variables para los valores mínimos y máximos
		Double max = Double.MIN_VALUE;
		Double min = Double.MAX_VALUE;

		enteroFechaInicial = obtenerEnteroFechaPrevision(ENTERO_FECHA_3_DIAS_ANTES);
		enteroFechaUmbralHistorico = obtenerEnteroFechaPrevision(ENTERO_FECHA_DIA_DE_AYER);
		enteroFechaFinal = obtenerEnteroFechaPrevision(ENTERO_FECHA_10_DIAS_DESPUES);

		listadoValoresIngresos = dba.listadoValoresDiariosGraficas(
				enteroFechaInicial, enteroFechaFinal, "ingreso");
		listadoValoresGastos = dba.listadoValoresDiariosGraficas(
				enteroFechaInicial, enteroFechaFinal, "gasto");

		// Obtengo los acumulados históricos para calcular el balance
		acumIngresosPrevision = dba.obtenerAcumulados(
				enteroFechaUmbralHistorico, "ingreso");
		acumGastosPrevision = dba.obtenerAcumulados(enteroFechaUmbralHistorico,
				"gasto");
		acumBalancePrevision = acumIngresosPrevision - acumGastosPrevision;

		int dia = enteroFechaInicial;

		// INGRESOS
		if (!listadoValoresIngresos.isEmpty()) {
			// Relleno los valores de los ingresos para mostrarlo en la
			// grafica los 14 días que queremos mostrar de INGRESOS
			for (int i = 0; i < NUMERO_DIAS_HISTORICOS_PREVISON
					+ NUMERO_DIAS_FUTUROS_PREVISON; i++) {

				if (indiceAux < listadoValoresIngresos.size()) {

					if (Integer.valueOf(
							listadoValoresIngresos.get(indiceAux).getFecha())
							.intValue() == dia) {

						ingresos.add(listadoValoresIngresos.get(indiceAux)
								.getCantidad());

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
						ingresos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					ingresos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
				dia = obtenerDiaSiguiente(dia);
			}
		} else {
			for (int i = 0; i < NUMERO_DIAS_HISTORICOS_PREVISON
					+ NUMERO_DIAS_FUTUROS_PREVISON; i++) {
				ingresos.add(0.0);
			}
		}

		// Inicializo indiceAux a 0 y dia para utilizarlos de nuevo
		dia = enteroFechaInicial;
		indiceAux = 0;

		// GASTOS
		if (!listadoValoresGastos.isEmpty()) {
			// Relleno los valores de los gastos para mostrarlo en la
			// grafica los 14 días que queremos mostrar de GASTOS
			for (int i = 0; i < NUMERO_DIAS_HISTORICOS_PREVISON
					+ NUMERO_DIAS_FUTUROS_PREVISON; i++) {

				if (indiceAux < listadoValoresGastos.size()) {

					if (Integer.valueOf(
							listadoValoresGastos.get(indiceAux).getFecha())
							.intValue() == dia) {

						gastos.add(listadoValoresGastos.get(indiceAux)
								.getCantidad());

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
						gastos.add(0.0);
						if (0.0 > max) {
							max = 0.0;
						}
						if (0.0 < min) {
							min = 0.0;
						}
					}
				} else {
					gastos.add(0.0);
					if (0.0 > max) {
						max = 0.0;
					}
					if (0.0 < min) {
						min = 0.0;
					}
				}
				dia = obtenerDiaSiguiente(dia);
			}
		} else {
			for (int i = 0; i < NUMERO_DIAS_HISTORICOS_PREVISON
					+ NUMERO_DIAS_FUTUROS_PREVISON; i++) {
				gastos.add(0.0);
			}
		}

		if (leyendaEjeX == null) {
			leyendaEjeX = new ArrayList<String>();
		} else {
			leyendaEjeX.clear();
		}

		// Inicializo dia para utilizarla de nuevo
		dia = enteroFechaInicial;

		// Me creo el ArrayList para la leyenda del ejeX
		for (int i = 0; i < NUMERO_DIAS_HISTORICOS_PREVISON
				+ NUMERO_DIAS_FUTUROS_PREVISON; i++) {
			leyendaEjeX.add(obtenerLeyendaDiariaEjeX(dia));
			dia = obtenerDiaSiguiente(dia);
		}

		// Relleno el balance a partir de los ingresos y gastos
		// Balance historico
		for (int i = 0; i < NUMERO_DIAS_HISTORICOS_PREVISON; i++) {
			balance.add(ingresos.get(i) - gastos.get(i));
		}
		// Balance Previsión
		for (int i = NUMERO_DIAS_HISTORICOS_PREVISON; i < NUMERO_DIAS_HISTORICOS_PREVISON
				+ NUMERO_DIAS_FUTUROS_PREVISON; i++) {
			acumBalancePrevision = acumBalancePrevision
					+ (ingresos.get(i) - gastos.get(i));
			balance.add(acumBalancePrevision);
		}

		// Veo si hay minimo y maximo en el balance
		for (int i = 0; i < balance.size(); i++) {

			if (balance.get(i) >= max) {
				max = balance.get(i);
			}

			if (balance.get(i) <= min) {
				min = balance.get(i);
			}
		}

		// Actualizo los valores mínimos y máximos de la gráfica
		maxCantidad = max.intValue();
		minCantidad = min.intValue();

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
			rango.add(Integer.valueOf(("" + enteroFechaInicial).substring(0, 6)));

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
		case 1: // Hiostórico de la Previsión mensual
			/*
			 * Al ser previsión lo que ha elegido el usuario lo que devuelvo es
			 * un array de integer de 4 items, la parte previsión del mes actual
			 * y los 3 meses anteriores
			 */
			rango.add(Integer.valueOf(("" + enteroFechaInicial).substring(0, 6)));

			for (int i = 0; i < NUMERO_MESES_HISTORICOS_PREVISON - 1; i++) {

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
		case 2: // Previsión mensual
			/*
			 * Al ser previsión lo que ha elegido el usuario lo que devuelvo es
			 * un array de integer de 8 items, la parte previsión del mes actual
			 * y los 7 meses siguientes
			 */
			rango.add(Integer.valueOf(("" + enteroFechaInicial).substring(0, 6)));

			for (int i = 0; i < NUMERO_MESES_FUTUROS_PREVISON - 1; i++) {

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

		}

		return rango;
	}

	/*
	 * Método que incrementa en 1 DÍA la fecha que estoy pasando por parámetro
	 */
	private int obtenerDiaSiguiente(int dia) {

		String fecha = new String("" + dia);
		String year = fecha.substring(0, 4);
		String month = fecha.substring(4, 6);
		String day = fecha.substring(6);
		int y = Integer.valueOf(year).intValue();
		int m = Integer.valueOf(month).intValue();
		int d = Integer.valueOf(day).intValue();

		// Obtengo la variable Calendar
		Calendar c = Calendar.getInstance();
		// Inicio la variable Calendar con la fecha que tengo actualmente
		c.set(y, m - 1, d);
		// le sumo 1 día
		c.add(Calendar.DATE, 1);
		/*
		 * Recupero la Fecha incrementada en 1 DÍA de la variable Calendar Al
		 * mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0 Si
		 * el mes solo tiene un dígito le pongo un cero delante
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
		// Actualizo el valor de la fecha
		return (Integer.valueOf(fecha)).intValue();

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
			fecha = "Ene.";
			break;
		case 2:
			fecha = "Feb.";
			break;
		case 3:
			fecha = "Mar.";
			break;
		case 4:
			fecha = "Abr.";
			break;
		case 5:
			fecha = "May.";
			break;
		case 6:
			fecha = "Jun.";
			break;
		case 7:
			fecha = "Jul.";
			break;
		case 8:
			fecha = "Ago.";
			break;
		case 9:
			fecha = "Sep.";
			break;
		case 10:
			fecha = "Oct.";
			break;
		case 11:
			fecha = "Nov.";
			break;
		case 12:
			fecha = "Dic.";
			break;
		}

		fecha += Integer.valueOf(fechaComprobacion.substring(2, 4)).intValue();

		return fecha;
	}

	/*
	 * Método que me formatea la fecha que le paso como entero al patrón
	 * dd/mm/yy
	 */
	private String obtenerLeyendaDiariaEjeX(int fecha) {

		String cadenaFecha = "" + fecha;
		String fechaFormateada;

		fechaFormateada = cadenaFecha.substring(6) + "/"
				+ cadenaFecha.substring(4, 6) + "/"
				+ cadenaFecha.substring(2, 4);

		return fechaFormateada;
	}

	/*
	 * Método donde le asigno las series a la gráfica y defino el color de las
	 * líneas y puntos en función del archivo de configuración
	 */
	private void prepararLineasGraficas() {

		LineAndPointFormatter lapfIngresos;
		LineAndPointFormatter lapfGastos;
		LineAndPointFormatter lapfBalance;

		// Serie de INGRESOS
		if (!ingresos.isEmpty()) {

			XYSeries seriesIngresos = new SimpleXYSeries(ingresos,
					SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "INGRESOS");

			if (preferenceConfiguracionPrivate.getBoolean(
					singleton_csp.KEY_CBPLINEINGRESOS, false) == true) {

				if (preferenceConfiguracionPrivate.getBoolean(
						singleton_csp.KEY_CBPVALUESINGRESOS, false) == true) {

					lapfIngresos = new LineAndPointFormatter(getResources()
							.getColor(R.color.LightGreen), // Color de la línea
							getResources().getColor(R.color.verde), // Color del
																	// punto
							null, // Color del relleno
							new PointLabelFormatter(Color.BLACK)); // Color de
																	// los
																	// valores
																	// del
																	// punto

				} else {

					lapfIngresos = new LineAndPointFormatter(getResources()
							.getColor(R.color.LightGreen), // Color de la línea
							getResources().getColor(R.color.verde), // Color del
																	// punto
							null, // Color del relleno
							(PointLabelFormatter) null); // Color de los valores
															// del
															// punto

				}

				mySimpleXYPlot.addSeries(seriesIngresos, lapfIngresos);

			}
		}

		// Serie de GASTOS
		if (!gastos.isEmpty()) {
			XYSeries seriesGastos = new SimpleXYSeries(gastos,
					SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "GASTOS");

			if (preferenceConfiguracionPrivate.getBoolean(
					singleton_csp.KEY_CBPLINEGASTOS, false) == true) {

				if (preferenceConfiguracionPrivate.getBoolean(
						singleton_csp.KEY_CBPVALUESGASTOS, false) == true) {

					lapfGastos = new LineAndPointFormatter(getResources()
							.getColor(R.color.LightRed), // Color de la línea
							getResources().getColor(R.color.rojo), // Color del
																	// punto
							null, // Color del relleno
							new PointLabelFormatter(Color.BLACK)); // Color de
																	// los
																	// valores
																	// del
																	// punto

				} else {

					lapfGastos = new LineAndPointFormatter(getResources()
							.getColor(R.color.LightRed), // Color de la línea
							getResources().getColor(R.color.rojo), // Color del
																	// punto
							null, // Color del relleno
							(PointLabelFormatter) null); // Color de los valores
															// del
															// punto

				}

				mySimpleXYPlot.addSeries(seriesGastos, lapfGastos);

			}
		}

		// Serie de BALANCE
		if (!ingresos.isEmpty() && !gastos.isEmpty()) {
			XYSeries seriesBalance = new SimpleXYSeries(balance,
					SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "BALANCE");

			if (preferenceConfiguracionPrivate.getBoolean(
					singleton_csp.KEY_CBPLINEBALANCE, false) == true) {

				if (preferenceConfiguracionPrivate.getBoolean(
						singleton_csp.KEY_CBPVALUESBALANCE, false) == true) {

					lapfBalance = new LineAndPointFormatter(getResources()
							.getColor(R.color.CalendarioAzulCabeceraEnd), // Color
																			// de
																			// la
																			// línea
							getResources().getColor(R.color.azul), // Color del
																	// punto
							null, // Color del relleno
							new PointLabelFormatter(Color.BLACK)); // Color de
																	// los
																	// valores
																	// del
																	// punto

				} else {

					lapfBalance = new LineAndPointFormatter(getResources()
							.getColor(R.color.CalendarioAzulCabeceraEnd), // Color
																			// de
																			// la
																			// línea
							getResources().getColor(R.color.azul), // Color del
																	// punto
							null, // Color del relleno
							(PointLabelFormatter) null); // Color de los valores
															// del
															// punto

				}

				mySimpleXYPlot.addSeries(seriesBalance, lapfBalance);
			}
		}

	}

	/*
	 * Método para dibujar la gráfica
	 */
	private void dibujarGrafica() {

		mySimpleXYPlot.redraw();

		// Set of internal variables for keeping track of the boundaries
		// Calcula los máximos y mínimos para dibujar la gráfica
		mySimpleXYPlot.calculateMinMaxVals();
		minXY = new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),
				mySimpleXYPlot.getCalculatedMinY().floatValue());
		maxXY = new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),
				mySimpleXYPlot.getCalculatedMaxY().floatValue());

	}

	/*
	 * Método que me devuelve en un entero la fecha que quiero pasada por
	 * parámetro para los valores Históricos
	 */
	private int obtenerEnteroFechaHistorico(int tipoFecha) {

		String fecha;
		String month;
		String day;
		int entero_fecha;
		boolean perteneceMesActual;

		Calendar c = Calendar.getInstance();

		if ((preferenceConfiguracionPrivate.getInt(singleton_csp.KEY_PYEARS, 0) == c
				.get(Calendar.YEAR) && c.get(Calendar.MONTH) == (preferenceConfiguracionPrivate
				.getInt(singleton_csp.KEY_PMONTHS, 0) - 1))) {
			perteneceMesActual = true;
		} else {
			// Situo la fecha según los valores del archivo de configuración de
			// las Gráficas
			c.set(Calendar.DAY_OF_MONTH, preferenceConfiguracionPrivate.getInt(
					singleton_csp.KEY_PDAY, 0));
			c.set(Calendar.MONTH, preferenceConfiguracionPrivate.getInt(
					singleton_csp.KEY_PMONTHS, 0) - 1);
			c.set(Calendar.YEAR, preferenceConfiguracionPrivate.getInt(
					singleton_csp.KEY_PYEARS, 0));

			perteneceMesActual = false;
		}

		switch (tipoFecha) {

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
		case ENTERO_FECHA_15_DIAS_ANTES:
			// Obtengo de la variable Calendar la fecha de hace 31 días antes
			c.add(Calendar.DATE, -15);
			break;
		case ENTERO_FECHA_DIA_DE_AYER:
			if (perteneceMesActual) {
				c.add(Calendar.DATE, -1);
			}
			break;
		case ENTERO_FECHA_10_DIAS_ANTES:
			c.add(Calendar.DATE, -10);
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

	/*
	 * Método que me devuelve en un entero la fecha que quiero pasada por
	 * parámetro para los valores de Previsión
	 */
	private int obtenerEnteroFechaPrevision(int tipoFecha) {

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
		case ENTERO_FECHA_3_MESES_ANTES:
			// Obtengo de la variable Calendar la fecha del primer día de hace 5
			// meses
			// antes
			c.add(Calendar.MONTH, -3);
			c.set(Calendar.DATE, 1);
			break;
		case ENTERO_FECHA_3_DIAS_ANTES:
			// Obtengo de la variable Calendar la fecha de hace 31 días antes
			c.add(Calendar.DATE, -3);
			break;
		case ENTERO_FECHA_2_ANYOS_DESPUES:
			// Obtengo de la variable Calendar la fecha del último día de 2
			// años
			// después
			c.add(Calendar.YEAR, 3);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);
			break;
		case ENTERO_FECHA_2_ANYOS_ANTES:
			// Obtengo de la variable Calendar la fecha del primer día de hace 2
			// años
			// antes
			c.add(Calendar.YEAR, -2);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			break;
		case ENTERO_FECHA_DIA_DE_AYER:
			c.add(Calendar.DATE, -1);
			break;
		case ENTERO_FECHA_7_MESES_DESPUES:
			c.add(Calendar.MONTH, 8);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);
			break;
		case ENTERO_FECHA_10_DIAS_DESPUES:
			c.add(Calendar.DATE, 11);
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
	 * Método para configurar el diseño de la Gráfica
	 */
	private void configurarGrafica() {

		// Título de la Gráfica
		mySimpleXYPlot.setTitle("Balance Económico");
		// Título ejeY
		mySimpleXYPlot.setRangeLabel("Catindad (€)");
		// Título ejeX
		mySimpleXYPlot.setDomainLabel("");

		// Remove legend
		// mySimpleXYPlot.getLayoutManager().remove(mySimpleXYPlot.getLegendWidget());
		mySimpleXYPlot.getLayoutManager().remove(
				mySimpleXYPlot.getDomainLabelWidget());
		mySimpleXYPlot.getLayoutManager().remove(
				mySimpleXYPlot.getRangeLabelWidget());
		// mySimpleXYPlot.getLayoutManager().remove(mySimpleXYPlot.getTitleWidget());

		mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
		mySimpleXYPlot.setPlotMargins(0, 0, 0, 0);
		mySimpleXYPlot.setPlotPadding(0, 0, 0, 0);
		mySimpleXYPlot.setGridPadding(20, 10, 20, 0); // left,top,right,bottom

		// mySimpleXYPlot.setBackgroundColor(Color.WHITE);
		/*
		 * mySimpleXYPlot.position( mySimpleXYPlot.getGraphWidget(), 0,
		 * XLayoutStyle.RELATIVE_TO_LEFT, 0, YLayoutStyle.RELATIVE_TO_CENTER,
		 * AnchorPosition.LEFT_MIDDLE);
		 */

		// mySimpleXYPlot.getLegendWidget().setMargins(5, 5, 5, 5);
		// mySimpleXYPlot.getLegendWidget().setPadding(10, 10, 10, 10);
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		mySimpleXYPlot.getLegendWidget().setTextPaint(p);

		mySimpleXYPlot.getGraphWidget().getBackgroundPaint()
				.setColor(Color.WHITE);
		mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint()
				.setColor(Color.WHITE);

		// Me dibuja los valores del ejeX y ejeY
		mySimpleXYPlot.getGraphWidget().getDomainLabelPaint()
				.setColor(Color.BLACK);
		mySimpleXYPlot.getGraphWidget().getRangeLabelPaint()
				.setColor(Color.BLACK);

		mySimpleXYPlot.getGraphWidget().getDomainOriginLabelPaint()
				.setColor(Color.BLACK);
		// Lineas origen del ejeY y ejeX
		mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint()
				.setColor(Color.BLACK);
		mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint()
				.setColor(Color.BLACK);
		// Líneas centrales del gráfico
		mySimpleXYPlot.getGraphWidget().getGridDomainLinePaint()
				.setColor(getResources().getColor(R.color.gris_claro));
		mySimpleXYPlot.getGraphWidget().getGridRangeLinePaint()
				.setColor(getResources().getColor(R.color.gris_claro));

		// Rango de valores ejeX a mostrar
		if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 0
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 0) {
			// Entra aquí si es Historico y Anual
			// mySimpleXYPlot.setDomainStep(XYStepMode.SUBDIVIDE,leyendaEjeX.size());

			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()) {
				minCantidad = 0;
				maxCantidad = 100;
			}

			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 1
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 0) {
			// Entra aquí si es Historico y Mensual
			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()) {
				minCantidad = 0;
				maxCantidad = 100;
			}

			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 2
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 0) {
			// Entra aquí si es Historico y Diario
			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()) {
				minCantidad = 0;
				maxCantidad = 100;
			}

			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 3);

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 0
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 1) {
			// Entra aquí si es Con Prevision y Anual
			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()
					&& listadoValoresIngresosPrevision.isEmpty()
					&& listadoValoresGastosPrevision.isEmpty()) {
				minCantidad = 0;
				maxCantidad = 100;
			}

			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 1
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 1) {
			// Entra aquí si es Con Prevision y Mensual
			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()
					&& listadoValoresIngresosPrevision.isEmpty()
					&& listadoValoresGastosPrevision.isEmpty()) {
				minCantidad = 0;
				maxCantidad = 100;
			}

			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);

		} else if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0) == 2
				&& preferenceConfiguracionPrivate.getInt(
						singleton_csp.KEY_LPVALORESGRAFICA, 0) == 1) {
			// Entra aquí si es Con Prevision y Diario
			if (listadoValoresIngresos.isEmpty()
					&& listadoValoresGastos.isEmpty()
					&& listadoValoresIngresosPrevision.isEmpty()
					&& listadoValoresGastosPrevision.isEmpty()) {
				minCantidad = 0;
				maxCantidad = 100;
			}

			mySimpleXYPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 3);

		}

		// Le pongo el formato al ejeX (meses)
		mySimpleXYPlot.setDomainValueFormat(new GraphXLabelFormat(leyendaEjeX));

		// Pongo los límites del ejeY (Cantidad en €)
		mySimpleXYPlot.setUserRangeOrigin(0);
		mySimpleXYPlot.setRangeBoundaries(minCantidad - 50, maxCantidad + 50,
				BoundaryMode.FIXED);
		mySimpleXYPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL,
				obtenerRangoEjeY(minCantidad, maxCantidad));

		// mySimpleXYPlot.disableAllMarkup();

	}

	/*
	 * Método que me devuelve el salto entre valores del ejeY a partir de los
	 * valores mínimos y máximos pasados por parámetro
	 */
	private int obtenerRangoEjeY(int minCantidad2, int maxCantidad2) {

		int dif = maxCantidad2 - minCantidad2;
		int salto = dif / 15;

		return salto;
	}

	/*
	 * Método para configurar la leyenda
	 */
	/*
	 * private void configurarLeyenda() {
	 * 
	 * // use a 2x2 grid: mySimpleXYPlot.getLegendWidget().setTableModel( new
	 * DynamicTableModel(2, 2));
	 * 
	 * // adjust the legend size so there is enough room // to draw the new
	 * legend grid: mySimpleXYPlot.getLegendWidget().setSize( new
	 * SizeMetrics((float) 0.2, SizeLayoutType.RELATIVE, (float) 0.2,
	 * SizeLayoutType.RELATIVE));
	 * 
	 * // add a semi-transparent black background to the legend // so it's
	 * easier to see overlaid on top of our plot: Paint bgPaint = new Paint();
	 * bgPaint.setColor(Color.BLACK); bgPaint.setStyle(Paint.Style.FILL);
	 * bgPaint.setAlpha(50);
	 * mySimpleXYPlot.getLegendWidget().setBackgroundPaint(bgPaint); // adjust
	 * the padding of the legend widget to look a little nicer:
	 * mySimpleXYPlot.getLegendWidget().setPadding(1, 1, 1, 1); // reposition
	 * the grid so that it rests above the bottom-left // edge of the graph
	 * widget:
	 * 
	 * mySimpleXYPlot.position(mySimpleXYPlot.getLegendWidget(), (float) -0.225,
	 * XLayoutStyle.RELATIVE_TO_RIGHT, (float) -0.3,
	 * YLayoutStyle.RELATIVE_TO_BOTTOM); }
	 */

	/*
	 * Método que lanza un Dialog de una advertencia producida
	 */
	private void lanzarAdvertencia(String advice) {

		Dialog d = crearDialogAdvertencia(advice);

		d.show();

	}

	/*
	 * Dialog para avisar de un movimiento no permitido
	 */
	private Dialog crearDialogAdvertencia(String advice) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("ADVERTENCIA");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(advice);

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Cierro el dialog
						dialog.cancel();
					}
				});

		return builder.create();
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

			dba.openREAD();

			cargarListasDeValores();

			dba.close();

			mySimpleXYPlot.clear();

			if (preferenceConfiguracionPrivate.getBoolean(
					singleton_csp.KEY_CBPLINEINGRESOS, false) == false
					&& preferenceConfiguracionPrivate.getBoolean(
							singleton_csp.KEY_CBPLINEGASTOS, false) == false
					&& preferenceConfiguracionPrivate.getBoolean(
							singleton_csp.KEY_CBPLINEBALANCE, false) == false) {

				lanzarAdvertencia("Debe seleccionar alguna gráfica en la configuración para mostrar.");

				mySimpleXYPlot.setVisibility(View.INVISIBLE);

			} else {
				prepararLineasGraficas();
				configurarGrafica();
				dibujarGrafica();

				mySimpleXYPlot.setVisibility(View.VISIBLE);
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Método que se llama al tocar la pantalla
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // Start gesture
			firstFinger = new PointF(event.getX(), event.getY());
			mode = ONE_FINGER_DRAG;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// When the gesture ends, a thread is created to give inertia to the
			// scrolling and zoom
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					while (Math.abs(lastScrolling) > 1f
							|| Math.abs(lastZooming - 1) < 1.01) {
						lastScrolling *= .8;
						scroll(lastScrolling);
						lastZooming += (1 - lastZooming) * .2;
						zoom(lastZooming);
						mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
								BoundaryMode.AUTO);
						// try {
						// mySimpleXYPlot.postRedraw();
						mySimpleXYPlot.redraw();
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
						// the thread lives until the scrolling and zooming are
						// imperceptible
					}
				}
			}, 0);
			
		case MotionEvent.ACTION_POINTER_DOWN: // second finger
			distBetweenFingers = spacing(event);
			// the distance check is done to avoid false alarms
			if (distBetweenFingers > 5f) {
				mode = TWO_FINGERS_DRAG;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ONE_FINGER_DRAG) {
				PointF oldFirstFinger = firstFinger;
				firstFinger = new PointF(event.getX(), event.getY());
				lastScrolling = oldFirstFinger.x - firstFinger.x;
				scroll(lastScrolling);
				lastZooming = (firstFinger.y - oldFirstFinger.y)
						/ mySimpleXYPlot.getHeight();
				if (lastZooming < 0)
					lastZooming = 1 / (1 - lastZooming);
				else
					lastZooming += 1;
				zoom(lastZooming);
				mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
						BoundaryMode.AUTO);
				
				mySimpleXYPlot.redraw();
				

			} else if (mode == TWO_FINGERS_DRAG) {
				float oldDist = distBetweenFingers;
				distBetweenFingers = spacing(event);
				lastZooming = oldDist / distBetweenFingers;
				zoom(lastZooming);
				mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
						BoundaryMode.AUTO);
				mySimpleXYPlot.redraw();
			}
			break;
		}
		
		return true;
	}

	private void zoom(float scale) {
		float domainSpan = maxXY.x - minXY.x;
		float domainMidPoint = maxXY.x - domainSpan / 2.0f;
		float offset = domainSpan * scale / 2.0f;
		minXY.x = domainMidPoint - offset;
		maxXY.x = domainMidPoint + offset;
	}

	private void scroll(float pan) {
		float domainSpan = maxXY.x - minXY.x;
		float step = domainSpan / mySimpleXYPlot.getWidth();
		float offset = pan * step;
		minXY.x += offset;
		maxXY.x += offset;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		//return Math.sqrt(x * x + y * y);
		return (float) Math.sqrt(x * x + y * y);
	}

}