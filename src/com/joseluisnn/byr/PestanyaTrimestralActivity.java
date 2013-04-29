package com.joseluisnn.byr;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.ValoresElementoListaGD;

public class PestanyaTrimestralActivity extends Activity {

	/*
	 * Me creo una serie de constantes para buscar el Trimestre que quiera
	 */
	private static final int TRIMESTRE1_ANYO_ANTERIOR = 0;
	private static final int TRIMESTRE2_ANYO_ANTERIOR = 1;
	private static final int TRIMESTRE3_ANYO_ANTERIOR = 2;
	private static final int TRIMESTRE4_ANYO_ANTERIOR = 3;
	private static final int TRIMESTRE1_ANYO_ACTUAL = 4;
	private static final int TRIMESTRE2_ANYO_ACTUAL = 5;
	private static final int TRIMESTRE3_ANYO_ACTUAL = 6;
	private static final int TRIMESTRE4_ANYO_ACTUAL = 7;

	// Objetos View
	private Spinner anyos;
	private Spinner trimestres;
	private TextView tvTotalIngresos;
	private TextView tvTotalGastos;
	private TextView tvTotalBalance;
	private LinearLayout llListadoIngresos;
	private LinearLayout llListadoGastos;
	private ImageView ivFlechaListadoIngresos;
	private ImageView ivFlechaListadoGastos;
	private ImageView ivSearchTrimestre;
	// Variables que me despliegan/pliegan los Listados de Ingresos y Gastos
	private RelativeLayout rlBarraListadoIngresos;
	private RelativeLayout rlBarraListadoGastos;
	// Adaptador para el spinner los anyos y trimestres
	private ArrayAdapter<String> adapterAnyos;
	private ArrayAdapter<String> adapterTrimestres;
	// Lista para el spinner de los anyos y trimestres
	private ArrayList<String> listaAnyos;
	private ArrayList<String> listaTrimestres;

	// Variable para la BASE DE DATOS
	private DBAdapter dba;

	// Listas para los Ingresos y los Gastos agrupados por TRIMESTRES
	ArrayList<ValoresElementoListaGD> listadoValoresIngresos;
	ArrayList<ValoresElementoListaGD> listadoValoresGastos;
	/*
	 * Listas para los Ingresos y los Gastos devueltos por la BD y que
	 * posteriormente volcaré en las listas de arriba agrupados los datos por
	 * TRIMESTRES, no por días
	 */
	ArrayList<ValoresElementoListaGD> listadoValoresIngresosAux;
	ArrayList<ValoresElementoListaGD> listadoValoresGastosAux;

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
	// private int posicionSpinnerAnyosAnterior;
	// private int posicionSpinnerTrimestresAnterior;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pestanya_trimestral);

		/*
		 * Instancio los objetos View
		 */
		anyos = (Spinner) findViewById(R.id.spinnerPestanyaITrimestralAnyos);
		trimestres = (Spinner) findViewById(R.id.spinnerPestanyaITrimestralTrimestres);
		tvTotalIngresos = (TextView) findViewById(R.id.textViewTotalIngresosTrimestral);
		tvTotalGastos = (TextView) findViewById(R.id.textViewTotalGastosTrimestral);
		tvTotalBalance = (TextView) findViewById(R.id.textViewTotalBalanceTrimestral);
		llListadoIngresos = (LinearLayout) findViewById(R.id.linearLayoutListadoIngresosTrimestral);
		llListadoGastos = (LinearLayout) findViewById(R.id.linearLayoutListadoGastosTrimestral);
		rlBarraListadoIngresos = (RelativeLayout) findViewById(R.id.relativeLayoutBarraListadoIngresosTrimestral);
		rlBarraListadoGastos = (RelativeLayout) findViewById(R.id.relativeLayoutBarraListadoGastosTrimestral);
		ivFlechaListadoIngresos = (ImageView) findViewById(R.id.imageViewListadoIngresosTrimestral);
		ivFlechaListadoGastos = (ImageView) findViewById(R.id.imageViewListadoGastosTrimestral);
		ivSearchTrimestre = (ImageView) findViewById(R.id.imageViewSearchTrimestre);

