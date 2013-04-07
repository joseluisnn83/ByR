package com.joseluisnn.byr;

import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class InformesScreenActivity extends TabActivity{

	/*
	 * variables del tipo de dato SharedPreferences para ver
	 * que informe por defecto debe ser visualizado
	 */
	private SharedPreferences preferenceConfiguracionPrivate;	
	private SingletonConfigurationSharedPreferences singleton_csp;
	private int informeSeleccionado;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_informes_screen);
		
		// Obtengo los valores que se encuentran en el archivo de configuración
		singleton_csp = new SingletonConfigurationSharedPreferences();
		
		// Instanciamos el TabHost de la Actividad
		TabHost tabHost = getTabHost();
		// Creamos un recurso para las propiedades de la pestaña
		TabHost.TabSpec spec;
		
		Intent intent;
		Resources res = getResources();
		
		intent = new Intent().setClass(this, PestanyaSemanalActivity.class);
		spec = tabHost.newTabSpec("Semanal").setIndicator(
				"Semanal",
				res.getDrawable(android.R.drawable.ic_menu_week)).setContent(intent);
		tabHost.addTab(spec);
		
		
		intent = new Intent().setClass(this, PestanyaMensualActivity.class);
		spec = tabHost.newTabSpec("Mensual").setIndicator(
				"Mensual",
				res.getDrawable(android.R.drawable.ic_menu_month)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, PestanyaTrimestralActivity.class);
		spec = tabHost.newTabSpec("Trimestral").setIndicator(
				"Trimestral",
				res.getDrawable(android.R.drawable.ic_menu_my_calendar)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, PestanyaAnualActivity.class);
		spec = tabHost.newTabSpec("Anual").setIndicator(
				"Anual",
				res.getDrawable(android.R.drawable.ic_menu_agenda)).setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, PestanyaLibreActivity.class);
		spec = tabHost.newTabSpec("Libre").setIndicator(
				"Libre",
				res.getDrawable(android.R.drawable.ic_menu_today)).setContent(intent);
		tabHost.addTab(spec);
		
		// Obtengo el valor del informe a seleccionar por defecto guardado previamente
	    preferenceConfiguracionPrivate = getSharedPreferences(singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION, Context.MODE_PRIVATE);
	    
	    informeSeleccionado = preferenceConfiguracionPrivate.getInt(singleton_csp.KEY_INFORME_POR_DEFECTO, 0);
		
		tabHost.setCurrentTab(informeSeleccionado);
		
	}
	
	/*
	public static void setTabColor(TabHost tabhost, Resources res) {
	    for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
	    {
	        tabhost.getTabWidget().getChildAt(i).setBackgroundColor(res.getColor(R.color.ListadosBrown)); //unselected
	        //tabhost.getTabWidget().getChildAt(i).setBackground(res.getDrawable(R.drawable.listados_pestanya_background));
	    }
	    tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(res.getColor(R.color.ListadosLightGold)); // selected
	}
	*/
	
	

}
