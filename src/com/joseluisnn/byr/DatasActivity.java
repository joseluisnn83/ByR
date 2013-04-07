package com.joseluisnn.byr;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.joseluisnn.byr.MyAdapterListaGestionDatos.ObservadorMyAdapterListViewGD;
import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.Concepto;
import com.joseluisnn.objetos.ValoresElementoListaGD;

public class DatasActivity extends Activity{
	
	/*
	 * Constantes para indicar el tipo de Dialog a crear
	 */
	private static final int DIALOGO_INSERTAR_INGRESO = 0;
	private static final int DIALOGO_ACTUALIZAR_INGRESO = 1;	
	private static final int DIALOGO_INSERTAR_GASTO = 2;
	private static final int DIALOGO_ACTUALIZAR_GASTO = 3;
	private static final int DIALOGO_BORRAR_INGRESO = 4;
	private static final int DIALOGO_BORRAR_GASTO = 5;
	
	/*
	 * Variables para el SPINNER de los Dialogs
	 */
	// Lista donde obtengo el CONCEPTO (id y nombre) y luego lo paso a los ArrayList de Nombre
	private ArrayList<Concepto> listaConceptosIngresos;
	private ArrayList<Concepto> listaConceptosGastos;
	// Listas para tener el idConcepto y el concepto para el sipnner del Dialog (insertar, actualizar) Ingresos
	private ArrayList<String> listaNombreConceptosIngresos;	
	// Lista para tener el idConcepto y el concepto para el sipnner del Dialog (insertar, actualizar) Gastos
	private ArrayList<String> listaNombreConceptosGastos;
	// Adaptadores para los spinner de conceptos de Ingresos y Gastos (listaNombreConceptosIngresos y listaNombreConceptosGastos)
	private ArrayAdapter<String> adapterConceptoIngresos;
	private ArrayAdapter<String> adapterConceptoGastos;	
	
	/*
	 * Variables para los LISTVIEWS
	 */	
	// Lista para los conceptos de ingresos y sus cantidades
	private ArrayList<ValoresElementoListaGD> listaIngresos;
	// Lista para los conceptos de gastos y sus cantidades
	private ArrayList<ValoresElementoListaGD> listaGastos;
	// Adaptadores para los dos ListView
	private MyAdapterListaGestionDatos adapterIngresos;
	private MyAdapterListaGestionDatos adapterGastos;		
	// Al tener 2 ListView debo hacer referencia a cada una de ellas
	private ListView lvIngresos;
	private ListView lvGastos;
	
	// Variable que me indica la posicion a borrar de un Item del ListView de Ingreso o Gasto
	private int posicioABorrar;
	//Variable que me indica la posicion a editar de un Item del ListView de Ingreso o Gasto
	private int posicioAEditar;
	
	// Variable TextView que me va a mostrar la fecha en la Actividad
	private TextView tvFecha;
	//Variables para al pulsar insertar Ingresos o Gastos
	private LinearLayout llAddIngresos;
	private LinearLayout llAddGastos;
	
	// Variable para la BASE DE DATOS
	private DBAdapter dba;
	
	// Variable tipo entero para saber la fecha pulsada en el calendario
	private int entero_fecha;
	// Variable tipo String para saber la fecha pulsada en el calendario
	private String cadena_fecha;
	// Variables ImageView para pasar las fechas de una en una
	private ImageView ivForwardDate;
	private ImageView ivBackDate;
	
	// Variable para el formato de los números DOUBLE
	private DecimalFormatSymbols separadores;
	private DecimalFormat numeroAFormatear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Método donde se crea la Actividad
		super.onCreate(savedInstanceState);
		
