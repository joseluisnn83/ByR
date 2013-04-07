package com.joseluisnn.byr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joseluisnn.singleton.SingletonBroadcastReceiver;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

public class ConfigurationActivity extends Activity{

	/*
	 * Constantes para indicar el tipo de Dialog a crear
	 */
	private static final int DIALOGO_TIPO_CONFIGURACION = 0;
	private static final int DIALOGO_INFORMES = 1;
	
	/*
	 * variables del tipo de dato SharedPreferences
	 */
	private SharedPreferences preferenceConfiguracionPrivate;	
	private SingletonConfigurationSharedPreferences singleton_csp;
	private Editor editorPreference;
	/*
	 * Variables del diseño gráfico de la Activity
	 */
	private LinearLayout llTipoConfiguracion;
	private LinearLayout llConceptosIngresos;
	private LinearLayout llConceptosGastos;
	private LinearLayout llInformes;
	private TextView tvMTTipoConfiguracion;
	private TextView tvSTTipoConfiguracion;
	private TextView tvGestionConceptos;
	private TextView tvMTConceptosIngresos;
	private TextView tvSTConceptosIngresos;	
	private TextView tvMTConceptosGastos;
	private TextView tvSTConceptosGastos;
	private TextView tvOtrasOpciones;
	private TextView tvMTOtrasOpciones;
	private TextView tvSTOtrasOpciones;
	
	// Variable que contiene la constante para saber qué Broadcast se ha enviado
	private SingletonBroadcastReceiver sbr;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		
		// Obtengo la clase que contiene las constantes para enviar el Broadcast 
		sbr = new SingletonBroadcastReceiver();
		// Obtengo los valores que se encuentran en el archivo de configuración
		singleton_csp = new SingletonConfigurationSharedPreferences();
		
		// Instancio los objetos View del diseño
		llTipoConfiguracion = (LinearLayout)findViewById(R.id.llTipoConfiguracion);
		llConceptosIngresos = (LinearLayout)findViewById(R.id.llConceptosIngresos);
		llConceptosGastos = (LinearLayout)findViewById(R.id.llConceptosGastos);
		llInformes = (LinearLayout)findViewById(R.id.llInformes);
		tvMTTipoConfiguracion = (TextView)findViewById(R.id.textViewMTTipoConfiguracion);
		tvSTTipoConfiguracion = (TextView)findViewById(R.id.textViewSTTipoConfiguracion);
		tvGestionConceptos = (TextView)findViewById(R.id.textViewGestionConceptos);
		tvMTConceptosIngresos = (TextView)findViewById(R.id.textViewMTConceptosIngresos);		
		tvSTConceptosIngresos = (TextView)findViewById(R.id.textViewSTConceptosIngresos);	
		tvMTConceptosGastos = (TextView)findViewById(R.id.textViewMTConceptosGastos);
		tvSTConceptosGastos = (TextView)findViewById(R.id.textViewSTConceptosGastos);
		tvOtrasOpciones = (TextView)findViewById(R.id.textViewOtrasOpciones);
		tvMTOtrasOpciones = (TextView)findViewById(R.id.textViewMTOtrasOpciones);
		tvSTOtrasOpciones = (TextView)findViewById(R.id.textViewSTOtrasOpciones);
		
		// Cambio los tipos de fuentes
		Typeface fuente = Typeface.createFromAsset(getAssets(), "fonts/go_boom.ttf");
		tvMTTipoConfiguracion.setTypeface(fuente);
		tvSTTipoConfiguracion.setTypeface(fuente);
		tvGestionConceptos.setTypeface(fuente);
		tvMTConceptosIngresos.setTypeface(fuente);
		tvSTConceptosIngresos.setTypeface(fuente);
		tvMTConceptosGastos.setTypeface(fuente);
		tvSTConceptosGastos.setTypeface(fuente);
		tvOtrasOpciones.setTypeface(fuente);
		tvMTOtrasOpciones.setTypeface(fuente);
		tvSTOtrasOpciones.setTypeface(fuente);
		
		// Evento OnClick del Layout del tipo de configuración
		llTipoConfiguracion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Bundle b = new Bundle();
				//b.putInt("tipoConfig", tipoConfig);
				
				Dialog d = onCreateDialog(DIALOGO_TIPO_CONFIGURACION,b);
				
