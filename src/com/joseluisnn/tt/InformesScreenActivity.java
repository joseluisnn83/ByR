package com.joseluisnn.tt;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class InformesScreenActivity extends TabActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (!(currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2)) {
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

		// Si la API es mayor o igual que la 13 entra en el primer if
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {

			// Instanciamos el TabHost de la Actividad
			tabHost = getTabHost();
			// Creamos un recurso para las propiedades de la pestaña
			TabHost.TabSpec spec;

			Intent intent;
			res = getResources();

			intent = new Intent().setClass(this, PestanyaSemanalActivity.class);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeSemanal_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_week))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent().setClass(this, PestanyaMensualActivity.class);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeMensual_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_month))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent().setClass(this,
					PestanyaTrimestralActivity.class);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeTrimestral_title))
					.setIndicator(
							"",
							res.getDrawable(android.R.drawable.ic_menu_my_calendar))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent().setClass(this, PestanyaAnualActivity.class);
			spec = tabHost
					.newTabSpec(
							getResources().getString(
									R.string.PestanyaInformeAnual_title))
					.setIndicator("",
							res.getDrawable(android.R.drawable.ic_menu_agenda))
					.setContent(intent);
			tabHost.addTab(spec);

			intent = new Intent().setClass(this, PestanyaLibreActivity.class);
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

			intent = new Intent().setClass(this, PestanyaSemanalActivity.class);
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

			intent = new Intent().setClass(this, PestanyaMensualActivity.class);
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

			intent = new Intent().setClass(this,
					PestanyaTrimestralActivity.class);
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

			intent = new Intent().setClass(this, PestanyaAnualActivity.class);
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

			intent = new Intent().setClass(this, PestanyaLibreActivity.class);
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

}
