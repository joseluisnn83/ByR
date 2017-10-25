package com.joseluisnn.tt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.FiltroConcepto;
import com.joseluisnn.objetos.ValoresElementoListaGD;
import com.joseluisnn.singleton.SingletonBroadcastReceiver;
import com.joseluisnn.singleton.SingletonFechas;
import com.joseluisnn.singleton.SingletonTipoMoneda;

public class PestanyaMensualActivity extends Activity {

	// Declaro los dos tipos de filtros posibles
	private static String TIPO_FILTRO_INGRESO = "filtroIngreso";
	private static String TIPO_FILTRO_GASTO = "filtroGasto";
	// Declaro los dos tipos de conceptos posibles
	private static String TIPO_CONCEPTO_INGRESO = "ingreso";
	// Objetos View
	private Spinner meses;
	private TextView tvTotalIngresos;
	private TextView tvTotalGastos;
	private TextView tvTotalBalance;
	private LinearLayout llListadoIngresos;
	private LinearLayout llListadoGastos;
	private ImageView ivFlechaListadoIngresos;
	private ImageView ivFlechaListadoGastos;
	// Variables que me despliegan/pliegan los Listados de Ingresos y Gastos
	private RelativeLayout rlBarraListadoIngresos;
	private RelativeLayout rlBarraListadoGastos;
	// Adaptador para el spinner los meses
	private ArrayAdapter<String> adapterMeses;
	// Lista para el spinner de los meses
	private ArrayList<String> listaMeses;
	// Variable para la BASE DE DATOS
	private DBAdapter dba;
	// Listas para los Ingresos y los Gastos
	ArrayList<ValoresElementoListaGD> listadoValoresIngresos;
	ArrayList<ValoresElementoListaGD> listadoValoresGastos;	
	// Listas para el filtro de Ingresos y Gastos
	ArrayList<FiltroConcepto> filtroIngresos;
	ArrayList<FiltroConcepto> filtroGastos;
	// Variables para tener el total de ingresos y gastos
	private double totalIngresos;
	private double totalGastos;
	// Variable para el formato de los números DOUBLE
	private DecimalFormatSymbols separadores;
	private DecimalFormat numeroAFormatear;
	/*
	 * Variable para captar cuando es la primera ejecución del programa y
	 * controlar los cambios en el Spinner de las semanas
	 */
	private int posicionSpinnerAnterior;
	/*
	 * Variables para saber el símbolo de la moneda a utilizar
	 */
	private SingletonTipoMoneda singleton_tm;
	private String tipoMoneda;
	// Variable para obtener las fechas
	private SingletonFechas singleton_fechas;
	// Variable que contiene la constante para saber qué Broadcast se ha enviado
	private SingletonBroadcastReceiver sbr;
	/*
	 *  Variable para recibir una señal de BroadcastReceiver al modificar el 
	 *  filtro de los conceptos
	 */
	private BroadcastReceiver myReceiver;
	// Variable para aplicar el filtro cada vez que se cambie y el usuario le de a Aplicar
	private boolean aplicarFiltro;
	private String tipoFiltroAplicar;
	/*
	 * Variable para saber en qué mes me encuentro y actualizar los valores
	 * después de aplicar el filtro
	 * 
	 * month = -1 (mes pasado)
	 * month = 1 (mes actual)
	 */
	private int month;
	// Variable para controlar que sea la primera vez que se entra en el OnResume()
	private boolean primeraVez;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pestanya_mensual);

		// Obtengo el tipo de moneda a tulizar
		singleton_tm = SingletonTipoMoneda.getInstance();
		tipoMoneda = singleton_tm.obtenerTipoMoneda(getApplicationContext());
		
		// Obtengo la clase que contiene los métodos para obtener las fechas
		singleton_fechas = SingletonFechas.getInstance();
		
		// Obtengo la clase que contiene las constantes para enviar el Broadcast
		sbr = new SingletonBroadcastReceiver();
		// El filtro inicialmente está a false
		this.aplicarFiltro = false;
		this.primeraVez = true;

		/*
		 * Instancio los objetos View
		 */
		meses = (Spinner) findViewById(R.id.spinnerPestanyaIMensual);
		tvTotalIngresos = (TextView) findViewById(R.id.textViewTotalIngresosMensual);
		tvTotalGastos = (TextView) findViewById(R.id.textViewTotalGastosMensual);
		tvTotalBalance = (TextView) findViewById(R.id.textViewTotalBalanceMensual);
		llListadoIngresos = (LinearLayout) findViewById(R.id.linearLayoutListadoIngresosMensual);
		llListadoGastos = (LinearLayout) findViewById(R.id.linearLayoutListadoGastosMensual);
		rlBarraListadoIngresos = (RelativeLayout) findViewById(R.id.relativeLayoutBarraListadoIngresosMensual);
		rlBarraListadoGastos = (RelativeLayout) findViewById(R.id.relativeLayoutBarraListadoGastosMensual);
		ivFlechaListadoIngresos = (ImageView) findViewById(R.id.imageViewListadoIngresosMensual);
		ivFlechaListadoGastos = (ImageView) findViewById(R.id.imageViewListadoGastosMensual);

		// Inicializo los controles del Sipnner de los meses
		posicionSpinnerAnterior = 0;

		// Instancio y creo los meses en la lista  de selección
		listaMeses = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		listaMeses.add(singleton_fechas.obtenerMes(this,c.get(Calendar.MONTH)) + " "
				+ getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + c.get(Calendar.YEAR));
		c.add(Calendar.MONTH, 1);
		listaMeses.add(singleton_fechas.obtenerMes(this,c.get(Calendar.MONTH)) + " "
				+ getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + c.get(Calendar.YEAR));
		// Instancio y creo el Adaptador para el spinner
		adapterMeses = new ArrayAdapter<String>(this, R.layout.own_spinner,
				listaMeses);
		adapterMeses
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// le añado el adapter al spinner
		meses.setAdapter(adapterMeses);

		// Instancio los formateadores de números
		separadores = new DecimalFormatSymbols();
		separadores.setDecimalSeparator(',');
		separadores.setGroupingSeparator('.');
		numeroAFormatear = new DecimalFormat("###,###.##", separadores);

		// Obtengo los filtros pasados por parametros de la Activity InformesScreenActivity
		filtroIngresos = getIntent().getExtras().getParcelableArrayList(TIPO_FILTRO_INGRESO);
		filtroGastos = getIntent().getExtras().getParcelableArrayList(TIPO_FILTRO_GASTO);
		
		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(this);

		// Abro la Base de Datos solo en modo lectura
		dba.openREADWRITE();

		/*
		 * accedo a la BD para que me devuelva los ingresos y gastos en sus
		 * respectivas Listas
		 */
		listadoValoresIngresos = dba.listadoValoresIngresosPorFecha(
				singleton_fechas.obtenerInicioEnteroFechaMesAnterior(),
				singleton_fechas.obtenerFinEnteroFechaMesAnterior(),filtroIngresos);
		listadoValoresGastos = dba.listadoValoresGastosPorFecha(
				singleton_fechas.obtenerInicioEnteroFechaMesAnterior(),
				singleton_fechas.obtenerFinEnteroFechaMesAnterior(),filtroGastos);
		
		// Indico que estoy visualizando el mes pasado
		this.month = -1;

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

		meses.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				if (posicionSpinnerAnterior != position) {

					if (position == 0) {
						// entra por aquí si se quiere ver el listado de la
						// semana anterior
						// lanzarMensaje("semana anterior");
						posicionSpinnerAnterior = position;

						/*
						 * accedo a la BD para que me devuelva los ingresos y
						 * gastos en sus respectivas Listas
						 */

						listadoValoresIngresos = dba
								.listadoValoresIngresosPorFecha(
										singleton_fechas.obtenerInicioEnteroFechaMesAnterior(),
										singleton_fechas.obtenerFinEnteroFechaMesAnterior(),filtroIngresos);
						listadoValoresGastos = dba
								.listadoValoresGastosPorFecha(
										singleton_fechas.obtenerInicioEnteroFechaMesAnterior(),
										singleton_fechas.obtenerFinEnteroFechaMesAnterior(),filtroGastos);

						cargarLayoutListadoIngresos();
						cargarLayoutListadoGastos();

						/*
						 * Calculo los totales y la diferencia y los actualizo
						 * en sus variables
						 */
						setTotalIngresos(calcularTotalIngresos());
						setTotalGastos(calcularTotalGastos());

						/*
						 * Actualizo los TextView que contienen los totales
						 */
						actualizarTotales();
						
						// Indico que estoy visualizando el mes pasado
						month = -1;

					} else {
						// entra por aquí si se quiere ver el listado de la
						// semana actual
						// lanzarMensaje("semana actual");
						posicionSpinnerAnterior = position;

						/*
						 * accedo a la BD para que me devuelva los ingresos y
						 * gastos en sus respectivas Listas
						 */

						listadoValoresIngresos = dba
								.listadoValoresIngresosPorFecha(
										singleton_fechas.obtenerInicioEnteroFechaMesActual(),
										singleton_fechas.obtenerFinEnteroFechaMesActual(),filtroIngresos);
						listadoValoresGastos = dba
								.listadoValoresGastosPorFecha(
										singleton_fechas.obtenerInicioEnteroFechaMesActual(),
										singleton_fechas.obtenerFinEnteroFechaMesActual(),filtroGastos);

						cargarLayoutListadoIngresos();
						cargarLayoutListadoGastos();

						/*
						 * Calculo los totales y la diferencia y los actualizo
						 * en sus variables
						 */
						setTotalIngresos(calcularTotalIngresos());
						setTotalGastos(calcularTotalGastos());

						/*
						 * Actualizo los TextView que contienen los totales
						 */
						actualizarTotales();
						
						// Indico que estoy visualizando el mes actual
						month = 1;

					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO no hago nada
			}
		});

		// Cierro la Base de datos
		dba.close();
		
	}

	/*
	 * Método que me carga en el Layout de listado de Ingresos las fechas,
	 * conceptos y valores asociados a partir de la consulta realizada a la Base
	 * de datos Mostrará valores del mes actual o el mes anterior
	 */
	private void cargarLayoutListadoIngresos() {

		int fechaAnterior = 00000000;

		RelativeLayout rowViewFechas = null;
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

			if (fechaAnterior != listadoValoresIngresos.get(i).getIdFecha()) {

				fechaAnterior = listadoValoresIngresos.get(i).getIdFecha();

				rowViewFechas = (RelativeLayout) LayoutInflater
						.from(this)
						.inflate(
								this.getResources()
										.getLayout(
												R.layout.listado_informes_encabezado_fecha),
								null);

				TextView tvFecha = (TextView) rowViewFechas
						.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
				tvFecha.setText(singleton_fechas.obtenerCadenaFecha(this,listadoValoresIngresos
						.get(i).getIdFecha()));

				// Inserto el layout de fecha como cabecera
				llListadoIngresos.addView(rowViewFechas);

				// Inserto el contenido
				llListadoIngresos.addView(rowViewContenido);

			} else {

				// Inserto el contenido
				llListadoIngresos.addView(rowViewContenido);

			}
		}

	}

	/*
	 * Método que me carga en el Layout de listado de Gastos las fechas,
	 * conceptos y valores asociados a partir de la consulta realizada a la Base
	 * de datos Mostrará valores de la semana actual o la semana anterior
	 */
	private void cargarLayoutListadoGastos() {

		int fechaAnterior = 00000000;

		RelativeLayout rowViewFechas = null;
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

			if (fechaAnterior != listadoValoresGastos.get(i).getIdFecha()) {

				fechaAnterior = listadoValoresGastos.get(i).getIdFecha();

				rowViewFechas = (RelativeLayout) LayoutInflater
						.from(this)
						.inflate(
								this.getResources()
										.getLayout(
												R.layout.listado_informes_encabezado_fecha),
								null);

				TextView tvFecha = (TextView) rowViewFechas
						.findViewById(R.id.textViewEncabezadoFechaListadoInformes);
				tvFecha.setText(singleton_fechas.obtenerCadenaFecha(this,listadoValoresGastos.get(i)
						.getIdFecha()));

				// Inserto el layout de fecha como cabecera
				llListadoGastos.addView(rowViewFechas);

				// Inserto el contenido
				llListadoGastos.addView(rowViewContenido);

			} else {

				// Inserto el contenido
				llListadoGastos.addView(rowViewContenido);

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
				+ numeroAFormatear.format(getTotalIngresos()) + " " + tipoMoneda);
		tvTotalIngresos.setTextColor(this.getResources().getColor(
				R.color.ListadosVerdeOscuro));

		tvTotalGastos.setText(" " + numeroAFormatear.format(getTotalGastos())
				+ " " + tipoMoneda);
		tvTotalGastos.setTextColor(this.getResources().getColor(
				R.color.ListadosRojo));

		tvTotalBalance.setText(" " + numeroAFormatear.format(balance) + " " + tipoMoneda);
		if (balance >= 0) {
			tvTotalBalance.setTextColor(this.getResources().getColor(
					R.color.ListadosVerdeOscuro));
		} else {
			tvTotalBalance.setTextColor(this.getResources().getColor(
					R.color.ListadosRojo));
		}

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
				 *  Obtengo los filtros pasados por parametros de la Activity InformesScreenActivity
				 *  en función del filtro a aplicar
				 */
				tipoFiltroAplicar = intent.getExtras().getString("tipo_concepto");
				
				if(tipoFiltroAplicar.equals(TIPO_CONCEPTO_INGRESO)){
					filtroIngresos = intent.getExtras().getParcelableArrayList(TIPO_FILTRO_INGRESO);
				}else{
					filtroGastos = intent.getExtras().getParcelableArrayList(TIPO_FILTRO_GASTO);
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
		
		if(primeraVez){
			primeraVez = false;
		}else{
			/*
			 * Quito el registro de mi BroadcastReceiver cuando mi Actividad pasa de
			 * estar oculta (o no Activa) por otra Actividad o aplicación a visible
			 * (o Activa)
			 */
			this.unregisterReceiver(myReceiver);
		}
		
		if (aplicarFiltro){
			
			// Primer if para visualizar el mes pasado
			if (this.month == -1){
				
				/*
				 * accedo a la BD para que me devuelva los ingresos y
				 * gastos en sus respectivas Listas en función del filtro a 
				 * aplicar
				 */
				
				if(tipoFiltroAplicar.equals(TIPO_CONCEPTO_INGRESO)){
					listadoValoresIngresos = dba
							.listadoValoresIngresosPorFecha(
									singleton_fechas.obtenerInicioEnteroFechaMesAnterior(),
									singleton_fechas.obtenerFinEnteroFechaMesAnterior(),filtroIngresos);
					cargarLayoutListadoIngresos();
					/*
					 * Calculo los totales y la diferencia y los actualizo
					 * en sus variables
					 */
					setTotalIngresos(calcularTotalIngresos());
					
					
				}else{
					listadoValoresGastos = dba
							.listadoValoresGastosPorFecha(
									singleton_fechas.obtenerInicioEnteroFechaMesAnterior(),
									singleton_fechas.obtenerFinEnteroFechaMesAnterior(),filtroGastos);
					
					cargarLayoutListadoGastos();					
					setTotalGastos(calcularTotalGastos());
				}

				/*
				 * Actualizo los TextView que contienen los totales
				 */
				actualizarTotales();
				
			}else{//Segundo if para visualizar el mes actual
				
				/*
				 * accedo a la BD para que me devuelva los ingresos y
				 * gastos en sus respectivas Listas en funcion del tipo
				 * de filtro de concepto a aplicar
				 */

				if(tipoFiltroAplicar.equals(TIPO_CONCEPTO_INGRESO)){
					listadoValoresIngresos = dba
							.listadoValoresIngresosPorFecha(
									singleton_fechas.obtenerInicioEnteroFechaMesActual(),
									singleton_fechas.obtenerFinEnteroFechaMesActual(),filtroIngresos);
					cargarLayoutListadoIngresos();
					/*
					 * Calculo los totales y la diferencia y los actualizo
					 * en sus variables
					 */
					setTotalIngresos(calcularTotalIngresos());
					
				}else{
					listadoValoresGastos = dba
							.listadoValoresGastosPorFecha(
									singleton_fechas.obtenerInicioEnteroFechaMesActual(),
									singleton_fechas.obtenerFinEnteroFechaMesActual(),filtroGastos);
					cargarLayoutListadoGastos();				
					setTotalGastos(calcularTotalGastos());
				}
				/*
				 * Actualizo los TextView que contienen los totales
				 */
				actualizarTotales();
			}
			aplicarFiltro = false;
		}		
		
		super.onResume();
	}
}