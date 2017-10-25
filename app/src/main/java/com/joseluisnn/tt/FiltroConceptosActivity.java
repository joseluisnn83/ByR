package com.joseluisnn.tt;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.joseluisnn.objetos.FiltroConcepto;
import com.joseluisnn.singleton.SingletonBroadcastReceiver;
import com.joseluisnn.tt.MyAdapterFiltroConceptos.ObservadorMyAdapterFiltroConceptos;

public class FiltroConceptosActivity extends Activity {

	// Declaro los dos tipos de conceptos posibles
	private static String TIPO_CONCEPTO_INGRESO = "ingreso";
	private static String TIPO_CONCEPTO_GASTO = "gasto";
	// Declaro los dos tipos de filtros posibles
	private static String TIPO_FILTRO_INGRESO = "filtroIngreso";
	private static String TIPO_FILTRO_GASTO = "filtroGasto";

	// Objetos View
	private ListView listView;
	private Button bAplicar;
	private Button bSalir;
	// Adaptador para la lista de Conceptos
	private MyAdapterFiltroConceptos myAdapterFilter;
	// Lista con los conceptos checkados
	private ArrayList<FiltroConcepto> listaFiltro;
	// Variable para saber si se ha modificado algo
	private boolean modificado;

	// Variable que contiene la constante para saber qué Broadcast se ha enviado
	private SingletonBroadcastReceiver sbr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/**
		 * Compruebo si el dispositivo es una TABLET o un MOBILE normal
		 */
		TelephonyManager manager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
			// return "Tablet";
			;
		} else {
			// return "Mobile"; No muestro el título de la Actividad
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		setContentView(R.layout.activity_filtro_conceptos);

		listView = (ListView) findViewById(R.id.FiltroConceptos_listview);
		bAplicar = (Button) findViewById(R.id.FiltroConceptos_button_aplicar);
		bSalir = (Button) findViewById(R.id.FiltroConceptos_button_salir);

		// Obtengo la clase que contiene las constantes para enviar el Broadcast
		sbr = new SingletonBroadcastReceiver();

		// Según si es un concepto de ingreso o gasto obtengo una u otra lista
		if (getIntent().getExtras().getString("tipo_concepto")
				.equals(TIPO_CONCEPTO_INGRESO)) {
			listaFiltro = getIntent().getExtras().getParcelableArrayList(
					TIPO_FILTRO_INGRESO);
		} else {
			listaFiltro = getIntent().getExtras().getParcelableArrayList(
					TIPO_FILTRO_GASTO);
		}

		// Al entrar no se modifica nada
		this.modificado = false;

		// Creo el Adaptador donde va una lista con los conceptos y sus check
		// para ser visualizados o no
		myAdapterFilter = new MyAdapterFiltroConceptos(this, listaFiltro);
		// Al ListView le añado el adaptador
		listView.setAdapter(myAdapterFilter);

		/*
		 * Instancio el observador del Adaptador para cuando se haga click en
		 * los checkbox de cada concepto, para visualizarlo o no
		 */
		myAdapterFilter.setObservador(new ObservadorMyAdapterFiltroConceptos() {

			@Override
			public void actualizarListaFiltro(int position, boolean b) {
				// TODO Se ha pulsado algún check del filtro y actualizo la
				// lista
				listaFiltro.get(position).setFiltrado(b);
				//bAplicar.setEnabled(true);
			}
		});

		bAplicar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO aplico el filtro

				// Compruebo que algún concepto esté seleccionado para apliar el
				// filtro
				boolean aplicarFiltro = false;
				int i = 0;
				while (i < listaFiltro.size() && !aplicarFiltro) {
					if (listaFiltro.get(i).isFiltrado()) {
						aplicarFiltro = true;
					}
					i++;
				}

				// Entro si se puede aplicar el filtro
				if (aplicarFiltro) {

					Intent intentFilter = new Intent(
							sbr.FILTRO_CONCEPTOS_APLICAR);

					// Actualizo el la lista del filtro
					if (getIntent().getExtras().getString("tipo_concepto")
							.equals(TIPO_CONCEPTO_INGRESO)) {
						intentFilter.putParcelableArrayListExtra(
								TIPO_FILTRO_INGRESO, listaFiltro);
						// Le paso al Intent como parámetros los filtros
						intentFilter.putExtra("tipo_concepto",
								TIPO_CONCEPTO_INGRESO);

					} else {
						// Le paso al Intent como parámetros los filtros
						intentFilter.putParcelableArrayListExtra(
								TIPO_FILTRO_GASTO, listaFiltro);
						// Le paso al Intent como parámetros los filtros
						intentFilter.putExtra("tipo_concepto",
								TIPO_CONCEPTO_GASTO);

					}

					/*
					 * Envío la señal de Broadcast para que lo reciban las
					 * clases Semanal, Mensual, Trimestral, Anual y Libre e
					 * inicialicen de nuevo los informes
					 */
					sendBroadcast(intentFilter);

					//bAplicar.setEnabled(false);
					lanzarMensaje(getResources().getString(R.string.filtro_conceptos_aplicado));
					
					modificado = true;

				}else{
					lanzarMensaje(getResources().getString(R.string.filtro_conceptos_item_seleccionado));
					//bAplicar.setEnabled(false);
				}

			}
		});

		bSalir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO cancelo el filtro
				Intent intentFilter = getIntent();

				// Si se ha modificado el filtro lo actualizo al salir
				if (modificado) {

					// Según si es un concepto de ingreso o gasto obtengo una u
					// otra lista
					if (getIntent().getExtras().getString("tipo_concepto")
							.equals(TIPO_CONCEPTO_INGRESO)) {
						intentFilter.putParcelableArrayListExtra(
								TIPO_FILTRO_INGRESO, listaFiltro);
						intentFilter.putExtra("tipo_concepto",
								TIPO_CONCEPTO_INGRESO);
					} else {
						intentFilter.putParcelableArrayListExtra(
								TIPO_FILTRO_GASTO, listaFiltro);
						intentFilter.putExtra("tipo_concepto",
								TIPO_CONCEPTO_GASTO);
					}

					setResult(Activity.RESULT_OK, intentFilter);

				} else {
					// Si no se ha modificado el filtro, no hago nada
					setResult(Activity.RESULT_CANCELED, intentFilter);
				}

				// Finalizo la Activity
				finish();
			}
		});

		//bAplicar.setEnabled(false);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Evento de levantar la pulsación de una tecla del móvil

		/*Intent intentFilter = getIntent();

		// Entra en el IF si la tecla pulsada es la de salir o retorno
		if (keyCode == KeyEvent.KEYCODE_BACK && this.modificado) {

			// Según si es un concepto de ingreso o gasto obtengo una u otra
			// lista
			if (getIntent().getExtras().getString("tipo_concepto")
					.equals(TIPO_CONCEPTO_INGRESO)) {
				intentFilter.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
						listaFiltro);
				intentFilter.putExtra("tipo_concepto", TIPO_CONCEPTO_INGRESO);
			} else {
				intentFilter.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
						listaFiltro);
				intentFilter.putExtra("tipo_concepto", TIPO_CONCEPTO_GASTO);
			}

			setResult(Activity.RESULT_OK, intentFilter);

			finish();

		} else {

			setResult(Activity.RESULT_CANCELED, intentFilter);

			finish();

		}*/
		Intent intentFilter = getIntent();

		// Si se ha modificado el filtro lo actualizo al salir
		if (keyCode == KeyEvent.KEYCODE_BACK && modificado) {

			// Según si es un concepto de ingreso o gasto obtengo una u
			// otra lista
			if (getIntent().getExtras().getString("tipo_concepto")
					.equals(TIPO_CONCEPTO_INGRESO)) {
				intentFilter.putParcelableArrayListExtra(
						TIPO_FILTRO_INGRESO, listaFiltro);
				intentFilter.putExtra("tipo_concepto",
						TIPO_CONCEPTO_INGRESO);
			} else {
				intentFilter.putParcelableArrayListExtra(
						TIPO_FILTRO_GASTO, listaFiltro);
				intentFilter.putExtra("tipo_concepto",
						TIPO_CONCEPTO_GASTO);
			}

			setResult(Activity.RESULT_OK, intentFilter);

		} else {
			// Si no se ha modificado el filtro, no hago nada
			setResult(Activity.RESULT_CANCELED, intentFilter);
		}

		// Finalizo la Activity
		finish();

		return true;
	}
	
	private void lanzarMensaje(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
