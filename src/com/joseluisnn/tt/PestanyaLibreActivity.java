package com.joseluisnn.tt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.ValoresElementoListaGD;

public class PestanyaLibreActivity extends Activity {

	// Objetos View
	private TextView tvStartDate;
	private TextView tvEndDate;
	private TextView tvTotalIngresos;
	private TextView tvTotalGastos;
	private TextView tvTotalBalance;
	private LinearLayout llListadoIngresos;
	private LinearLayout llListadoGastos;
	private ImageView ivFlechaListadoIngresos;
	private ImageView ivFlechaListadoGastos;
	private ImageView ivSearchDate;
	// Variables para la animación de los iconos
	private Animation animacionBotonPulsado, animacionBotonLevantado;
	// Tiempo de pulsación inicial del icono
	private long tiempoDePulsacionInicial;
	// Variables que me despliegan/pliegan los Listados de Ingresos y Gastos
	private RelativeLayout rlBarraListadoIngresos;
	private RelativeLayout rlBarraListadoGastos;

	// Variables Calendar para tratar la fecha que se está viendo en los
	// Informes (Inicio y Fin)
	Calendar cInicio;
	Calendar cFin;

	// Variable para la BASE DE DATOS
	private DBAdapter dba;

	// Listas para los Ingresos y los Gastos agrupados por TRIMESTRES
	ArrayList<ValoresElementoListaGD> listadoValoresIngresos;
	ArrayList<ValoresElementoListaGD> listadoValoresGastos;
	/*
	 * Listas para los Ingresos y los Gastos devueltos por la BD y que
	 * posteriormente volcaré en las listas de arriba agrupados los datos por
	 * MESES y DÍAS
	 */
	ArrayList<ValoresElementoListaGD> listadoValoresIngresosAux;
	ArrayList<ValoresElementoListaGD> listadoValoresGastosAux;

	// Variables para tener el total de ingresos y gastos
	private double totalIngresos;
	private double totalGastos;