		/*
		 * Quitamos barra de titulo de la Actividad
		 * Debe ser ejecutada esta instruccion antes del setContentView para que no cargue las imágenes
		 */
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_datas);
		
		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(this);
		
		// Aquí debo recuperar los datos de la casilla del calendario pulsada con la clase Bundle
		// Recupero la fecha pasada por parámetro de la Actividad Anterior
		Bundle b = this.getIntent().getExtras();
		entero_fecha = b.getInt("entero_fecha");
		cadena_fecha = b.getString("cadena_fecha");
		// Instancio los ImageViews que servirán de pulsación para pasar de fechas alante o atrás
		ivForwardDate = (ImageView)findViewById(R.id.imageViewForwardSearch);
		ivBackDate = (ImageView)findViewById(R.id.imageViewBackSearch);
				
		// Abro la Base de Datos solo en modo lectura
		dba.openREADWRITE();
				
		/* accedo a la BD para que me devuelva los ingresos y gastos
		 * en sus respectivas Listas  
		 */
		listaIngresos = dba.obtenerValoresIngresos(entero_fecha);
		listaGastos = dba.obtenerValoresGastos(entero_fecha);
						
		// Instancio los Adaptadores para las dos ListView
		adapterIngresos = new MyAdapterListaGestionDatos(this, listaIngresos, "ingreso");
		adapterGastos = new MyAdapterListaGestionDatos(this, listaGastos, "gasto");
		/*
		 * Instancio el observador de los Adaptadores del los ListViews para cuando 
		 * se pulsen los botones de borrar o editar y poder realizar la acción 
		 * correspondiente
		 */
		adapterIngresos.setObservadorMyAdapterListViewGD(new ObservadorMyAdapterListViewGD() {
			
			@Override
			public void editar(int position) {
				// TODO Edito el Item del ListView de Ingresos
				posicioAEditar = position;
				Dialog d = onCreateDialog(DIALOGO_ACTUALIZAR_INGRESO);
				
				d.show();
				
			}
			
			@Override
			public void borrar(int position) {
				// TODO Borro el Item del ListView de Ingresos
				posicioABorrar = position;
				Dialog d = onCreateDialog(DIALOGO_BORRAR_INGRESO);
				
				d.show();
				
			}
		});
		
		adapterGastos.setObservadorMyAdapterListViewGD(new ObservadorMyAdapterListViewGD() {
			
			@Override
			public void editar(int position) {
				// TODO Edito el Item del ListView de Gastos
				posicioAEditar = position;
				Dialog d = onCreateDialog(DIALOGO_ACTUALIZAR_GASTO);
				
				d.show();
				
				//lanzarMensaje("Editar Gasto :" + position);
				
			}
			
			@Override
			public void borrar(int position) {
				// TODO Borro el Item del ListView de Gastos
				posicioABorrar = position;
				Dialog d = onCreateDialog(DIALOGO_BORRAR_GASTO);
				
				d.show();
				//lanzarMensaje("Borrar Gasto :" + position);
				
			}
		});
		
		lvIngresos = (ListView)findViewById(R.id.listIngresos);
		lvGastos = (ListView)findViewById(R.id.listGastos);
		// Le asigno las vistas (TextView), a las dos listas cuando se encuentren vacías
		lvIngresos.setEmptyView(findViewById(R.id.empyViewIngresos));
		lvGastos.setEmptyView(findViewById(R.id.empyViewGastos));
		
		tvFecha = (TextView)findViewById(R.id.textViewGDFecha);
		llAddIngresos = (LinearLayout)findViewById(R.id.LinearLayoutGDAddIngresos);
		llAddGastos = (LinearLayout)findViewById(R.id.LinearLayoutGDAddGastos);
		
		// Inserto la fecha en el TextView de arriba de la Actividad
		tvFecha.setText(cadena_fecha);
		
		// Instancio el adaptador a cada ListView
		lvIngresos.setAdapter(adapterIngresos);
		lvGastos.setAdapter(adapterGastos);
		
		// Cargo las listas para los CONCEPTOS 
		listaConceptosIngresos = dba.listarConceptos("ingreso");
		listaConceptosGastos = dba.listarConceptos("gasto");
		// Los vuelco en las listas respectivas solo su Nombre para luego insertarlo en el adaptador propio
		listaNombreConceptosIngresos = new ArrayList<String>();
		listaNombreConceptosGastos = new ArrayList<String>();
		for(int i=0;i<listaConceptosIngresos.size();i++){
			listaNombreConceptosIngresos.add(listaConceptosIngresos.get(i).getNombre());
		}
		for(int i=0;i<listaConceptosGastos.size();i++){
			listaNombreConceptosGastos.add(listaConceptosGastos.get(i).getNombre());
		}
		
		adapterConceptoIngresos = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_spinner_item, 
				listaNombreConceptosIngresos);
		adapterConceptoIngresos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		adapterConceptoGastos = new ArrayAdapter<String>(
				this, 
				android.R.layout.simple_spinner_item, 
				listaNombreConceptosGastos);
		adapterConceptoGastos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
		/*
		 * Evento OnClick del  ImageView para insertar Ingresos
		 */
		llAddIngresos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Llamo al adapter de base de datos para insertar el ingreso
				
				if (!listaConceptosIngresos.isEmpty()){
					
					Dialog d = onCreateDialog(DIALOGO_INSERTAR_INGRESO);
				
					d.show();
				}else{
					lanzarMensaje("Para insertar valores debe\n crear los conceptos de INGRESOS\n " +
							"en la configuración del programa !!!");
				}
			}
		});
		
		/*
		 * Evento OnClick del  ImageView para insertar Gastos
		 */
		llAddGastos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Llamo al adapter de base de datos para insertar el gasto
				
				if (!listaConceptosGastos.isEmpty()){
					
					Dialog d = onCreateDialog(DIALOGO_INSERTAR_GASTO);					
				
					d.show();
					
				}else{
					lanzarMensaje("Para insertar valores debe\n crear los conceptos de GASTOS\n" +
							" en la configuración del programa !!!");
				}
				
				
			}
		});
		
		// Evento click del ImageView para ver la fecha del día siguiente
		ivForwardDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Incremento la fecha en un día y actualizo los dos ListViews
				
				/*
				 * Listas auxiliares para insertar los valores en las listas asociadas a los ListView
				 * y de esta manera que se vea el cambio de fecha con sus valores asociados
				 */
				ArrayList<ValoresElementoListaGD> listaIngresosAux;
				ArrayList<ValoresElementoListaGD> listaGastosAux;
				
				// Incremento la fecha
				obtenerDiaSiguiente();
				
				// Obtengo los valores para la nueva fecha
				listaIngresosAux = dba.obtenerValoresIngresos(obtenerEnteroFecha());
				listaGastosAux = dba.obtenerValoresGastos(obtenerEnteroFecha());
				
				/* 
				 * Actualizo los ListViews obteniendo los valores de las listas asociadas con la nueva fecha incrementada
				 */
				listaIngresos.clear();
				listaGastos.clear();
				
				// Cargo la Lista de Ingresos con los nuevos valores en caso de que no esté vacía la Auxiliar
				if(!listaIngresosAux.isEmpty()){
					for(int i=0;i<listaIngresosAux.size();i++){
						listaIngresos.add(listaIngresosAux.get(i));
					}
				}
				// Notifico a su ArrayAdapter que el ArrayList ha sido modificado
				adapterIngresos.notifyDataSetChanged();
				
				// Cargo la Lista de Ingresos con los nuevos valores en caso de que no esté vacía la Auxiliar
				if(!listaGastosAux.isEmpty()){
					for(int i=0;i<listaGastosAux.size();i++){
						listaGastos.add(listaGastosAux.get(i));
					}
				}
				// Igualmente lo notifico a su ArrayAdapter
				adapterGastos.notifyDataSetChanged();
				
				// Libero de memoria
				listaIngresosAux = null;
				listaGastosAux = null;
				
			}
		});
		
		// Evento click del ImageView para ver la fecha del día anterior
		ivBackDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Decremento la fecha en un día y actualizo los dos ListViews
				
				/*
				 * Listas auxiliares para insertar los valores en las listas asociadas a los ListView
				 * y de esta manera que se vea el cambio de fecha con sus valores asociados
				 */
				ArrayList<ValoresElementoListaGD> listaIngresosAux;
				ArrayList<ValoresElementoListaGD> listaGastosAux;
				
				// Decremento la fecha
				obtenerDiaAnterior();
				
				// Obtengo los valores para la nueva fecha
				listaIngresosAux = dba.obtenerValoresIngresos(obtenerEnteroFecha());
				listaGastosAux = dba.obtenerValoresGastos(obtenerEnteroFecha());
				
				/* 
				 * Actualizo los ListViews obteniendo los valores de las listas asociadas con la nueva fecha incrementada
				 */
				listaIngresos.clear();
				listaGastos.clear();
				
				// Cargo la Lista de Ingresos con los nuevos valores en caso de que no esté vacía la Auxiliar
				if(!listaIngresosAux.isEmpty()){
					for(int i=0;i<listaIngresosAux.size();i++){
						listaIngresos.add(listaIngresosAux.get(i));
					}
				}
				// Notifico a su ArrayAdapter que el ArrayList ha sido modificado
				adapterIngresos.notifyDataSetChanged();
				
				// Cargo la Lista de Ingresos con los nuevos valores en caso de que no esté vacía la Auxiliar
				if(!listaGastosAux.isEmpty()){
					for(int i=0;i<listaGastosAux.size();i++){
						listaGastos.add(listaGastosAux.get(i));
					}
				}
				// Igualmente lo notifico a su ArrayAdapter
				adapterGastos.notifyDataSetChanged();
				
				// Libero de memoria
				listaIngresosAux = null;
				listaGastosAux = null;
				
			}
		});
		
		// Instancio los formateadores de números
		separadores = new DecimalFormatSymbols();
		separadores.setDecimalSeparator(',');
		separadores.setGroupingSeparator('.');
		numeroAFormatear = new DecimalFormat("###,###.##", separadores);
		
		// Cierro la Base de datos
		dba.close();
		
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


	@Override
	protected Dialog onCreateDialog(int tipoDialogo) {
		/* TODO 
		 * Método donde según el tipoDialogo pasado por parámetro, me creo el Dialog 
		 * correspondiente
		 */
		
		Dialog dialogo;
		
		switch(tipoDialogo)
	    {
	        case DIALOGO_INSERTAR_INGRESO:
	            dialogo = crearDialogoInsertarIngreso();
	            break;
	        case DIALOGO_ACTUALIZAR_INGRESO:
	            dialogo = crearDialogoActualizarIngreso();
	            break;
	        case DIALOGO_INSERTAR_GASTO:
	            dialogo = crearDialogoInsertarGasto();
	            break;
	        case DIALOGO_ACTUALIZAR_GASTO:
	            dialogo = crearDialogoActualizarGasto();
	            break;
	        case DIALOGO_BORRAR_INGRESO:
	            dialogo = crearDialogBorrarValorIngreso();
	            break;
	        case DIALOGO_BORRAR_GASTO:
	            dialogo = crearDialogBorrarValorGasto();
	            break;
	        default:
	        	dialogo =null; // new Dialog(this);
	            break;
	    }
	 
	    return dialogo;
		
	}
	
	/*
	 * Dialog para INSERTAR un concepto de Ingreso
	 */
	private Dialog crearDialogoInsertarIngreso(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();
	    View layout = inflater.inflate(R.layout.dialog_insertar_ingreso, null);
	    
		final Spinner spinnerConceptosIngresos = (Spinner)layout.findViewById(R.id.spinnerDialogInsertarIngreso);
		final EditText valorIngreso = (EditText)layout.findViewById(R.id.editTextDialogInsertarIngreso);
		
		// Le inserto el layout al Dialog con LayoutInflater
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(layout);
	    
		// Cargo el spinner con el adaptador
		spinnerConceptosIngresos.setAdapter(adapterConceptoIngresos);
		spinnerConceptosIngresos.setSelection(0);
		
		builder.setTitle("Inserta Ingreso");
		
		//builder.setView(valorIngreso);
		builder.setIcon(android.R.drawable.ic_menu_add);
		
		builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
									
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO inserto en la BD el concepto insertado por el usuario
				
				if(!valorIngreso.getText().toString().equals("")){

					// Doy formato double al valor introducido
					double valor = Double.valueOf(valorIngreso.getText().toString());
					
					// lo inserto en la BD					
					if (dba.insertarValor(
							valor, 
							listaConceptosIngresos.get(spinnerConceptosIngresos.getSelectedItemPosition()).getId(), 
							entero_fecha) == 1){
						
						ValoresElementoListaGD v = new ValoresElementoListaGD(
								listaConceptosIngresos.get(spinnerConceptosIngresos.getSelectedItemPosition()).getId(), 
								entero_fecha, 
								listaConceptosIngresos.get(spinnerConceptosIngresos.getSelectedItemPosition()).getNombre(), 
								valor);
												
						// Lo inserto en la lista de Ingresos 
						listaIngresos.add(v);
						
						// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
						adapterIngresos.notifyDataSetChanged();
							
					}
				}
			}
		});
		
		builder.setNegativeButton(R.string.botonCancelar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO No hago nada
				dialog.cancel();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * Dialog para ACTUALIZAR un concepto de Ingreso
	 */
	private Dialog crearDialogoActualizarIngreso(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		/*
		 *  Get the layout inflater ==> Va a ser los mismos elementos gráficos que para insertar
		 */
	    LayoutInflater inflater = this.getLayoutInflater();
	    View layout = inflater.inflate(R.layout.dialog_insertar_ingreso, null);
	    
		final Spinner spinnerConceptosIngresos = (Spinner)layout.findViewById(R.id.spinnerDialogInsertarIngreso);
		final EditText valorIngreso = (EditText)layout.findViewById(R.id.editTextDialogInsertarIngreso);
		
		// Le inserto el layout al Dialog con LayoutInflater
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(layout);
	    
		// Cargo el spinner con el adaptador
		spinnerConceptosIngresos.setAdapter(adapterConceptoIngresos);
		// Inicializo su valor
		spinnerConceptosIngresos.setSelection(obtenerIdConceptoSpinner(0));
		// Inicializo valor del EditText valor
		valorIngreso.setText(""+listaIngresos.get(posicioAEditar).getCantidad());
		
		// propiedades del Dialog
		builder.setTitle("Editar Ingreso");
		builder.setIcon(android.R.drawable.ic_menu_edit);
		
		builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
									
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO inserto en la BD el concepto insertado por el usuario
				
				if(!valorIngreso.getText().toString().equals("")){

					// Doy formato double al valor introducido
					double valor = Double.valueOf(valorIngreso.getText().toString());
					// Posicion del Item seleccionado del Spinner 
					int posicionSpinner = spinnerConceptosIngresos.getSelectedItemPosition();
					
					// lo actualizo en la BD					
					if (dba.actualizarValor(
							valor, 
							listaIngresos.get(posicioAEditar).getIdConcepto(),
							listaConceptosIngresos.get(posicionSpinner).getId(), 
							entero_fecha) == 1){
							
						// Lo actualizo en la lista de Ingresos
						listaIngresos.get(posicioAEditar).setIdConcepto(
								listaConceptosIngresos.get(posicionSpinner).getId());
						listaIngresos.get(posicioAEditar).setConcepto(
								listaConceptosIngresos.get(posicionSpinner).getNombre());
						listaIngresos.get(posicioAEditar).setCantidad(valor);
							
						// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
						adapterIngresos.notifyDataSetChanged();
							
					}
				}
			}
		});
		
		builder.setNegativeButton(R.string.botonCancelar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO No hago nada
				dialog.cancel();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * Dialog para INSERTAR un concepto de Gasto
	 */
	private Dialog crearDialogoInsertarGasto(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();
	    View layout = inflater.inflate(R.layout.dialog_insertar_gasto, null);
	    
		final Spinner spinnerConceptosGastos = (Spinner)layout.findViewById(R.id.spinnerDialogInsertarGasto);
		final EditText valorGasto = (EditText)layout.findViewById(R.id.editTextDialogInsertarGasto);
		
		// Le inserto el layout al Dialog con LayoutInflater
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(layout);
	    
		// Cargo el spinner con el adaptador
		spinnerConceptosGastos.setAdapter(adapterConceptoGastos);
		spinnerConceptosGastos.setSelection(0);
		
		builder.setTitle("Inserta Gasto");
		
		//builder.setView(valorIngreso);
		builder.setIcon(android.R.drawable.ic_menu_add);
		
		builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
									
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO inserto en la BD el concepto insertado por el usuario
				
				if(!valorGasto.getText().toString().equals("")){

					// Doy formato double al valor introducido
					double valor = Double.valueOf(valorGasto.getText().toString());
					
					// lo inserto en la BD					
					if (dba.insertarValor(
							valor, 
							listaConceptosGastos.get(spinnerConceptosGastos.getSelectedItemPosition()).getId(), 
							entero_fecha) == 1){
							
						ValoresElementoListaGD v = new ValoresElementoListaGD(
								listaConceptosGastos.get(spinnerConceptosGastos.getSelectedItemPosition()).getId(), 
								entero_fecha, 
								listaConceptosGastos.get(spinnerConceptosGastos.getSelectedItemPosition()).getNombre(), 
								valor);
							
						// Lo inserto en la lista de Gastos 
						listaGastos.add(v);
							
						// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
						adapterGastos.notifyDataSetChanged();
							
					}
				}
			}
		});
		
		builder.setNegativeButton(R.string.botonCancelar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO No hago nada
				dialog.cancel();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * Dialog para ACTUALIZAR un concepto de Gasto
	 */
	private Dialog crearDialogoActualizarGasto(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		/*
		 *  Get the layout inflater ==> Va a ser los mismos elementos gráficos que para insertar
		 */
	    LayoutInflater inflater = this.getLayoutInflater();
	    View layout = inflater.inflate(R.layout.dialog_insertar_gasto, null);
	    
		final Spinner spinnerConceptosGastos = (Spinner)layout.findViewById(R.id.spinnerDialogInsertarGasto);
		final EditText valorGasto = (EditText)layout.findViewById(R.id.editTextDialogInsertarGasto);
		
		// Le inserto el layout al Dialog con LayoutInflater
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(layout);
	    
		// Cargo el spinner con el adaptador
	    spinnerConceptosGastos.setAdapter(adapterConceptoGastos);
		// Inicializo su valor
	    spinnerConceptosGastos.setSelection(obtenerIdConceptoSpinner(1));
		// Inicializo valor del EditText valor
	    valorGasto.setText(""+listaGastos.get(posicioAEditar).getCantidad());
		
		// propiedades del Dialog
		builder.setTitle("Editar Gasto");
		builder.setIcon(android.R.drawable.ic_menu_edit);
		
		builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
									
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO inserto en la BD el concepto insertado por el usuario
				
				if(!valorGasto.getText().toString().equals("")){

					// Doy formato double al valor introducido
					double valor = Double.valueOf(valorGasto.getText().toString());
					// Posicion del Item seleccionado del Spinner 
					int posicionSpinner = spinnerConceptosGastos.getSelectedItemPosition();
					
					// lo actualizo en la BD					
					if (dba.actualizarValor(
							valor, 
							listaGastos.get(posicioAEditar).getIdConcepto(),
							listaConceptosGastos.get(posicionSpinner).getId(), 
							entero_fecha) == 1){
							
						// Lo actualizo en la lista de Gastos
						listaGastos.get(posicioAEditar).setIdConcepto(
								listaConceptosGastos.get(posicionSpinner).getId());
						listaGastos.get(posicioAEditar).setConcepto(
								listaConceptosGastos.get(posicionSpinner).getNombre());
						listaGastos.get(posicioAEditar).setCantidad(valor);
							
						// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
						adapterGastos.notifyDataSetChanged();
							
					}
				}
			}
		});
		
		builder.setNegativeButton(R.string.botonCancelar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO No hago nada
				dialog.cancel();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * Dialog para BORRAR un valor de un concepto de Ingreso
	 */
	private Dialog crearDialogBorrarValorIngreso()
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	    builder.setTitle("Confirmación");
	    builder.setIcon(android.R.drawable.ic_menu_delete);
	    builder.setMessage("¿Desea borrar el Ingreso de " + 
	    		listaIngresos.get(posicioABorrar).getConcepto().toUpperCase() +
	    		" de " + numeroAFormatear.format(listaIngresos.get(posicioABorrar).getCantidad()) + " € " +
	    		"seleccionado?");
	    
	    builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Borro en el ListView y la BD
				//lanzarMensaje("Borrar Ingreso: " + posicioABorrar);
				
				// Si el borrado es correcto entro en el if y elimino los valores del ListView
				if(dba.borrarValor(
						listaIngresos.get(posicioABorrar).getIdConcepto(), 
						entero_fecha)){
					
					// Lo borro de la Lista de valores asociada al ListView
					listaIngresos.remove(posicioABorrar);
					
					// Notifico el borrado al Adaptador del LitView
					adapterIngresos.notifyDataSetChanged();
					
				}
			}
		});
		
	    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO no hago nada
				dialog.cancel();
			}
		}); 
	 
	    return builder.create();
	}
	
	/*
	 * Dialog para BORRAR un concepto de Gasto
	 */
	private Dialog crearDialogBorrarValorGasto()
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	    builder.setTitle("Confirmación");
	    builder.setIcon(android.R.drawable.ic_menu_delete);
	    builder.setMessage("¿Desea borrar el Gasto de " + 
	    		listaGastos.get(posicioABorrar).getConcepto().toUpperCase() +
	    		" de " + numeroAFormatear.format(listaGastos.get(posicioABorrar).getCantidad()) + " € " +
	    		"seleccionado?");
	    
	    builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Borro en el ListView y la BD
				//lanzarMensaje("Borrar Gasto: " + posicioABorrar);
				
				// Si el borrado es correcto entro en el if y elimino los valores del ListView
				if(dba.borrarValor(
						listaGastos.get(posicioABorrar).getIdConcepto(), 
						entero_fecha)){
					
					// Lo borro de la Lista de valores asociada al ListView
					listaGastos.remove(posicioABorrar);
					
					// Notifico el borrado al Adaptador del LitView
					adapterGastos.notifyDataSetChanged();
					
				}
				
			}
		});
		
	    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO no hago nada
				dialog.cancel();
			}
		});
	 
	    return builder.create();
	}
	
	/*
	 * Método que me da la posición del concepto en el Spinner
	 * según el Valor a Actualizar
	 * @tipo: variable que me dice si es un ingreso o gasto
	 * 		0 = ingreso
	 * 		1 = gasto
	 */
	private int obtenerIdConceptoSpinner(int tipo){
		
		ValoresElementoListaGD elemento;
		int id=-1;
		
		if (tipo == 0){
			elemento = listaIngresos.get(posicioAEditar);
			
			for(int i=0; i<listaNombreConceptosIngresos.size(); i++){				
				if (listaNombreConceptosIngresos.get(i).toString().equals(elemento.getConcepto())){
					id = i;
				}
			}
			
		}else{
			elemento = listaGastos.get(posicioAEditar);
			
			for(int i=0; i<listaNombreConceptosGastos.size(); i++){				
				if (listaNombreConceptosGastos.get(i).toString().equals(elemento.getConcepto())){
					id = i;
				}
			}
		}
		
		return id; 
		
	}
	
	/*
	 * Obtengo la fecha entero
	 */
	private int obtenerEnteroFecha(){
		return this.entero_fecha;
	}
	
	/*
	 * Actualizo la fecha entero
	 */
	private void setEnteroFecha(int f){
		this.entero_fecha = f;
	}
	
	private void setCadenaFecha(String cadena){
		tvFecha.setText(cadena);
	}
	
	/*
	 * Método que incrementa en 1 DÍA la fecha que estoy tratando actualmente
	 */
	private void obtenerDiaSiguiente(){
		
		String fecha = new String("" + obtenerEnteroFecha());
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
		
		/*	Recupero la Fecha incrementada en 1 DÍA de la variable Calendar
		 *  Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 *  Si el mes solo tiene un dígito le pongo un cero delante
		 */		
		month = "" + (c.get(Calendar.MONTH)+1);
		if (month.length() == 1){
			month = "0" + month;
		}
		
		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if(day.length() == 1){
			day = "0" + day;
		}
		
		cadena_fecha = "" + obtenerDiaSemana(c.get(Calendar.DAY_OF_WEEK)) + ", " 
				+ c.get(Calendar.DAY_OF_MONTH) + " de "
				+ obtenerMes(c.get(Calendar.MONTH)) + " de " 
				+ c.get(Calendar.YEAR);
		
		// Actualizo la Fecha en el Título de la Activity
		this.setCadenaFecha(cadena_fecha);
		
		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) +  month + day;
		// Actualizo el valor de la fecha
		this.setEnteroFecha(Integer.valueOf(fecha));
		//entero_fecha = Integer.valueOf(fecha);
		
	}
	
	/*
	 * Método que decrementa en 1 DÍA la fecha que estoy tratando actualmente
	 */
	private void obtenerDiaAnterior(){
		
		String fecha = new String("" + obtenerEnteroFecha());
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
		// le resto 1 día
		c.add(Calendar.DATE, -1);
		
		/*	Recupero la Fecha incrementada en 1 DÍA de la variable Calendar
		 *  Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 *  Si el mes solo tiene un dígito le pongo un cero delante
		 */		
		month = "" + (c.get(Calendar.MONTH)+1);
		if (month.length() == 1){
			month = "0" + month;
		}
		
		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if(day.length() == 1){
			day = "0" + day;
		}
		
		cadena_fecha = "" + obtenerDiaSemana(c.get(Calendar.DAY_OF_WEEK)) + ", "  
				+ c.get(Calendar.DAY_OF_MONTH) + " de "
				+ obtenerMes(c.get(Calendar.MONTH)) + " de " 
				+ c.get(Calendar.YEAR);
		
		// Actualizo la Fecha en el Título de la Activity
		this.setCadenaFecha(cadena_fecha);
		
		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) +  month + day;
		// Actualizo el valor de la fecha
		this.setEnteroFecha(Integer.valueOf(fecha));
		//entero_fecha = Integer.valueOf(fecha);
		
	}
	
	/*
	 * Método que me devuelve el mes según el entero pasado por parámetro
	 */
	private String obtenerMes(int month){
		
		String m = new String();
		
		switch(month){
		
		case 0:
			m = "Ene.";
			break;
		case 1:
			m = "Feb.";
			break;
		case 2:
			m = "Mar.";
			break;
		case 3:
			m = "Abr.";
			break;
		case 4:
			m = "May.";
			break;
		case 5:
			m = "Jun.";
			break;
		case 6:
			m = "Jul.";
			break;
		case 7:
			m = "Ago.";
			break;
		case 8:
			m = "Sep.";
			break;
		case 9:
			m = "Oct.";
			break;
		case 10:
			m = "Nov.";
			break;
		case 11:
			m = "Dic.";
			break;
		default:
			m= "error";
			break;
		}
		
		return m;
		
	}
	
	/*
	 * Método que me devuelve el día de la semana según el entero pasado por parámetro
	 */
	private String obtenerDiaSemana(int day){
		
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
	
	public void lanzarMensaje(String e){
		Toast t = Toast.makeText(this, e, Toast.LENGTH_LONG);
		t.show();
		
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Evento de levantar la pulsación de una tecla del móvil
		
		// Entra en el IF si la tecla pulsada es la de salir o retorno
		if (keyCode == KeyEvent.KEYCODE_BACK){
			
			/*
			 *  Aquí Instacio el Intent que voy a devolver a la Activity anterior DatasAcitivityScreen
			 *  y le paso los parámetros pertinentes
			 */
			Intent resultData =  getIntent();
			
			String fecha = new String("" + entero_fecha);
			
			/*
			 * Formato de la fecha: AAAAMMDD
			 * Para coger el mes le digo cadena.substring(inicio,ultimoCaracterNoIncluido) : mi ejemplo es fecha.substring(4,6)
			 * Para coger el año le digo cadena.substring(inicio,ultimoCaracterNoIncluido) : mi ejemplo es fecha.substring(0,4)
			 */
			Integer anyo = Integer.valueOf(fecha.substring(0,4));
			Integer mes = Integer.valueOf(fecha.substring(4,6));
			
			resultData.putExtra("mes", mes.intValue()-1);
			resultData.putExtra("anyo", anyo.intValue());
			setResult(Activity.RESULT_OK, resultData);
			finish();
			
		}
		
		return true;
	}
	
}