		// Instancio y creo los anyos en la lista
		listaAnyos = new ArrayList<String>();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -1);
		listaAnyos.add("" + c.get(Calendar.YEAR));
		c.add(Calendar.YEAR, 1);
		listaAnyos.add("" + c.get(Calendar.YEAR));
		// Instancio y creo el Adaptador para el spinner
		adapterAnyos = new ArrayAdapter<String>(this, R.layout.own_spinner,
				listaAnyos);
		adapterAnyos
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// le añado el adapter al spinner
		anyos.setAdapter(adapterAnyos);

		// Instancio y creo los trimestres en la lista
		listaTrimestres = new ArrayList<String>();
		listaTrimestres.add("1º Trimestre");
		listaTrimestres.add("2º Trimestre");
		listaTrimestres.add("3º Trimestre");
		listaTrimestres.add("4º Trimestre");
		// Instancio y creo el Adaptador para el spinner
		adapterTrimestres = new ArrayAdapter<String>(this,
				R.layout.own_spinner, listaTrimestres);
		adapterTrimestres
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// le añado el adapter al spinner
		trimestres.setAdapter(adapterTrimestres);

		// Instancio los formateadores de números
		separadores = new DecimalFormatSymbols();
		separadores.setDecimalSeparator(',');
		separadores.setGroupingSeparator('.');
		numeroAFormatear = new DecimalFormat("###,###.##", separadores);

		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(this);

		// Abro la Base de Datos solo en modo lectura
		dba.openREADWRITE();

		/*
		 * accedo a la BD para que me devuelva los ingresos y gastos en sus
		 * respectivas Listas según el Trimestre en que me encuentre Siempre
		 * iniciaré el Informe con el Tirmestre anterior al que me encuentro
		 */
		Calendar cal = Calendar.getInstance();

		int mesActual = cal.get(Calendar.MONTH);

		if (mesActual >= 0 && mesActual <= 2) {
			listadoValoresIngresosAux = dba
					.listadoValoresIngresosPorFecha(
							obtenerInicioEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR),
							obtenerFinEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR));
			listadoValoresGastosAux = dba
					.listadoValoresGastosPorFecha(
							obtenerInicioEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR),
							obtenerFinEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR));
			// Situo los Combo correctamente
			anyos.setSelection(0);
			trimestres.setSelection(3);

		} else if (mesActual >= 3 && mesActual <= 5) {
			listadoValoresIngresosAux = dba.listadoValoresIngresosPorFecha(
					obtenerInicioEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL),
					obtenerFinEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL));
			listadoValoresGastosAux = dba.listadoValoresGastosPorFecha(
					obtenerInicioEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL),
					obtenerFinEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL));
			// Situo los Combo correctamente
			anyos.setSelection(1);
			trimestres.setSelection(0);

		} else if (mesActual >= 6 && mesActual <= 8) {
			listadoValoresIngresosAux = dba.listadoValoresIngresosPorFecha(
					obtenerInicioEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL),
					obtenerFinEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL));
			listadoValoresGastosAux = dba.listadoValoresGastosPorFecha(
					obtenerInicioEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL),
					obtenerFinEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL));
			// Situo los Combo correctamente
			anyos.setSelection(1);
			trimestres.setSelection(1);

		} else if (mesActual >= 9 && mesActual <= 11) {
			listadoValoresIngresosAux = dba.listadoValoresIngresosPorFecha(
					obtenerInicioEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL),
					obtenerFinEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL));
			listadoValoresGastosAux = dba.listadoValoresGastosPorFecha(
					obtenerInicioEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL),
					obtenerFinEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL));
			// Situo los Combo correctamente
			anyos.setSelection(1);
			trimestres.setSelection(2);
		}

		/*
		 * Agrupo las Lista por TRIMESTRES y luego cargo los layouts
		 */
		agruparListaIngresosPorMeses();
		cargarLayoutListadoIngresos();
		agruparListaGastosPorMeses();
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

		ivSearchTrimestre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Coge el click del botón y genera el informe según la
				// selección en los Spinner

				// Obtengo el mes actual
				Calendar cal = Calendar.getInstance();
				int mesActual = cal.get(Calendar.MONTH);
				boolean cargarPantalla = true;

				if (anyos.getSelectedItemPosition() == 0
						&& trimestres.getSelectedItemPosition() == 0) {

					// Cargo las listas con los valores recuperados de la BD del
					// 1º Trimestre del Año Anterior
					listadoValoresIngresosAux = dba
							.listadoValoresIngresosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE1_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE1_ANYO_ANTERIOR));
					listadoValoresGastosAux = dba
							.listadoValoresGastosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE1_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE1_ANYO_ANTERIOR));

				} else if (anyos.getSelectedItemPosition() == 0
						&& trimestres.getSelectedItemPosition() == 1) {

					// Cargo las listas con los valores recuperados de la BD del
					// 2º Trimestre del Año Anterior
					listadoValoresIngresosAux = dba
							.listadoValoresIngresosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE2_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE2_ANYO_ANTERIOR));
					listadoValoresGastosAux = dba
							.listadoValoresGastosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE2_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE2_ANYO_ANTERIOR));

				} else if (anyos.getSelectedItemPosition() == 0
						&& trimestres.getSelectedItemPosition() == 2) {

					// Cargo las listas con los valores recuperados de la BD del
					// 3º Trimestre del Año Anterior
					listadoValoresIngresosAux = dba
							.listadoValoresIngresosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE3_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE3_ANYO_ANTERIOR));
					listadoValoresGastosAux = dba
							.listadoValoresGastosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE3_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE3_ANYO_ANTERIOR));

				} else if (anyos.getSelectedItemPosition() == 0
						&& trimestres.getSelectedItemPosition() == 3) {

					// Cargo las listas con los valores recuperados de la BD del
					// 4º Trimestre del Año Anterior
					listadoValoresIngresosAux = dba
							.listadoValoresIngresosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR));
					listadoValoresGastosAux = dba
							.listadoValoresGastosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE4_ANYO_ANTERIOR));

				} else if (anyos.getSelectedItemPosition() == 1
						&& trimestres.getSelectedItemPosition() == 0) {

					// Cargo las listas con los valores recuperados de la BD del
					// 1º Trimestre del Año Actual
					listadoValoresIngresosAux = dba
							.listadoValoresIngresosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL));
					listadoValoresGastosAux = dba
							.listadoValoresGastosPorFecha(
									obtenerInicioEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL),
									obtenerFinEnteroFechaTrimestre(TRIMESTRE1_ANYO_ACTUAL));

				} else if (anyos.getSelectedItemPosition() == 1
						&& trimestres.getSelectedItemPosition() == 1) {

					if (mesActual <= 2) {
						lanzarAdvertencia("No se puede visualizar valores de previsión (en futuro). "
								+ "En los informes, sólo se visualizan valores pasados excepto en el informe libre.");
						cargarPantalla = false;
					} else {

						// Cargo las listas con los valores recuperados de la BD
						// del 2º Trimestre del Año Actual
						listadoValoresIngresosAux = dba
								.listadoValoresIngresosPorFecha(
										obtenerInicioEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL),
										obtenerFinEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL));
						listadoValoresGastosAux = dba
								.listadoValoresGastosPorFecha(
										obtenerInicioEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL),
										obtenerFinEnteroFechaTrimestre(TRIMESTRE2_ANYO_ACTUAL));
					}
				} else if (anyos.getSelectedItemPosition() == 1
						&& trimestres.getSelectedItemPosition() == 2) {

					if (mesActual <= 5) {
						lanzarAdvertencia("No se puede visualizar valores de previsión (en futuro). "
								+ "En los informes, sólo se visualizan valores pasados excepto en el informe libre.");
						cargarPantalla = false;
					} else {
						// Cargo las listas con los valores recuperados de la BD
						// del 3º Trimestre del Año Actual
						listadoValoresIngresosAux = dba
								.listadoValoresIngresosPorFecha(
										obtenerInicioEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL),
										obtenerFinEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL));
						listadoValoresGastosAux = dba
								.listadoValoresGastosPorFecha(
										obtenerInicioEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL),
										obtenerFinEnteroFechaTrimestre(TRIMESTRE3_ANYO_ACTUAL));
					}
				} else if (anyos.getSelectedItemPosition() == 1
						&& trimestres.getSelectedItemPosition() == 3) {
					if (mesActual <= 8) {
						lanzarAdvertencia("No se puede visualizar valores de previsión (en futuro). "
								+ "En los informes, sólo se visualizan valores pasados excepto en el informe libre.");
						cargarPantalla = false;
					} else {
						// Cargo las listas con los valores recuperados de la BD
						// del 4º Trimestre del Año Actual
						listadoValoresIngresosAux = dba
								.listadoValoresIngresosPorFecha(
										obtenerInicioEnteroFechaTrimestre(TRIMESTRE4_ANYO_ACTUAL),
										obtenerFinEnteroFechaTrimestre(TRIMESTRE4_ANYO_ACTUAL));
						listadoValoresGastosAux = dba
								.listadoValoresGastosPorFecha(
										obtenerInicioEnteroFechaTrimestre(TRIMESTRE4_ANYO_ACTUAL),
										obtenerFinEnteroFechaTrimestre(TRIMESTRE4_ANYO_ACTUAL));
					}
				}

				// Entro en el if si se han cargado las listas con los valores
				if (cargarPantalla) {
					/*
					 * Agrupo las Lista por TRIMESTRES y luego cargo los layouts
					 */
					agruparListaIngresosPorMeses();
					cargarLayoutListadoIngresos();
					agruparListaGastosPorMeses();
					cargarLayoutListadoGastos();
					/*
					 * Calculo los totales y la diferencia y los actualizo en
					 * sus variables
					 */
					setTotalIngresos(calcularTotalIngresos());
					setTotalGastos(calcularTotalGastos());
					/*
					 * Actualizo los TextView que contienen los totales
					 */
					actualizarTotales();

				} else {
					// borro el contenido de los Layouts
					llListadoIngresos.removeAllViews();
					llListadoGastos.removeAllViews();
					// Pongo a vacía los totales
					tvTotalIngresos.setText("");
					tvTotalGastos.setText("");
					tvTotalBalance.setText("");
				}

			}
		});

		// Cierro la Base de datos
		dba.close();

	}

	/*
	 * Método que me devuelve la fecha de inicio en un entero correspondiente al
	 * trimestre pedido por parámetro
	 */
	private int obtenerInicioEnteroFechaTrimestre(int trimestre) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		Calendar c = Calendar.getInstance();

		switch (trimestre) {

		case TRIMESTRE1_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 1º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE2_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 2º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 3);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE3_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 6);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE4_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 4º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE1_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 1º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE2_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 2º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 3);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE3_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 6);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE4_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);

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
	 * Método que me devuelve la fecha de fin en un entero correspondiente al
	 * trimestre pedido por parámetro
	 */
	private int obtenerFinEnteroFechaTrimestre(int trimestre) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		Calendar c = Calendar.getInstance();

		int mesActual = c.get(Calendar.MONTH);

		switch (trimestre) {

		case TRIMESTRE1_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 1º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 3);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE2_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 2º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 6);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE3_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 3º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE4_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 4º Trimestre del año
			// anterior
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE1_ANYO_ACTUAL:

			if (mesActual > 2) {
				// Situo la variable Calendar en el final del 1º Trimestre del
				// año
				// actual
				c.set(Calendar.MONTH, 0);
				c.add(Calendar.MONTH, 3);
				c.set(Calendar.DATE, 1);
				c.add(Calendar.DATE, -1);
			}
			break;
		case TRIMESTRE2_ANYO_ACTUAL:

			if (mesActual > 5) {
				// Situo la variable Calendar en el inicio del 2º Trimestre del
				// año
				// actual
				c.set(Calendar.MONTH, 0);
				c.add(Calendar.MONTH, 6);
				c.set(Calendar.DATE, 1);
				c.add(Calendar.DATE, -1);
			}
			break;
		case TRIMESTRE3_ANYO_ACTUAL:

			if (mesActual > 8) {
				// Situo la variable Calendar en el inicio del 3º Trimestre del
				// año
				// actual
				c.set(Calendar.MONTH, 0);
				c.add(Calendar.MONTH, 9);
				c.set(Calendar.DATE, 1);
				c.add(Calendar.DATE, -1);
			}
			break;
		case TRIMESTRE4_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// actual
			// c.add(Calendar.YEAR, 1);
			// c.set(Calendar.MONTH, 0);
			// c.set(Calendar.DATE, 1);
			// c.add(Calendar.DATE, -1);

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
	 * Método que me agrupa por meses los valores de la lista auxiliar en la
	 * lista de ingresos
	 */
	private void agruparListaIngresosPorMeses() {

		String fechaAnterior = new String("000000");
		// Variable para la fecha devuelta por la base de datos
		String fechaBD;
		// Variable que me servirá para sumar valores de un mismo concepto el
		// mismo mes
		String fechaParaCombinarValores;
		// double totalPorMeses = 0;
		int indiceListaPrincipal = 0;
		int iAux = 0;
		boolean encontrado = false;

		// if(listadoValoresIngresosAux.isEmpty()){

		if (listadoValoresIngresos == null) {
			listadoValoresIngresos = new ArrayList<ValoresElementoListaGD>();
		} else {

			listadoValoresIngresos.clear();

		}

		for (int i = 0; i < listadoValoresIngresosAux.size(); i++) {

			fechaBD = "" + listadoValoresIngresosAux.get(i).getIdFecha();

			if (fechaBD.substring(0, 6).equals(fechaAnterior)) {
				/*
				 * Entra en este IF si coincide la fecha que estoy tratando de
				 * la BD (Año y mes: AAAAMM)con la fecha anteriormente leída Lo
				 * que tengo que controlar es sumar la cantidad al concepto de
				 * Ingreso correctamente
				 */
				while (iAux < indiceListaPrincipal && !encontrado) {

					if (listadoValoresIngresos.get(iAux).getIdConcepto() == listadoValoresIngresosAux
							.get(i).getIdConcepto()) {

						fechaParaCombinarValores = ("" + listadoValoresIngresos
								.get(iAux).getIdFecha()).substring(0, 6);

						if (fechaParaCombinarValores.equals(fechaBD.substring(
								0, 6))) {

							// Actualizo el total mensual del Concepto
							listadoValoresIngresos.get(iAux).setCantidad(
									listadoValoresIngresos.get(iAux)
											.getCantidad()
											+ listadoValoresIngresosAux.get(i)
													.getCantidad());
							encontrado = true;
						}
					}

					iAux++;

				}

				/*
				 * Entra en esta condición si el nuevo concepto leido tiene la
				 * misma fecha que el concepto anterior pero son diferentes
				 * conceptos por lo que me lo debe poner en otro registro de la
				 * Lista
				 */
				if (encontrado == false) {

					indiceListaPrincipal++;

					listadoValoresIngresos.add(new ValoresElementoListaGD(
							listadoValoresIngresosAux.get(i).getIdConcepto(),
							Integer.valueOf(fechaAnterior + "01").intValue(),
							listadoValoresIngresosAux.get(i).getConcepto(),
							listadoValoresIngresosAux.get(i).getCantidad()));

				}

				iAux = 0;
				encontrado = false;

			} else {

				fechaAnterior = fechaBD.substring(0, 6);

				indiceListaPrincipal++;

				// totalPorMeses = 0;

				listadoValoresIngresos.add(new ValoresElementoListaGD(
						listadoValoresIngresosAux.get(i).getIdConcepto(),
						Integer.valueOf(fechaAnterior + "01").intValue(),
						listadoValoresIngresosAux.get(i).getConcepto(),
						listadoValoresIngresosAux.get(i).getCantidad()));

			}
		}
	}

	/*
	 * Método que me agrupa por meses los valores de la lista auxiliar en la
	 * lista de GASTOS
	 */
	private void agruparListaGastosPorMeses() {

		String fechaAnterior = new String("000000");
		// Variable para la fecha devuelta por la base de datos
		String fechaBD;
		// Variable que me servirá para sumar valores de un mismo concepto el
		// mismo mes
		String fechaParaCombinarValores;
		// double totalPorMeses = 0;
		int indiceListaPrincipal = 0;
		int iAux = 0;
		boolean encontrado = false;

		if (listadoValoresGastos == null) {
			listadoValoresGastos = new ArrayList<ValoresElementoListaGD>();
		} else {

			listadoValoresGastos.clear();

		}

		for (int i = 0; i < listadoValoresGastosAux.size(); i++) {

			fechaBD = "" + listadoValoresGastosAux.get(i).getIdFecha();

			if (fechaBD.substring(0, 6).equals(fechaAnterior)) {
				/*
				 * Entra en este IF si coincide la fecha que estoy tratando de
				 * la BD (Año y mes: AAAAMM)con la fecha anteriormente leída Lo
				 * que tengo que controlar es sumar la cantidad al concepto de
				 * Gasto correctamente
				 */
				while (iAux < indiceListaPrincipal && !encontrado) {

					if (listadoValoresGastos.get(iAux).getIdConcepto() == listadoValoresGastosAux
							.get(i).getIdConcepto()) {

						fechaParaCombinarValores = ("" + listadoValoresGastos
								.get(iAux).getIdFecha()).substring(0, 6);

						if (fechaParaCombinarValores.equals(fechaBD.substring(
								0, 6))) {
							// Actualizo el total mensual del Concepto
							listadoValoresGastos.get(iAux).setCantidad(
									listadoValoresGastos.get(iAux)
											.getCantidad()
											+ listadoValoresGastosAux.get(i)
													.getCantidad());
							encontrado = true;
						}

					}

					iAux++;

				}

				/*
				 * Entra en esta condición si el nuevo concepto leido tiene la
				 * misma fecha que el concepto anterior pero son difierntes
				 * conceptos por lo que me lo debe poner en otro registro de la
				 * Lista
				 */
				if (encontrado == false) {

					indiceListaPrincipal++;

					listadoValoresGastos.add(new ValoresElementoListaGD(
							listadoValoresGastosAux.get(i).getIdConcepto(),
							Integer.valueOf(fechaAnterior + "01").intValue(),
							listadoValoresGastosAux.get(i).getConcepto(),
							listadoValoresGastosAux.get(i).getCantidad()));

				}

				iAux = 0;
				encontrado = false;

			} else {

				fechaAnterior = fechaBD.substring(0, 6);

				indiceListaPrincipal++;

				// totalPorMeses = 0;

				listadoValoresGastos.add(new ValoresElementoListaGD(
						listadoValoresGastosAux.get(i).getIdConcepto(), Integer
								.valueOf(fechaAnterior + "01").intValue(),
						listadoValoresGastosAux.get(i).getConcepto(),
						listadoValoresGastosAux.get(i).getCantidad()));

			}
		}

	}

	/*
	 * Método que me carga en el Layout de listado de Ingresos las fechas,
	 * conceptos y valores asociados a partir de la consulta realizada a la Base
	 * de datos Mostrará valores del Año actual o el Año anterior
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
							.getCantidad()) + "€");

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
				tvFecha.setText(obtenerCadenaFecha(listadoValoresIngresos
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
							.getCantidad()) + "€");

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
				tvFecha.setText(obtenerCadenaFecha(listadoValoresGastos.get(i)
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
	 * Método para obtener la fecha en una Cadena Formateada para los Meses y
	 * Años
	 */
	private String obtenerCadenaFecha(int enteroFecha) {

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
