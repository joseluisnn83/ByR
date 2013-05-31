package com.joseluisnn.tt;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.joseluisnn.tt.R;
import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.Concepto;

public class ConceptosIngresosActivity extends ListActivity{

	/*
	 * Constantes para indicar el tipo de Dialog a crear
	 */
	private static final int DIALOGO_INSERTAR_CONCEPTO = 0;
	private static final int DIALOGO_ACTUALIZAR_CONCEPTO = 1;
	private static final int DIALOGO_BORRAR_CONCEPTO = 2;
	
	// Declaro los dos tipos de conceptos posibles
	private static String TIPO_CONCEPTO_INGRESO = "ingreso";	
	// Variable para la BASE DE DATOS
	private DBAdapter dba;
	// ArrayList de conceptos que contiene el id,nombre y tipo
	private ArrayList<Concepto> listaConceptosIngresos;
	// ArrayList de conceptos que contiene solo el nombre para poder visualizarlo en el ListView
	private ArrayList<String> listaConceptosIngresosAux;
	// Variable LinearLayout para poder añadir Conceptos de Ingreso
	private LinearLayout llConceptoIngresos;
	// Variable ArrayAdapter que se lo voy a asignar al ListView
	private ArrayAdapter<String> aadapter;
	
	// Variable para obtener el concepto pulsado para actualizarlo
	private int conceptoPulsado;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*
		 * Quitamos barra de titulo de la Actividad
		 * Debe ser ejecutada esta instruccion antes del setContentView para que no cargue las imágenes
		 */
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_conceptos_ingresos);
		
		// Instancio la variable LinearLayout para añadir conceptos ingreso
		llConceptoIngresos = (LinearLayout)findViewById(R.id.LinearLayoutConceptosIGeneral);
		
		// Instancio la Base de Datos
		//dba = new DBAdapter(this);
		dba = DBAdapter.getInstance(this);
		
		// Evento onclick para añadir un concepto de ingreso
		llConceptoIngresos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 0 = Insertar Concepto Ingreso
				Dialog d = onCreateDialog(0);
				
				d.show();
				
			}
		});
		
		// Abro la Base de Datos solo en modo lectura
		dba.openREAD();
		// Cargo en listaConceptos los conceptos de ingresos que tengo en la BD
		listaConceptosIngresos = dba.listarConceptos(TIPO_CONCEPTO_INGRESO);
		// Cargo el arrays de conceptos con String para iniciar el ListView 
		listaConceptosIngresosAux = dba.listarArrayConceptos(TIPO_CONCEPTO_INGRESO);
		// Inicializo la variable ArrayAdapter
		aadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaConceptosIngresosAux);
		
		// Cargo el ListView
		setListAdapter(aadapter);
		
		/*
		 * hay que registrar todos los items del listView para que suministren un ContextMenu al ser pulsado  
		 */
		registerForContextMenu(getListView());

		// Cierro la BD en modo lectura
		dba.close();
		
	}


	@Override
	protected Dialog onCreateDialog(int tipoDialogo) {
		// TODO Me crea el Diálogo según el entero pasado por parámetro
		
		Dialog dialogo;
		
		switch(tipoDialogo)
	    {
	        case DIALOGO_INSERTAR_CONCEPTO:
	            dialogo = crearDialogoInsertarConceptoIngreso();
	            break;
	        case DIALOGO_ACTUALIZAR_CONCEPTO:
	            dialogo = crearDialogoActualizarConceptoIngreso();
	            break;
	        case DIALOGO_BORRAR_CONCEPTO:
	        	dialogo = crearDialogBorrarConceptoIngreso();
	        	break;
	        default:
	        	dialogo = new Dialog(this);
	            break;
	    }
	 
	    return dialogo;
	}
	
	/*
	 * Dialog para insertar un concepto de Ingreso
	 */
	private Dialog crearDialogoInsertarConceptoIngreso(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		final EditText conceptoIngreso = new EditText(this);
	    
		builder.setTitle("Inserta concepto de Ingreso");
		builder.setView(conceptoIngreso);
		builder.setIcon(android.R.drawable.ic_menu_add);
		
		builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
									
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO inserto en la BD el concepto insertado por el usuario
				
				if(!conceptoIngreso.getText().toString().equals("")){

					Concepto c = new Concepto(-1, conceptoIngreso.getText().toString(), TIPO_CONCEPTO_INGRESO);
					// lo inserto en la BD
					int registro = dba.insertarConcepto(c);
					
					//Obtengo el id del concepto
					int id = dba.getIdConcepto(c.getNombre(), c.getTipo());
					c.setId(id);
					
					// Si no se ha lanzado la excepción de constraint en la BD actualizo los Arrays y el ListView
					if (registro==1){
						// Lo inserto la lista de Conceptos 
						listaConceptosIngresos.add(c);
						// Lo inserto en el ArrayList para que se muestre en el ListView
						listaConceptosIngresosAux.add(c.getNombre());
						
						// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
						aadapter.notifyDataSetChanged();
					}
					
				}
			}
		});
		
		builder.setNegativeButton(R.string.botonCancelar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO No hago nada
				;
			}
		});
			
		return builder.create();
		
	}
	
	/*
	 * Dialog para modificar un concepto de Ingreso
	 */
	private Dialog crearDialogoActualizarConceptoIngreso(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		final EditText conceptoIngreso = new EditText(this);
		final Concepto conceptoAnterior = listaConceptosIngresos.get(getConceptoPulsado());
	    
		builder.setTitle("Actualizar concepto de Ingreso");
		builder.setView(conceptoIngreso);
		builder.setIcon(android.R.drawable.ic_menu_edit);
		
		conceptoIngreso.setText(conceptoAnterior.getNombre());
		
		builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
									
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO inserto en la BD el concepto insertado por el usuario
				
				if(!conceptoIngreso.getText().toString().equals("") && !conceptoAnterior.getNombre().equals(conceptoIngreso.getText().toString()) ){
										
					Concepto c = listaConceptosIngresos.get(getConceptoPulsado());
					c.setNombre(conceptoIngreso.getText().toString());
					
					// Actualizo el valor en la BD
					int registro = dba.actualizarConcepto(c);
					
					// Si no se ha lanzado la excepción de constraint en la BD actualizo los Arrays y el ListView
					if (registro==1){					
						// Actualizo en las listas
						listaConceptosIngresos.get(getConceptoPulsado()).setNombre(conceptoIngreso.getText().toString());
						listaConceptosIngresosAux.set(getConceptoPulsado(), conceptoIngreso.getText().toString());
										
						// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
						aadapter.notifyDataSetChanged();
					}					
				}
			}
		});
		
		builder.setNegativeButton(R.string.botonCancelar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO No hago nada
				;
			}
		});
			
		return builder.create();
		
	}
	
	/*
	 * Dialog para BORRAR un concepto de Ingreso
	 */
	private Dialog crearDialogBorrarConceptoIngreso()
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	    builder.setTitle("Confirmación");
	    builder.setIcon(android.R.drawable.ic_menu_delete);
	    builder.setMessage("Borrar el concepto acarreará la " +
	    		"eliminación de sus valores (en €) asociados" +
	    		"en este concepto, ¿Desea continuar?");
	    
	    builder.setPositiveButton(R.string.botonAceptar, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Borro en el ListView y la BD
				//lanzarMensaje("Borrar Ingreso: " + posicioABorrar);
				
				Concepto c = listaConceptosIngresos.get(getConceptoPulsado());
				
				// Si el borrado es correcto entro en el if y elimino los valores del ListView
				if(dba.borrarValoresSegunConcepto(c)==1){
					
					// Lo borro de la lista de Conceptos 
					listaConceptosIngresos.remove(getConceptoPulsado());				
					// Lo borro del ArrayList para que me lo quite del ListView
					listaConceptosIngresosAux.remove(getConceptoPulsado());
					
					// Notifico al ArrayAdapter que mi ArrayList ha sido modificado
					aadapter.notifyDataSetChanged();
					
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
	 * Método en el que obtengo el concepto que ha sido pulsado para actualizarlo
	 */
	private int getConceptoPulsado(){
		return this.conceptoPulsado;
	}
	
	/*
	 * Método que me indica el concepto que ha sido pulsado para actualizarlo
	 */
	private void setConceptoPulsado(int item){
		this.conceptoPulsado = item;
	}


	@Override
	protected void onPause() {
		// TODO Cierro la BD en OnPause() cuando la Actividad no está en el foco
		dba.close();
		//lanzarToast("Me cierra la BD en OnPause()");
		
		super.onPause();
	}


	@Override
	protected void onResume() {
		// TODO Abro la base de datos mientras la Actividad está Activa
		
		dba.openREADWRITE();
		//lanzarToast("Me abre la BD en OnResume()");
				
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Cierro la base de datos cuando se destruye la Actividad si estuviera abierta
		if(dba.isOpen()){
			dba.close();
			//lanzarToast("Me cierra la BD en OnDestroy()");
		}
		
		super.onDestroy();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Item seleccionado del ContextMenu asociado al ListView
		
		Dialog d = null;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
			case R.id.delete:
				
				setConceptoPulsado((int) info.id);
				d = onCreateDialog(DIALOGO_BORRAR_CONCEPTO);
				
				d.show();
				
				return true;
				
			case R.id.edit:
				
				setConceptoPulsado((int) info.id);
				d = onCreateDialog(DIALOGO_ACTUALIZAR_CONCEPTO);
				
				d.show();
				
				return true;
			default:
				return super.onContextItemSelected(item);
		}
		
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_configuracion_conceptos, menu);
	}

}