	// Variable para el formato de los números DOUBLE
	private DecimalFormatSymbols separadores;
	private DecimalFormat numeroAFormatear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pestanya_libre);
		
		animacionBotonPulsado = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_pulsado);
		animacionBotonLevantado = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_levantado);

		rlBarraListadoIngresos = (RelativeLayout) findViewById(R.id.relativeLayoutBarraListadoIngresosLibre);
		rlBarraListadoGastos = (RelativeLayout) findViewById(R.id.relativeLayoutBarraListadoGastosLibre);
		llListadoIngresos = (LinearLayout) findViewById(R.id.linearLayoutListadoIngresosLibre);
		llListadoGastos = (LinearLayout) findViewById(R.id.linearLayoutListadoGastosLibre);
		ivFlechaListadoIngresos = (ImageView) findViewById(R.id.imageViewListadoIngresosLibre);
		ivFlechaListadoGastos = (ImageView) findViewById(R.id.imageViewListadoGastosLibre);
		tvTotalIngresos = (TextView) findViewById(R.id.textViewTotalIngresosLibre);
		tvTotalGastos = (TextView) findViewById(R.id.textViewTotalGastosLibre);
		tvTotalBalance = (TextView) findViewById(R.id.textViewTotalBalanceLibre);
		ivSearchDate = (ImageView) findViewById(R.id.imageViewSearchFechaLibre);

		// Instancio las fechas
		tvStartDate = (TextView) findViewById(R.id.textViewPestanyaILibreFechaDe);
		tvEndDate = (TextView) findViewById(R.id.textViewPestanyaILibreFechaHasta);
		// Instancio las variables Calendar al día de hoy
		cInicio = Calendar.getInstance();
		cFin = Calendar.getInstance();

		tvStartDate.setText(obtenerFechaInicio());
		tvEndDate.setText(obtenerFechaFin());

		/*
		 * Método onClick para plegar/desplegar el listado de Ingresos
		 */
		rlBarraListadoIngresos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Veo cual es el estado del listado de ingresos y luego lo
				// abro o lo cierro
				if (llListadoIngresos.getVisibility() == View.VISIBLE) {
					llListadoIngresos.setVisibility(View.GONE);
					ivFlechaListadoIngresos.setImageDrawable(getResources()
							.getDrawable(android.R.drawable.arrow_down_float));
				} else {
					llListadoIngresos.setVisibility(View.VISIBLE);
					ivFlechaListadoIngresos.setImageDrawable(getResources()
							.getDrawable(android.R.drawable.arrow_up_float));
				}

			}
		});

		/*
		 * Método onClick para plegar/desplegar el listado de Gastos
		 */
		rlBarraListadoGastos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Veo cual es el estado del listado de ingresos y luego lo
				// abro o lo cierro
				if (llListadoGastos.getVisibility() == View.VISIBLE) {
					llListadoGastos.setVisibility(View.GONE);
					ivFlechaListadoGastos.setImageDrawable(getResources()
							.getDrawable(android.R.drawable.arrow_down_float));
				} else {
					llListadoGastos.setVisibility(View.VISIBLE);
					ivFlechaListadoGastos.setImageDrawable(getResources()
							.getDrawable(android.R.drawable.arrow_up_float));
				}
			}
		});

		ivSearchDate.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Método onTouch del botón de buscar en un Rango de Fechas
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					ivSearchDate.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						// lanzo el dialog con la fecha a buscar por el usuario
						ivSearchDate.startAnimation(animacionBotonLevantado);
						Dialog dialogo;
						dialogo = crearDialogoBuscarFecha();
						dialogo.show();
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					ivSearchDate.startAnimation(animacionBotonLevantado);
					break;
				case MotionEvent.ACTION_CANCEL:
					ivSearchDate.startAnimation(animacionBotonLevantado);
					break;

				}

				return true;
			}
		});

		// Instancio los formateadores de números
		separadores = new DecimalFormatSymbols();
		separadores.setDecimalSeparator(',');
		separadores.setGroupingSeparator('.');
		numeroAFormatear = new DecimalFormat("###,###.##", separadores);

		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(this);

		// Abro la Base de Datos solo en modo lectura
		dba.openREADWRITE();

		// Obtengo los valores de los datos de las Listas de Ingresos y Gastos
		listadoValoresIngresos = dba.listadoValoresIngresosPorFecha(
				obtenerEnteroFechaInicio(), obtenerEnteroFechaFin());
		listadoValoresGastos = dba.listadoValoresGastosPorFecha(
				obtenerEnteroFechaInicio(), obtenerEnteroFechaFin());

		/*
		 * Agrupo las Lista por TRIMESTRES y luego cargo los layouts
		 */
		cargarLayoutListadoIngresos();
		cargarLayoutListadoGastos();
		/*
		 * Calculo los totales y la diferencia y los actualizo en sus variables
		 */
		setTotalIngresos(calcularTotalIngresos());
		setTotalGastos(calcularTotalGastos());
		/*
		 * Actualizo los TextView que contienen los totales
		 */
		actualizarTotales();

		// Pongo los listados plegados
		llListadoIngresos.setVisibility(View.GONE);
		llListadoGastos.setVisibility(View.GONE);

		// Cierro la Base de datos
		dba.close();

	}

	/*
	 * Método que me devuelve en un String la fecha de inicio en el formato Día
	 * de Mes de Año
	 */
	private String obtenerFechaInicio() {

		String fecha;

		// Obtengo la fecha en el formato Día de Mes de Año
		fecha = "" + cInicio.get(Calendar.DAY_OF_MONTH) + " de "
				+ obtenerMes(cInicio.get(Calendar.MONTH)) + " de "
				+ cInicio.get(Calendar.YEAR);

		return fecha;

	}

	/*
	 * Método que me devuelve en un String la fecha de fin en el formato Día de
	 * Mes de Año
	 */
	private String obtenerFechaFin() {

		String fecha;

		// Obtengo la fecha en el formato Día de Mes de Año
		fecha = "" + cFin.get(Calendar.DAY_OF_MONTH) + " de "
				+ obtenerMes(cFin.get(Calendar.MONTH)) + " de "
				+ cFin.get(Calendar.YEAR);

		return fecha;

	}

	/*
	 * Método que me devuelve la fecha de INICIO en una variable entero
	 */
	private int obtenerEnteroFechaInicio() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (cInicio.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + cInicio.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + cInicio.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}

	/*
	 * Método que me devuelve la fecha de FIN en una variable entero
	 */
	private int obtenerEnteroFechaFin() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (cFin.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + cFin.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + cFin.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}

	/*
	 * Método que me carga en el Layout de listado de Ingresos las fechas,
	 * conceptos y valores asociados a partir de la consulta realizada a la Base
	 * de datos Mostrará valores del Año actual o el Año anterior
	 */
	private void cargarLayoutListadoIngresos() {

		int fechaAnterior = 00000000;
		String fechaAnteriorFormateada = new String("000000");
		// Variable para la fecha devuelta por la base de datos
		String fechaBD;

		RelativeLayout rowViewFechaMes = null;
		RelativeLayout rowViewFechaDia = null;
		RelativeLayout rowViewContenido = null;

		// borro el contenido del Layout para cargarlo
		llListadoIngresos.removeAllViews();

		for (int i = 0; i < listadoValoresIngresos.size(); i++) {

			/*
			 * recupero el RelativeLayout que contiene la fila del contenido
			 * inflamos nuestra fila desde el archivo XML
			 */
			rowViewContenido = (RelativeLayout) LayoutInflater.from(this)
					.inflate(
							this.getResources().getLayout(
									R.layout.listado_informes_contenido), null);
			// Buscamos el View que contendrá el Concepto
			TextView tvConcepto = (TextView) rowViewContenido
					.findViewById(R.id.textViewListadoInformesConcepto);
			// establecemos el contenido
			tvConcepto.setText(listadoValoresIngresos.get(i).getConcepto());
			// Buscamos el View que contendrá la Cantidad
			TextView tvCantidad = (TextView) rowViewContenido
					.findViewById(R.id.textViewListadoInformesCantidad);
			// establecemos el contenido
			tvCantidad.setText(""
					+ numeroAFormatear.format(listadoValoresIngresos.get(i)
							.getCantidad()) + "€");

			fechaBD = "" + listadoValoresIngresos.get(i).getIdFecha();

			// entra en el if si cambia de Mes, por lo que escribo el mes, abajo
			// el dia del mes y luego el contenido
			if (!fechaBD.substring(0, 6).equals(fechaAnteriorFormateada)) {

				fechaAnteriorFormateada = fechaBD.substring(0, 6);
				fechaAnterior = listadoValoresIngresos.get(i).getIdFecha();

				rowViewFechaMes = (RelativeLayout) LayoutInflater
						.from(this)
						.inflate(
								this.getResources()
										.getLayout(
												R.layout.listado_informe_encabezado_mes),
								null);
				rowViewFechaDia = (RelativeLayout) LayoutInflater
						.from(this)
						.inflate(
								this.getResources()
										.getLayout(
												R.layout.listado_informes_encabezado_fecha),
								null);
				TextView tvMes = (TextView) rowViewFechaMes
						.findViewById(R.id.textViewEncabezadoMesListadoInformes);
				tvMes.setText(obtenerCadenaFechaMes(listadoValoresIngresos.get(
						i).getIdFecha()));

				TextView tvFecha = (TextView) rowViewFechaDia
						.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
				tvFecha.setText(obtenerCadenaFechaParcial(listadoValoresIngresos
						.get(i).getIdFecha()));

				// Inserto el layout del mes y fecha completa como cabeceras
				llListadoIngresos.addView(rowViewFechaMes);
				llListadoIngresos.addView(rowViewFechaDia);

				// Inserto el contenido
				llListadoIngresos.addView(rowViewContenido);

			} else {
				// ENTRA SI CAMBIA DE DIA, NO DE MES
				if (fechaAnterior != listadoValoresIngresos.get(i).getIdFecha()) {

					fechaAnterior = listadoValoresIngresos.get(i).getIdFecha();

					rowViewFechaDia = (RelativeLayout) LayoutInflater
							.from(this)
							.inflate(
									this.getResources()
											.getLayout(
													R.layout.listado_informes_encabezado_fecha),
									null);

					TextView tvFecha = (TextView) rowViewFechaDia
							.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
					tvFecha.setText(obtenerCadenaFechaParcial(listadoValoresIngresos
							.get(i).getIdFecha()));

					// Inserto el layout de fecha como cabecera
					llListadoIngresos.addView(rowViewFechaDia);

					// Inserto el contenido
					llListadoIngresos.addView(rowViewContenido);

				} else {

					// Inserto el contenido
					llListadoIngresos.addView(rowViewContenido);

				}
			}
		}
	}

	/*
	 * Método que me carga en el Layout de listado de Ingresos las fechas,
	 * conceptos y valores asociados a partir de la consulta realizada a la Base
	 * de datos Mostrará valores del Año actual o el Año anterior
	 */
	private void cargarLayoutListadoGastos() {

		int fechaAnterior = 00000000;
		String fechaAnteriorFormateada = new String("000000");
		// Variable para la fecha devuelta por la base de datos
		String fechaBD;

		RelativeLayout rowViewFechaMes = null;
		RelativeLayout rowViewFechaDia = null;
		RelativeLayout rowViewContenido = null;

		// borro el contenido del Layout para cargarlo
		llListadoGastos.removeAllViews();

		for (int i = 0; i < listadoValoresGastos.size(); i++) {

			/*
			 * recupero el RelativeLayout que contiene la fila del contenido
			 * inflamos nuestra fila desde el archivo XML
			 */
			rowViewContenido = (RelativeLayout) LayoutInflater.from(this)
					.inflate(
							this.getResources().getLayout(
									R.layout.listado_informes_contenido), null);
			// Buscamos el View que contendrá el Concepto
			TextView tvConcepto = (TextView) rowViewContenido
					.findViewById(R.id.textViewListadoInformesConcepto);
			// establecemos el contenido
			tvConcepto.setText(listadoValoresGastos.get(i).getConcepto());
			// Buscamos el View que contendrá la Cantidad
			TextView tvCantidad = (TextView) rowViewContenido
					.findViewById(R.id.textViewListadoInformesCantidad);
			// establecemos el contenido
			tvCantidad.setText(""
					+ numeroAFormatear.format(listadoValoresGastos.get(i)
							.getCantidad()) + "€");

			fechaBD = "" + listadoValoresGastos.get(i).getIdFecha();

			// entra en el if si cambia de Mes, por lo que escribo el mes, abajo
			// el dia del mes y luego el contenido
			if (!fechaBD.substring(0, 6).equals(fechaAnteriorFormateada)) {

				fechaAnteriorFormateada = fechaBD.substring(0, 6);
				fechaAnterior = listadoValoresGastos.get(i).getIdFecha();

				rowViewFechaMes = (RelativeLayout) LayoutInflater
						.from(this)
						.inflate(
								this.getResources()
										.getLayout(
												R.layout.listado_informe_encabezado_mes),
								null);
				rowViewFechaDia = (RelativeLayout) LayoutInflater
						.from(this)
						.inflate(
								this.getResources()
										.getLayout(
												R.layout.listado_informes_encabezado_fecha),
								null);
				TextView tvMes = (TextView) rowViewFechaMes
						.findViewById(R.id.textViewEncabezadoMesListadoInformes);
				tvMes.setText(obtenerCadenaFechaMes(listadoValoresGastos.get(i)
						.getIdFecha()));

				TextView tvFecha = (TextView) rowViewFechaDia
						.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
				tvFecha.setText(obtenerCadenaFechaParcial(listadoValoresGastos
						.get(i).getIdFecha()));

				// Inserto el layout del mes y fecha completa como cabeceras
				llListadoGastos.addView(rowViewFechaMes);
				llListadoGastos.addView(rowViewFechaDia);

				// Inserto el contenido
				llListadoGastos.addView(rowViewContenido);

			} else {

				if (fechaAnterior != listadoValoresGastos.get(i).getIdFecha()) {

					fechaAnterior = listadoValoresGastos.get(i).getIdFecha();

					rowViewFechaDia = (RelativeLayout) LayoutInflater
							.from(this)
							.inflate(
									this.getResources()
											.getLayout(
													R.layout.listado_informes_encabezado_fecha),
									null);

					TextView tvFecha = (TextView) rowViewFechaDia
							.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
					tvFecha.setText(obtenerCadenaFechaParcial(listadoValoresGastos
							.get(i).getIdFecha()));

					// Inserto el layout de fecha como cabecera
					llListadoGastos.addView(rowViewFechaDia);

					// Inserto el contenido
					llListadoGastos.addView(rowViewContenido);

				} else {

					// Inserto el contenido
					llListadoGastos.addView(rowViewContenido);

				}
			}
		}

	}

	/*
	 * Método para obtener la fecha en una Cadena Formateada para los Meses y
	 * Años
	 */
	private String obtenerCadenaFechaMes(int enteroFecha) {

		String fecha = new String("" + enteroFecha);
		String cadenaFecha;
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

		cadenaFecha = obtenerMes(c.get(Calendar.MONTH)) + " de "
				+ c.get(Calendar.YEAR);

		return cadenaFecha;
	}

	/*
	 * Método para obtener la fecha en una Cadena Formateada con el día de la
	 * semana y el día del mes; Ejemplo: Lun, 25
	 */
	private String obtenerCadenaFechaParcial(int enteroFecha) {

		String fecha = new String("" + enteroFecha);
		String cadenaFecha;
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

		cadenaFecha = obtenerDiaSemana(c.get(Calendar.DAY_OF_WEEK)) + ". "
				+ c.get(Calendar.DAY_OF_MONTH);

		return cadenaFecha;
	}

	/*
	 * Método que me devuelve el mes según el entero pasado por parámetro
	 */
	private String obtenerMes(int month) {

		String m = new String();

		switch (month) {

		case 0:
			m = "Enero";
			break;
		case 1:
			m = "Febrero";
			break;
		case 2:
			m = "Marzo";
			break;
		case 3:
			m = "Abril";
			break;
		case 4:
			m = "Mayo";
			break;
		case 5:
			m = "Junio";
			break;
		case 6:
			m = "Julio";
			break;
		case 7:
			m = "Agosto";
			break;
		case 8:
			m = "Septiembre";
			break;
		case 9:
			m = "Octubre";
			break;
		case 10:
			m = "Noviembre";
			break;
		case 11:
			m = "Diciembre";
			break;
		default:
			m = "error";
			break;
		}

		return m;

	}

	/*
	 * Método que me devuelve el día de la semana según el entero pasado por
	 * parámetro
	 */
	private String obtenerDiaSemana(int day) {

		String d = new String();

		switch (day) {
		case 1:
			d = "Dom";
			break;
		case 2:
			d = "Lun";
			break;
		case 3:
			d = "Mar";
			break;
		case 4:
			d = "Mié";
			break;
		case 5:
			d = "Jue";
			break;
		case 6:
			d = "Vie";
			break;
		case 7:
			d = "Sáb";
			break;
		default:
			break;
		}

		return d;
	}

	/*
	 * Método que me actualiza la variable total de ingresos
	 */
	private void setTotalIngresos(double ing) {
		this.totalIngresos = ing;
	}

	/*
	 * Método que me devuelve el valor total de ingresos
	 */
	private double getTotalIngresos() {
		return this.totalIngresos;
	}

	/*
	 * Método que me actualiza la variable total de gastos
	 */
	private void setTotalGastos(double gas) {
		this.totalGastos = gas;
	}

	/*
	 * Método que me devuelve el valor total de gastos
	 */
	private double getTotalGastos() {
		return this.totalGastos;
	}

	/*
	 * Método que me calcula el total de los Ingresos a partir de la Lista de
	 * Ingresos recuperada de la BD
	 */
	private double calcularTotalIngresos() {

		double total = 0;

		if (!listadoValoresIngresos.isEmpty()) {

			for (int i = 0; i < listadoValoresIngresos.size(); i++) {
				total = total + listadoValoresIngresos.get(i).getCantidad();
			}

		}

		return total;
	}

	/*
	 * Método que me calcula el total de los Gastos a partir de la Lista de
	 * Gastos recuperada de la BD
	 */
	private double calcularTotalGastos() {

		double total = 0;

		if (!listadoValoresGastos.isEmpty()) {

			for (int i = 0; i < listadoValoresGastos.size(); i++) {
				total = total + listadoValoresGastos.get(i).getCantidad();
			}
		}

		return total;
	}

	/*
	 * Método que me actualiza los TextView con los totales
	 */
	private void actualizarTotales() {

		double balance = getTotalIngresos() - getTotalGastos();

		tvTotalIngresos.setText(" "
				+ numeroAFormatear.format(getTotalIngresos()) + " €");
		tvTotalIngresos.setTextColor(this.getResources().getColor(
				R.color.ListadosVerdeOscuro));

		tvTotalGastos.setText(" " + numeroAFormatear.format(getTotalGastos())
				+ " €");
		tvTotalGastos.setTextColor(this.getResources().getColor(
				R.color.ListadosRojo));

		tvTotalBalance.setText(" " + numeroAFormatear.format(balance) + " €");
		if (balance >= 0) {
			tvTotalBalance.setTextColor(this.getResources().getColor(
					R.color.ListadosVerdeOscuro));
		} else {
			tvTotalBalance.setTextColor(this.getResources().getColor(
					R.color.ListadosRojo));
		}

	}

	/*
	 * Dialog para situar un rango de fechas y que me devuelva
	 * los valores contenidos en ellas.
	 */
	private Dialog crearDialogoBuscarFecha() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_search_date, null);

		final DatePicker startDate = (DatePicker) layout
				.findViewById(R.id.datePickerStart);
		final DatePicker endDate = (DatePicker) layout
				.findViewById(R.id.datePickerEnd);

		// inicializo las fechas en los DatePicker
		startDate
				.updateDate(cInicio.get(Calendar.YEAR),
						cInicio.get(Calendar.MONTH),
						cInicio.get(Calendar.DAY_OF_MONTH));
		endDate.updateDate(cFin.get(Calendar.YEAR), cFin.get(Calendar.MONTH),
				cFin.get(Calendar.DAY_OF_MONTH));

		// Le inserto el layout al Dialog con LayoutInflater
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(layout);

		builder.setTitle("Elegir Fechas");

		// builder.setView(valorIngreso);
		builder.setIcon(android.R.drawable.ic_menu_my_calendar);

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Si la fechaInicio es mayor que la FechaFin no se
						// puede
						// realizar la búsqueda
						
						// Actualizo las variables Calendar
						cInicio.set(Calendar.YEAR, startDate.getYear());
						cInicio.set(Calendar.MONTH, startDate.getMonth());
						cInicio.set(Calendar.DAY_OF_MONTH,
								startDate.getDayOfMonth());
						cFin.set(Calendar.YEAR, endDate.getYear());
						cFin.set(Calendar.MONTH, endDate.getMonth());
						cFin.set(Calendar.DAY_OF_MONTH,
								endDate.getDayOfMonth());

						if (obtenerEnteroFechaInicio() > obtenerEnteroFechaFin()) {
							// Entra en el IF si la fecha de inicio es mayor que
							// la de fin
							lanzarAdvertencia("La fecha de inicio no puede ser mayor que la de fin.");

						} else {

							// Obtengo los valores de los datos de las Listas de
							// Ingresos y Gastos
							listadoValoresIngresos = dba
									.listadoValoresIngresosPorFecha(
											obtenerEnteroFechaInicio(),
											obtenerEnteroFechaFin());
							listadoValoresGastos = dba
									.listadoValoresGastosPorFecha(
											obtenerEnteroFechaInicio(),
											obtenerEnteroFechaFin());

							/*
							 * Agrupo las Lista por TRIMESTRES y luego cargo los
							 * layouts
							 */
							cargarLayoutListadoIngresos();
							cargarLayoutListadoGastos();
							/*
							 * Calculo los totales y la diferencia y los
							 * actualizo en sus variables
							 */
							setTotalIngresos(calcularTotalIngresos());
							setTotalGastos(calcularTotalGastos());
							/*
							 * Actualizo los TextView que contienen los totales
							 */
							actualizarTotales();

							tvStartDate.setText(obtenerFechaInicio());
							tvEndDate.setText(obtenerFechaFin());
						}
					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO No hago nada
						dialog.cancel();
					}
				});

		return builder.create();
	}
	
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
	protected void onPause() {
		// TODO Cierro la BD en OnPause() cuando la Actividad no está en el foco
		dba.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Abro la base de datos mientras la Actividad está Activa
		dba.openREADWRITE();
		super.onResume();
	}

}
