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

		/*
		if (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0) == 0) {
			valorMoneda = c.getResources().getString(R.string.valor_euro);
		}else if(preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0) == 1) {
			valorMoneda = c.getResources().getString(R.string.valor_dolar);
		}else if(preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0) == 2){
			valorMoneda = c.getResources().getString(R.string.valor_libra);
		}
		*/
		
		switch (preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0)) {
		case 0:
			valorMoneda = c.getResources().getString(R.string.valor_euro);
			break;
		case 1:
			valorMoneda = c.getResources().getString(R.string.valor_dolar);
			break;
		case 2:
			valorMoneda = c.getResources().getString(R.string.valor_libra);
			break;
		case 3:
			valorMoneda = c.getResources().getString(R.string.valor_peso_mexicano);
			break;
		case 4:
			valorMoneda = c.getResources().getString(R.string.valor_peso_argentino);
			break;
		case 5:
			valorMoneda = c.getResources().getString(R.string.valor_boliviano);
			break;
		case 6:
			valorMoneda = c.getResources().getString(R.string.valor_real_brasileño);
			break;
		case 7:
			valorMoneda = c.getResources().getString(R.string.valor_peso_chileno);
			break;
		case 8:
			valorMoneda = c.getResources().getString(R.string.valor_peso_colombiano);
			break;
		case 9:// Paraguay
			valorMoneda = c.getResources().getString(R.string.valor_guaraní);
			break;
		case 10:// Peru
			valorMoneda = c.getResources().getString(R.string.valor_nuevo_sol);
			break;
		case 11:
			valorMoneda = c.getResources().getString(R.string.valor_peso_uruguayo);
			break;
		case 12:// Venezuela
			valorMoneda = c.getResources().getString(R.string.valor_bolivar);
			break;
		default:
			break;
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
