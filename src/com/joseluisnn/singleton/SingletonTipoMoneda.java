package com.joseluisnn.singleton;

import com.joseluisnn.tt.R;

import android.content.Context;
import android.content.SharedPreferences;

public class SingletonTipoMoneda {

	private static SingletonTipoMoneda INSTANCE = new SingletonTipoMoneda();

	/*
	 * variables del tipo de dato SharedPreferences para ver que moneda debe ser
	 * visualizada
	 */
	private SharedPreferences preferenceConfiguracionPrivate;
	private SingletonConfigurationSharedPreferences singleton_csp;
	private String valorMoneda;

	private SingletonTipoMoneda() {
	}

	public String obtenerTipoMoneda(Context c) {

		// Obtengo los valores que se encuentran en el archivo de configuración
		singleton_csp = new SingletonConfigurationSharedPreferences();

		// Obtengo el valor del informe a seleccionar por defecto guardado
		// previamente
		preferenceConfiguracionPrivate = c.getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0) == 0) {

			valorMoneda = c.getResources().getString(R.string.valor_euro);
		}else if(preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0) == 1) {
			valorMoneda = c.getResources().getString(R.string.valor_dolar);
		}else{
			valorMoneda = c.getResources().getString(R.string.valor_libra);
		}
		
		return valorMoneda;

	}

	/*
	 * creador sincronizado para protegerse de posibles problemas multi-hilo
	 * otra prueba para evitar instanciación múltiple
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SingletonTipoMoneda();
		}
	}

	public static SingletonTipoMoneda getInstance() {
		createInstance();
		return INSTANCE;
	}

}
