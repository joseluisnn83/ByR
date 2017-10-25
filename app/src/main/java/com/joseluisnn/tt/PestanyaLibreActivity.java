package com.joseluisnn.tt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.joseluisnn.objetos.FiltroConcepto;
import com.joseluisnn.objetos.ValoresElementoListaGD;
import com.joseluisnn.singleton.SingletonBroadcastReceiver;
import com.joseluisnn.singleton.SingletonFechas;
import com.joseluisnn.singleton.SingletonTipoMoneda;

public class PestanyaLibreActivity extends Activity {

	// Declaro los dos tipos de filtros posibles
	private static String TIPO_FILTRO_INGRESO = "filtroIngreso";
	private static String TIPO_FILTRO_GASTO = "filtroGasto";
	// Declaro los dos tipos de conceptos posibles
	private static String TIPO_CONCEPTO_INGRESO = "ingreso";
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
	/*
	 * Variables Calendar para tratar la fecha que se está viendo en los
	 * Informes (Inicio y Fin)
	 */
	private Calendar cInicio;
	private Calendar cFin;
	// Variable para la BASE DE DATOS
	private DBAdapter dba;
	// Listas para los Ingresos y los Gastos agrupados por TRIMESTRES
	private ArrayList<ValoresElementoListaGD> listadoValoresIngresos;
	private ArrayList<ValoresElementoListaGD> listadoValoresGastos;
	// Listas para el filtro de Ingresos y Gastos
	private ArrayList<FiltroConcepto> filtroIngresos;
	private ArrayList<FiltroConcepto> filtroGastos;
	// Variables para tener el total de ingresos y gastos
	private double totalIngresos;
	private double totalGastos;
	// Variable para el formato de los números DOUBLE
	private DecimalFormatSymbols separadores;
	private DecimalFormat numeroAFormatear;
	/*
	 * Variables para saber el símbolo de la moneda a utilizar
	 */
	private SingletonTipoMoneda singleton_tm;
	private String tipoMoneda;
	// Variable para obtener las fechas
	private SingletonFechas singleton_fechas;
	private Context contexto;
	// Variable que contiene la constante para saber qué Broadcast se ha enviado
	private SingletonBroadcastReceiver sbr;
	/*
	 * Variable para recibir una señal de BroadcastReceiver al modificar el
	 * filtro de los conceptos
	 */
	private BroadcastReceiver myReceiver;
	// Variable para aplicar el filtro cada vez que se cambie y el usuario le de
	// a Aplicar
	private boolean aplicarFiltro;
	private String tipoFiltroAplicar;
	// Variable para controlar que sea la primera vez que se entra en el
	// OnResume()
	private boolean primeraVez;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pestanya_libre);

		// Obtengo el tipo de moneda a tulizar
		singleton_tm = SingletonTipoMoneda.getInstance();
		tipoMoneda = singleton_tm.obtenerTipoMoneda(getApplicationContext());
		// Obtengo la clase que contiene los métodos para obtener las fechas
		singleton_fechas = SingletonFechas.getInstance();
		this.contexto = this;
		// Obtengo la clase que contiene las constantes para enviar el Broadcast
		sbr = new SingletonBroadcastReceiver();
		// El filtro inicialmente está a false
		this.aplicarFiltro = false;
		this.primeraVez = true;

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

		tvStartDate.setText(singleton_fechas.obtenerFechaInicioLibre(this,
				cInicio));
		tvEndDate.setText(singleton_fechas.obtenerFechaFinLibre(this, cFin));

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

		// Obtengo los filtros pasados por parametros de la Activity
		// InformesScreenActivity
		filtroIngresos = getIntent().getExtras().getParcelableArrayList(
				TIPO_FILTRO_INGRESO);
		filtroGastos = getIntent().getExtras().getParcelableArrayList(
				TIPO_FILTRO_GASTO);

		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(this);

		// Abro la Base de Datos solo en modo lectura
		dba.openREADWRITE();

		// Obtengo los valores de los datos de las Listas de Ingresos y Gastos
		listadoValoresIngresos = dba.listadoValoresIngresosPorFecha(
				singleton_fechas.obtenerEnteroFechaInicio(cInicio),
				singleton_fechas.obtenerEnteroFechaFin(cFin), filtroIngresos);
		listadoValoresGastos = dba.listadoValoresGastosPorFecha(
				singleton_fechas.obtenerEnteroFechaInicio(cInicio),
				singleton_fechas.obtenerEnteroFechaFin(cFin), filtroGastos);

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
							.getCantidad()) + tipoMoneda);

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
				tvMes.setText(singleton_fechas.obtenerCadenaFechaMensual(this,
						listadoValoresIngresos.get(i).getIdFecha()));

				TextView tvFecha = (TextView) rowViewFechaDia
						.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
				tvFecha.setText(singleton_fechas.obtenerCadenaFechaParcial(
						this, listadoValoresIngresos.get(i).getIdFecha()));

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
					tvFecha.setText(singleton_fechas.obtenerCadenaFechaParcial(
							this, listadoValoresIngresos.get(i).getIdFecha()));

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
							.getCantidad()) + tipoMoneda);

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
				tvMes.setText(singleton_fechas.obtenerCadenaFechaMensual(this,
						listadoValoresGastos.get(i).getIdFecha()));

				TextView tvFecha = (TextView) rowViewFechaDia
						.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
				tvFecha.setText(singleton_fechas.obtenerCadenaFechaParcial(
						this, listadoValoresGastos.get(i).getIdFecha()));

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
					tvFecha.setText(singleton_fechas.obtenerCadenaFechaParcial(
							this, listadoValoresGastos.get(i).getIdFecha()));

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
				+ numeroAFormatear.format(getTotalIngresos()) + " "
				+ tipoMoneda);
		tvTotalIngresos.setTextColor(this.getResources().getColor(
				R.color.ListadosVerdeOscuro));

		tvTotalGastos.setText(" " + numeroAFormatear.format(getTotalGastos())
				+ " " + tipoMoneda);
		tvTotalGastos.setTextColor(this.getResources().getColor(
				R.color.ListadosRojo));

		tvTotalBalance.setText(" " + numeroAFormatear.format(balance) + " "
				+ tipoMoneda);
		if (balance >= 0) {
			tvTotalBalance.setTextColor(this.getResources().getColor(
					R.color.ListadosVerdeOscuro));
		} else {
			tvTotalBalance.setTextColor(this.getResources().getColor(
					R.color.ListadosRojo));
		}

	}

	/*
	 * Dialog para situar un rango de fechas y que me devuelva los valores
	 * contenidos en ellas.
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

		builder.setTitle(getResources().getString(
				R.string.informes_libre_elegir_fecha));

		// builder.setView(valorIngreso);
		builder.setIcon(android.R.drawable.ic_menu_my_calendar);

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Si la fechaInicio es mayor que la FechaFin no se
						// puede
						// realizar la búsqueda
						Calendar cInicioAux = cInicio;
						Calendar cFinAux = cFin;

						// Actualizo las variables Calendar
						cInicio.set(Calendar.YEAR, startDate.getYear());
						cInicio.set(Calendar.MONTH, startDate.getMonth());
						cInicio.set(Calendar.DAY_OF_MONTH,
								startDate.getDayOfMonth());
						cFin.set(Calendar.YEAR, endDate.getYear());
						cFin.set(Calendar.MONTH, endDate.getMonth());
						cFin.set(Calendar.DAY_OF_MONTH, endDate.getDayOfMonth());

						if (singleton_fechas.obtenerEnteroFechaInicio(cInicio) > singleton_fechas
								.obtenerEnteroFechaFin(cFin)) {
							// Entra en el IF si la fecha de inicio es mayor que
							// la de fin
							lanzarAdvertencia(getResources().getString(
									R.string.informes_libre_fechaini_mayor));
							// Vuelvo a dejar los valores que tenían
							cInicio = cInicioAux;
							cFin = cFinAux;

						} else {

							// Obtengo los valores de los datos de las Listas de
							// Ingresos y Gastos
							listadoValoresIngresos = dba.listadoValoresIngresosPorFecha(
									singleton_fechas
											.obtenerEnteroFechaInicio(cInicio),
									singleton_fechas
											.obtenerEnteroFechaFin(cFin),
									filtroIngresos);
							listadoValoresGastos = dba.listadoValoresGastosPorFecha(
									singleton_fechas
											.obtenerEnteroFechaInicio(cInicio),
									singleton_fechas
											.obtenerEnteroFechaFin(cFin),
									filtroGastos);

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

							tvStartDate.setText(singleton_fechas
									.obtenerFechaInicioLibre(contexto, cInicio));
							tvEndDate.setText(singleton_fechas
									.obtenerFechaFinLibre(contexto, cFin));
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

		builder.setTitle(getResources().getString(
				R.string.configuracion_advertencia));
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

		IntentFilter ifilter = new IntentFilter(sbr.FILTRO_CONCEPTOS_APLICAR);

		myReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Recibo el Broadcast y sé que debo aplicar el filtro
				aplicarFiltro = true;
				/*
				 * Obtengo los filtros pasados por parametros de la Activity
				 * InformesScreenActivity en función del filtro a aplicar
				 */
				tipoFiltroAplicar = intent.getExtras().getString(
						"tipo_concepto");

				if (tipoFiltroAplicar.equals(TIPO_CONCEPTO_INGRESO)) {
					filtroIngresos = intent.getExtras().getParcelableArrayList(
							TIPO_FILTRO_INGRESO);
				} else {
					filtroGastos = intent.getExtras().getParcelableArrayList(
							TIPO_FILTRO_GASTO);
				}
			}
		};

		// Registro mi BroadcastReceiver
		this.registerReceiver(myReceiver, ifilter);
	}

	@Override
	protected void onResume() {
		// TODO Abro la base de datos mientras la Actividad está Activa
		dba.openREADWRITE();

		if (primeraVez) {
			primeraVez = false;
		} else {
			/*
			 * Quito el registro de mi BroadcastReceiver cuando mi Actividad
			 * pasa de estar oculta (o no Activa) por otra Actividad o
			 * aplicación a visible (o Activa)
			 */
			this.unregisterReceiver(myReceiver);
		}

		if (aplicarFiltro) {

			if (tipoFiltroAplicar.equals(TIPO_CONCEPTO_INGRESO)) {
				listadoValoresIngresos = dba.listadoValoresIngresosPorFecha(
						singleton_fechas.obtenerEnteroFechaInicio(cInicio),
						singleton_fechas.obtenerEnteroFechaFin(cFin),
						filtroIngresos);
				cargarLayoutListadoIngresos();
				/*
				 * Calculo los totales y la diferencia y los actualizo en sus
				 * variables
				 */
				setTotalIngresos(calcularTotalIngresos());

			} else {
				listadoValoresGastos = dba.listadoValoresGastosPorFecha(
						singleton_fechas.obtenerEnteroFechaInicio(cInicio),
						singleton_fechas.obtenerEnteroFechaFin(cFin),
						filtroGastos);

				cargarLayoutListadoGastos();
				setTotalGastos(calcularTotalGastos());
			}

			/*
			 * Actualizo los TextView que contienen los totales
			 */
			actualizarTotales();

			aplicarFiltro = false;
		}

		super.onResume();
	}

}