				d.show();
				
			}
		});
		
		// Evento OnClick del Layout de conceptos de ingresos
		llConceptosIngresos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// lanzo la Activity de conceptos de Ingresos
				lanzarPantallaConceptosIngresos();
			}
		});
		
		// Evento OnClick del Layout de conceptos de gastos
		llConceptosGastos.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// lanzo la Activity de conceptos de Gastos
				lanzarPantallaConceptosGastos();
				
			}
		});
		
		// Evento OnClick del Layout del Nombre de Usuario
		llInformes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Bundle b = new Bundle();
				Dialog d = onCreateDialog(DIALOGO_INFORMES, b);
				
				d.show();
			}
		});
		
	}

	/*
	 * Método que me crea los distintos tipos de Dialog según la opción de configuración elegida por el usuario
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		
		Dialog dialogo;
		
		switch(id)
	    {
	        case DIALOGO_TIPO_CONFIGURACION:
	            dialogo = crearDialogoSeleccionTipoConfiguracion();
	            break;
	        case DIALOGO_INFORMES:
	            dialogo = crearDialogoSeleccionInformes();
	            break;
	        default:
	        	dialogo = new Dialog(this);
	            break;
	    }
	 
	    return dialogo;		
	}
	
	/*
	 * Dialog para elegir el tipo de configuración que quiere el usuario
	 */
	private Dialog crearDialogoSeleccionTipoConfiguracion()
	{
	    final String[] items = {"Básica", "Avanzada"};
	    // Obtengo el valor del tipo de configuración guardado previamente
	    preferenceConfiguracionPrivate = getSharedPreferences(singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION, Context.MODE_PRIVATE);
	    
	    final int tipoConfiguracion = preferenceConfiguracionPrivate.getInt(singleton_csp.KEY_TIPO_CONFIGURACION, 0);
		
	    // Variable para modificar los valores del archivo de configuración
		editorPreference = preferenceConfiguracionPrivate.edit();
				
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	    builder.setTitle("Tipo de Configuración");
	    builder.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_preferences));
	    builder.setSingleChoiceItems(items, tipoConfiguracion, new DialogInterface.OnClickListener() {
	        
	    	public void onClick(DialogInterface dialog, int item) {
	        	if(tipoConfiguracion!=item){// Si se ha pulsado una opción diferente a la anterior guardada se realiza el cambio en el fichero
	        		editorPreference.putInt(singleton_csp.KEY_TIPO_CONFIGURACION, item);
	        		editorPreference.commit();
	        		
	        		// Envío la señal de Broadcast para que PrincipalScreenActivity lo reciba
	        		Intent intent = new Intent(sbr.CAMBIO_CONFIGURACION);
	        		intent.putExtra("cambio", 0);
	        		intent.putExtra("tipo_configuracion", item);
	        		sendBroadcast(intent);
	        		
	        	}
	        	dialog.cancel();
	        }
	    	
	    });
	 
	    return builder.create();
	}
	
	/*
	 * Dialog para elegir el tipo de infrome que quiere el usuario seleccionar por defecto
	 */
	private Dialog crearDialogoSeleccionInformes()
	{
	    final String[] items = {"Semanal", "Mensual","Trimestral","Anual","Libre"};
	    // Obtengo el valor del informe a seleccionar por defecto guardado previamente
	    preferenceConfiguracionPrivate = getSharedPreferences(singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION, Context.MODE_PRIVATE);
	    
	    final int informeSeleccionado = preferenceConfiguracionPrivate.getInt(singleton_csp.KEY_INFORME_POR_DEFECTO, 0);
		
	    // Variable para modificar los valores del archivo de configuración
		editorPreference = preferenceConfiguracionPrivate.edit();
				
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	    builder.setTitle("Informe a mostrar");
	    builder.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_agenda));
	    builder.setSingleChoiceItems(items, informeSeleccionado, new DialogInterface.OnClickListener() {
	        
	    	public void onClick(DialogInterface dialog, int item) {
	        	if(informeSeleccionado!=item){
	        		// Si se ha pulsado una opción diferente a la anterior guardada se realiza el cambio en el fichero
	        		editorPreference.putInt(singleton_csp.KEY_INFORME_POR_DEFECTO, item);
	        		editorPreference.commit();
	        		
	        	}
	        	dialog.cancel();
	        }
	    });
	 
	    return builder.create();
	}
	
	/*
	 * Método que lanza la Activity ConceptosIngresosActivity para insertar, modificar o borrar conceptos de ingresos
	 */
	private void lanzarPantallaConceptosIngresos(){
		Intent intent = new Intent(this,ConceptosIngresosActivity.class);
		startActivity(intent);
	}
	
	/*
	 * Método que lanza la Activity ConceptosGastosActivity para insertar, modificar o borrar conceptos de gastos
	 */
	private void lanzarPantallaConceptosGastos(){
		Intent intent = new Intent(this,ConceptosGastosActivity.class);
		startActivity(intent);
	}

}
