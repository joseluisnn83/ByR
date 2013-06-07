package com.joseluisnn.tt;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.joseluisnn.singleton.SingletonBroadcastReceiver;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

public class PrincipalScreenActivity extends Activity {

	// Variable para recibir una señal de BroadcastReceiver al modificar el tipo
	// de Configuracion
	private BroadcastReceiver myReceiver;
	// Variable que contiene la constante para saber qué Broadcast se ha enviado
	private SingletonBroadcastReceiver sbr;

	// Variables para acceder al archivo de configuración general y de las
	// gráficas
	private SharedPreferences preferenceConfiguracionPrivate;
	private SingletonConfigurationSharedPreferences singleton_csp;
	private int tipoConfig;

	// Variables de la interfaz gráfica
	private ImageView b_data;
	private ImageView b_config;
	private ImageView b_inform;
	private ImageView b_graphic;
	// private ImageView b_prevision;
	// Variables para los textView de los botones
	private TextView tvData;
	private TextView tvConfig;
	private TextView tvInform;
	private TextView tvGraphic;
	// private TextView tvPrevision;
	private TextView tvDeveloper;

	private long tiempoDePulsacionInicial;

	private Animation animacionBotonPulsado, animacionBotonLevantado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*
		 * Compruebo si el dispositivo es una TABLET o un MOBILE normal
		 */
		TelephonyManager manager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
        	//is a "Tablet";
        	;
        }else{
            //is a "Mobile";
        	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
		
		setContentView(R.layout.activity_principal_screen);

		animacionBotonPulsado = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_pulsado);
		animacionBotonLevantado = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_levantado);

		// Inicializo las variables de la interfaz
		b_data = (ImageView) findViewById(R.id.imageButtonData);
		b_config = (ImageView) findViewById(R.id.imageButtonConfiguration);
		b_inform = (ImageView) findViewById(R.id.imageButtonInforms);
		b_graphic = (ImageView) findViewById(R.id.imageButtonGraphics);
		// b_prevision = (ImageView)findViewById(R.id.imageButtonPrevision);
		tvData = (TextView) findViewById(R.id.textViewData);
		tvConfig = (TextView) findViewById(R.id.textViewConfiguration);
		tvInform = (TextView) findViewById(R.id.textViewInforms);
		tvGraphic = (TextView) findViewById(R.id.textViewGraphics);
		// tvPrevision = (TextView)findViewById(R.id.textViewPrevision);
		tvDeveloper = (TextView) findViewById(R.id.textViewDeveloper);

		// Modifico el tipo de fuente de los TextView
		// Variable para el tipo tipo de fuente
		Typeface fuente = Typeface.createFromAsset(getAssets(),
				"fonts/FreedanFont.ttf");
		tvData.setTypeface(fuente);
		tvConfig.setTypeface(fuente);
		tvInform.setTypeface(fuente);
		tvGraphic.setTypeface(fuente);
		// tvPrevision.setTypeface(fuente);
		tvDeveloper.setTypeface(fuente);

		// Inicio la clase donde están las constantes de BroadcastReceiver
		sbr = new SingletonBroadcastReceiver();

		/* Modifico las características al ser pulsado */
		b_data.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					b_data.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						// Lanzo la Actividad DatasActivityScreen
						b_data.startAnimation(animacionBotonLevantado);
						lanzarGestionDatos();
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					b_data.startAnimation(animacionBotonLevantado);
					break;
				case MotionEvent.ACTION_CANCEL:
					b_data.startAnimation(animacionBotonLevantado);
					break;

				}

				return true;
			}
		});

		/* Modifico las características al ser pulsado */
		b_config.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					b_config.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						// Lanzo la Actividad ConfigurationActivity
						b_config.startAnimation(animacionBotonLevantado);
						lanzarConfiguracion();
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					b_config.startAnimation(animacionBotonLevantado);
					break;

				}

				return true;
			}
		});

		/* Modifico las características al ser pulsado */
		b_inform.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					b_inform.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						// Lanzo la Actividad InformesScreenActivity
						b_inform.startAnimation(animacionBotonLevantado);
							
						    // do something for phones running an SDK before HONEYCOMB third verison
							lanzarInformes();
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					b_inform.startAnimation(animacionBotonLevantado);
					break;

				}

				return true;
			}
		});

		/* Modifico las características al ser pulsado */
		b_graphic.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					b_graphic.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						
						if (getTipoConfiguracion() == 0){
							lanzarAdvertencia("Para habilitar las Gráficas debe dirigirse a Configuración " +
									"y cambiar el tipo de configuración a Avanzada.");
						}else{
							// Lanzo la Actividad CreateAccountActivity
							b_graphic.startAnimation(animacionBotonLevantado);
							lanzarGraficas();
						}
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					b_graphic.startAnimation(animacionBotonLevantado);
					break;
				}

				return true;
			}
		});
		
		/*
		 * Según el tipo de configuración habilito los botones adecuados
		 */
		// Obtengo la clase que contiene las constantes para enviar el Broadcast
		sbr = new SingletonBroadcastReceiver();

		// Obtengo los valores que se encuentran en el archivo de configuración
		singleton_csp = new SingletonConfigurationSharedPreferences();
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		// compruebo si ya hay una cuenta creada
		if (preferenceConfiguracionPrivate.contains(singleton_csp.KEY_CUENTA)) {

			setTipoConfiguracion(preferenceConfiguracionPrivate.getInt(
					singleton_csp.KEY_TIPO_CONFIGURACION, 0));

		} else {

			/*
			 * -Con el control Editor iniciamos la manipulación de valores; con
			 * Editor podemos establecer tipos string,boolean,float,int y long
			 * como pares clave-valor -Almacenar los datos en Editor crea un
			 * elemento Map en memoria. prefEditorConfiguracion : para la
			 * configuracion general de la app prefEditorConfiguracionGraficas :
			 * para la configuracion de las graficas de la app
			 */
			Editor prefEditorConfiguracion = preferenceConfiguracionPrivate
					.edit();
			prefEditorConfiguracion.putBoolean(singleton_csp.KEY_CUENTA, true);
			/*
			 * El tipo de configuración del usuario será básica hasta que él
			 * decida modificarla dentro de la aplicación
			 */
			prefEditorConfiguracion.putInt(
					singleton_csp.KEY_TIPO_CONFIGURACION, 0);
			setTipoConfiguracion(0);
			/*
			 * El informe seleccionado por defecto será el semanal hasta que el
			 * usuario lo modifique en la configuración 0: semanal 1: mensual 2:
			 * trimestral 3: anual 4: libre
			 */
			prefEditorConfiguracion.putInt(
					singleton_csp.KEY_INFORME_POR_DEFECTO, 0);
			// Guardo los valores
			prefEditorConfiguracion.commit();

			// Archivo de configuración de las gráficas
			SharedPreferences preferenceConfiguracionPrivateGraficas = getSharedPreferences(
					singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION_GRAFICAS,
					Context.MODE_PRIVATE);			
			Editor prefEditorConfiguracionGraficas = preferenceConfiguracionPrivateGraficas
					.edit();
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_PRIMER_ACCESO_CONFIG_GRAFICA, true);
			prefEditorConfiguracionGraficas.putInt(
					singleton_csp.KEY_LPTIPOGRAFICA, 0);
			prefEditorConfiguracionGraficas.putInt(
					singleton_csp.KEY_LPVALORESGRAFICA, 0);
			Calendar c = Calendar.getInstance();
			prefEditorConfiguracionGraficas.putInt(singleton_csp.KEY_PYEARS,
					c.get(Calendar.YEAR));
			prefEditorConfiguracionGraficas.putInt(singleton_csp.KEY_PMONTHS,
					c.get(Calendar.MONTH) + 1);
			prefEditorConfiguracionGraficas.putInt(singleton_csp.KEY_PDAY,
					c.get(Calendar.DAY_OF_MONTH));
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_CBPLINEINGRESOS, true);
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_CBPLINEGASTOS, true);
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_CBPLINEBALANCE, false);
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_CBPVALUESINGRESOS, false);
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_CBPVALUESGASTOS, false);
			prefEditorConfiguracionGraficas.putBoolean(
					singleton_csp.KEY_CBPVALUESBALANCE, true);
			prefEditorConfiguracionGraficas.commit();

		}

		/*
		 * @tipoconfig = 0 : configuración básica y deshabilito las gráficas y
		 * la previsión
		 * 
		 * @tipoconfig = 1 : configuración avanzada y habilito las gráficas y la
		 * previsión
		 */
		if (getTipoConfiguracion() == 0) {
			// cambio el icono a deshabilitado
			b_graphic.setImageDrawable(getResources().getDrawable(R.drawable.chart_disabled));
		} else {
			// cambio el icono a habilitado
			b_graphic.setImageDrawable(getResources().getDrawable(R.drawable.chart));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_principal_screen, menu);

		return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Lanzo la Actividad que corresponda al ser pulsado el
		// item del menu
		Intent i;
		
		switch (item.getItemId()) {

		case R.id.agradecimientos:
			i = new Intent(this, AgradecimientosActivity.class);
			startActivity(i);
			break;
		case R.id.acerca_de:
			i = new Intent(this, AcercaDeActivity.class);
			startActivity(i);
			break;
		
		}

		return true;
	}

	/*
	 * Método que lanza la pantalla de GESTIÓN DE DATOS
	 */
	private void lanzarGestionDatos() {

		Intent intent = new Intent(this, DatasActivityScreen.class);
		startActivity(intent);

	}

	/*
	 * Método que lanza la pantalla de CONFIGURACIÓN
	 */
	private void lanzarConfiguracion() {

		Intent intent = new Intent(this, ConfigurationActivity.class);

		startActivity(intent);
	}

	/*
	 * Método que lanza la pantalla de INFORMES
	 */
	private void lanzarInformes() {

		Intent intent = new Intent(this, InformesScreenActivity.class);

		startActivity(intent);
	}

	/*
	 * Método que lanza la pantalla de GRAFICAS
	 */
	private void lanzarGraficas() {

		Intent intent = new Intent(this, GraphicsActivity.class);

		startActivity(intent);
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

		builder.setTitle("INFORMACIÓN");
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
	protected void onStop() {
		// TODO Registro en onStop() el Broadcast que me indicará cambios en la
		// configuración
		super.onStop();

		IntentFilter ifilter = new IntentFilter(sbr.CAMBIO_CONFIGURACION);

		myReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// Compruebo si se ha modificado el nombre de usuario o el tipo
				// de configuracion

				int cambio = intent.getIntExtra("cambio", 0);

				if (cambio == 0) {

					int tipo_config = intent.getIntExtra("tipo_configuracion",
							0);

					// Según el tipo de configuración que se le pase por
					// parámetro se ocultan las opciones
					if (tipo_config == 0) {// el usuario ha modificado el tipo
											// de configuración						
						b_graphic.setImageDrawable(getResources().getDrawable(R.drawable.chart_disabled));						
					} else {
						b_graphic.setImageDrawable(getResources().getDrawable(R.drawable.chart));						
					}
					
					setTipoConfiguracion(tipo_config);
				}
			}
		};

		// Registro mi BroadcastReceiver
		this.registerReceiver(myReceiver, ifilter);
	}
	
	/*
	 * Método que actualiza el tipo de configuración
	 */
	private void setTipoConfiguracion(int tc){
		this.tipoConfig = tc;
	}
	
	/*
	 * Método que me devuelve el tipo de configuración
	 */
	private int getTipoConfiguracion(){
		return this.tipoConfig;
	}

	@Override
	protected void onRestart() {

		/*
		 * Quito el registro de mi BroadcastReceiver cuando mi Actividad pasa de
		 * estar oculta (o no Activa) por otra Actividad o aplicación a visible
		 * (o Activa)
		 */
		this.unregisterReceiver(myReceiver);

		super.onRestart();

	}

	@Override
	protected void onDestroy() {

		/*
		 * Quito el registro de mi BoradcastReceiver cuando mi Actividad pasa de
		 * estado Parada a Destruida
		 */
		this.unregisterReceiver(myReceiver);
		
		/*
		 * Cierro sesión del Bugsense
		 */
		//BugSenseHandler.closeSession(PrincipalScreenActivity.this);

		super.onDestroy();

	}

}
