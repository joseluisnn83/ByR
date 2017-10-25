package com.joseluisnn.tt;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.FiltroConcepto;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class InformesScreenActivity extends TabActivity {

	// Declaro los dos tipos de conceptos posibles
	private static String TIPO_CONCEPTO_INGRESO = "ingreso";
	private static String TIPO_CONCEPTO_GASTO = "gasto";
	// Declaro los dos tipos de filtros posibles
	private static String TIPO_FILTRO_INGRESO = "filtroIngreso";
	private static String TIPO_FILTRO_GASTO = "filtroGasto";
	/*
	 * Variable para saber que se devuelven valores de la actividad a la que
	 * llamo a través del método startActivityForResult :
	 * FiltroConceptosActivity
	 */
	private static final int RESULT_FILTER = 1;
	/*
	 * variables del tipo de dato SharedPreferences para ver que informe por
	 * defecto debe ser visualizado
	 */
	private SharedPreferences preferenceConfiguracionPrivate;
	private SingletonConfigurationSharedPreferences singleton_csp;
	private int informeSeleccionado;
	private TabHost tabHost;
	private Resources res;
	private int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	/*
	 * Variables para crear el filtro de los informes
	 */
	private ArrayList<FiltroConcepto> filtroIngresos = new ArrayList<FiltroConcepto>();
	private ArrayList<FiltroConcepto> filtroGastos = new ArrayList<FiltroConcepto>();
	/*
	 * Variable para la BASE DE DATOS
	 */
	private DBAdapter dba;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/*
		 * if (!(currentapiVersion >=
		 * android.os.Build.VERSION_CODES.HONEYCOMB_MR2)) {
		 * this.requestWindowFeature(Window.FEATURE_NO_TITLE); }
		 */

		/**
		 * Compruebo si el dispositivo es una TABLET o un MOBILE normal
		 */
		TelephonyManager manager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
			// return "Tablet";
			;
		} else {
			// return "Mobile";
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		setContentView(R.layout.activity_informes_screen);

		// Obtengo los valores que se encuentran en el archivo de configuración
		singleton_csp = new SingletonConfigurationSharedPreferences();

		// Obtengo el valor del informe a seleccionar por defecto guardado
		// previamente
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		informeSeleccionado = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_INFORME_POR_DEFECTO, 0);

		/*
		 * Inicializo los filtros de ingresos y gastos
		 */
		// Busco los conceptos en la BD y los inserto en el Hash
		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(this);
		// Abro la Base de Datos solo en modo lectura
		dba.openREAD();

		filtroIngresos = dba.listarFiltroConceptos(TIPO_CONCEPTO_INGRESO);
		filtroGastos = dba.listarFiltroConceptos(TIPO_CONCEPTO_GASTO);
		// Cierro la BD
		dba.close();

		// Si la API es mayor o igual que la 13 entra en el primer if
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {

			// Instanciamos el TabHost de la Actividad
			tabHost = getTabHost();
			// Creamos un recurso para las propiedades de la pestaña
			TabHost.TabSpec spec;

			Intent intent;
			res = getResources();

			// Le paso al Intent como parámetros los filtros
			intent = new Intent()
					.setClass(this, PestanyaSemanalActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeSemanal_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_week))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaMensualActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeMensual_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_month))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaTrimestralActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeTrimestral_title))
					.setIndicator(
							"",
							res.getDrawable(android.R.drawable.ic_menu_my_calendar))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaAnualActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeAnual_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_agenda))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaLibreActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeLibre_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_today))
					.setContent(intent);
			tabHost.addTab(spec);

			// Do something for froyo and above versions
			// lanzarAdvertencia("Versión de API alta.");
			setTabColor(tabHost, res);

		} else {

			// Instanciamos el TabHost de la Actividad
			tabHost = getTabHost();
			// Creamos un recurso para las propiedades de la pestaña
			TabHost.TabSpec spec;

			Intent intent;
			res = getResources();

			// Le paso al Intent como parámetros los filtros
			intent = new Intent()
					.setClass(this, PestanyaSemanalActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeSemanal_title))
					.setIndicator(
							getResources().getString(
									R.string.PestanyaInformeSemanal_title),
							res.getDrawable(android.R.drawable.ic_menu_week))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaMensualActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeMensual_title))
					.setIndicator(
							getResources().getString(
									R.string.PestanyaInformeMensual_title),
							res.getDrawable(android.R.drawable.ic_menu_month))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaTrimestralActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeTrimestral_title))
					.setIndicator(
							getResources().getString(
									R.string.PestanyaInformeTrimestral_title),
							res.getDrawable(android.R.drawable.ic_menu_my_calendar))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaAnualActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeAnual_title))
					.setIndicator(
							getResources().getString(
									R.string.PestanyaInformeAnual_title),
							res.getDrawable(android.R.drawable.ic_menu_agenda))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent()
					.setClass(this, PestanyaLibreActivity.class)
					.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO,
							filtroIngresos)
					.putParcelableArrayListExtra(TIPO_FILTRO_GASTO,
							filtroGastos);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeLibre_title))
					.setIndicator(
							getResources().getString(
									R.string.PestanyaInformeLibre_title),
							res.getDrawable(android.R.drawable.ic_menu_today))
					.setContent(intent);
			tabHost.addTab(spec);

		}

		tabHost.setCurrentTab(informeSeleccionado);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				// Si la API es mayor o igual que la 13 entra en el primer if
				if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
					// Do something for froyo and above versions
					// lanzarAdvertencia("Versión de API alta.");
					setTabColor(tabHost, res);
					actualizarBarraTitulo(tabHost.getCurrentTab());
					
				}
			}
		});

		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			setTabColor(tabHost, res);
			actualizarBarraTitulo(informeSeleccionado);
		}

	}

	public static void setTabColor(TabHost tabhost, Resources reso) {

		TextView tv;

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {

			tabhost.getTabWidget().getChildAt(i)
					.setBackgroundColor(reso.getColor(R.color.Azul4)); // unselected
			// Cambio el color del texto del TabWidget
			tv = (TextView) tabhost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			tv.setTextColor(reso.getColor(R.color.colorGeneral));
		}

		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.setBackgroundColor(reso.getColor(R.color.colorGeneral)); // selected
		// Cambio el color del texto del TabWidget
		tv = (TextView) tabhost.getTabWidget()
				.getChildAt(tabhost.getCurrentTab())
				.findViewById(android.R.id.title);
		tv.setTextColor(reso.getColor(R.color.Azul4));

	}

	private void actualizarBarraTitulo(int informe) {

		switch (informe) {
		case 0:
			this.setTitle(getResources().getString(
					R.string.PestanyaInformeSemanal_title));
			break;
		case 1:
			this.setTitle(getResources().getString(
					R.string.PestanyaInformeMensual_title));
			break;
		case 2:
			this.setTitle(getResources().getString(
					R.string.PestanyaInformeTrimestral_title));
			break;
		case 3:
			this.setTitle(getResources().getString(
					R.string.PestanyaInformeAnual_title));
			break;
		case 4:
			this.setTitle(getResources().getString(
					R.string.PestanyaInformeLibre_title));
			break;

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Cargo el menu de los informes
		getMenuInflater().inflate(R.menu.menu_configuracion_informes, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Lanzo la Actividad PreferencesReportsActivity al ser pulsado el
		// item del menu
		switch (item.getItemId()) {

		case R.id.menu_filtro_ingresos:
			Intent i = new Intent(this, FiltroConceptosActivity.class);
			i.putExtra("tipo_concepto", TIPO_CONCEPTO_INGRESO);
			i.putParcelableArrayListExtra(TIPO_FILTRO_INGRESO, filtroIngresos);
			startActivityForResult(i, RESULT_FILTER);
			break;
		case R.id.menu_filtro_gastos:
			Intent g = new Intent(this, FiltroConceptosActivity.class);
			g.putExtra("tipo_concepto", TIPO_CONCEPTO_GASTO);
			g.putParcelableArrayListExtra(TIPO_FILTRO_GASTO, filtroGastos);
			startActivityForResult(g, RESULT_FILTER);
			break;
		}

		return true;
		// return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * TODO Método donde recojo los parámetros de la actividad
		 * FiltroConceptosActivity y actualizo el filtro en función de las
		 * modificaciones
		 */

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_FILTER && resultCode == Activity.RESULT_OK
				&& data != null) {

			// Actualizo el la lista del filtro
			if (data.getExtras().getString("tipo_concepto")
					.equals(TIPO_CONCEPTO_INGRESO)) {
				this.filtroIngresos = data.getExtras().getParcelableArrayList(
						TIPO_FILTRO_INGRESO);
			} else {
				// Le paso al Intent como parámetros los filtros
				this.filtroGastos = data.getExtras().getParcelableArrayList(
						TIPO_FILTRO_GASTO);
			}
		}
	}
}
