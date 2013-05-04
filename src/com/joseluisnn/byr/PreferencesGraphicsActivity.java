package com.joseluisnn.byr;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import com.bugsense.trace.BugSenseHandler;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

public class PreferencesGraphicsActivity extends PreferenceActivity {

	/*
	 * VARIABLES DE CONFIGURACION QUE SON RELLENADAS A PARTIR DE LA LECTURA DEL
	 * ARCHIVO SHAREDPREFENRENCES tipoGrafica : 0 -> Anual , 1 -> Mensual , 2 ->
	 * Diario valoresGraficas: 0 -> valoresGrafica : 0 -> Historico , 1 ->
	 * Prevision anyoCentralGrafica : año a partir del cual se harán las
	 * consultas a la BD para devolcer los valores mesCentralGrafica : mes a
	 * partir del cual se harán las consultas a la BD para devolcer los valores
	 * mostrarValores : variables para mostrar los valores en los puntos o picos
	 * de las líneas gráficas mostrarLinea : variables para mostrar las líneas
	 * gráficas
	 */
	private int tipoGrafica;
	private int valoresGrafica;
	private int anyoLimiteGrafica;
	private int mesLimiteGrafica;
	private boolean mostrarValoresIngresos;
	private boolean mostrarValoresGastos;
	private boolean mostrarValoresBalance;
	private boolean mostrarLineaIngresos;
	private boolean mostrarLineaGastos;
	private boolean mostrarLineaBalance;
	/*
	 * Las dos listas de que me indican que tipo de gŕaica y valores se van a
	 * mostrar
	 */
	private ListPreference listPreferenceTipoGrafica;
	private ListPreference listPreferenceValoresGrafica;
	/*
	 * Listas para las fechas
	 */
	private ListPreference listPreferenceYears;
	private ListPreference listPreferenceMonths;
	/*
	 * Variables para mostrar líneas y valores en la gráfica
	 */
	private CheckBoxPreference cbLineaIngresos;
	private CheckBoxPreference cbLineaGastos;
	private CheckBoxPreference cbLineaBalance;
	private CheckBoxPreference cbValoresIngresos;
	private CheckBoxPreference cbValoresGastos;
	private CheckBoxPreference cbValoresBalance;
	/*
	 * Variable para saber si se ha modificao el archivo de configuración
	 */
	private boolean modificado;
	/*
	 * variables del tipo de dato SharedPreferences para la configuración de la
	 * gráfica
	 */
	private SharedPreferences preferenceConfiguracionPrivate;
	private SingletonConfigurationSharedPreferences singleton_csp;
	private Editor editorPreference;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// BugSenseHandler.initAndStartSession(PreferencesGraphicsActivity.this,
		// "c815f559");

		addPreferencesFromResource(R.xml.preferences_configuration_graphics);

		// Instancio los objetos del SharedPreferences de la configuración de la
		// gráfica
		// Le indico en qué archivo de configuración va a ser guardada la
		// información
		singleton_csp = new SingletonConfigurationSharedPreferences();
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION_GRAFICAS,
				Context.MODE_PRIVATE);
		editorPreference = preferenceConfiguracionPrivate.edit();

		// Obtengo los objetos del PreferenceScreen
		listPreferenceTipoGrafica = (ListPreference) findPreference("lpTipoGrafica");
		listPreferenceValoresGrafica = (ListPreference) findPreference("lpValoresGrafica");
		listPreferenceYears = (ListPreference) findPreference("lpYears");
		listPreferenceMonths = (ListPreference) findPreference("lpMonths");
		cbLineaIngresos = (CheckBoxPreference) findPreference("cbpLineIngresos");
		cbLineaGastos = (CheckBoxPreference) findPreference("cbpLineGastos");
		cbLineaBalance = (CheckBoxPreference) findPreference("cbpLineBalance");
		cbValoresIngresos = (CheckBoxPreference) findPreference("cbpValuesIngresos");
		cbValoresGastos = (CheckBoxPreference) findPreference("cbpValuesGastos");
		cbValoresBalance = (CheckBoxPreference) findPreference("cbpValuesBalance");

		/**
		 * Los siguientes métodos recogen el evento onChangeListner de cada
		 * comopnente del PreferenceScreen y actualizo el valor en el archivo de
		 * configuración si se ha modificado un nuevo valor
		 */
		listPreferenceTipoGrafica
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						/*
						 * TODO Si se ha modificado el valor entra en el if a
						 * modificar el archivo de configuracion
						 */
						int newTipoGrafica = Integer.valueOf(newValue
								.toString());

						if (newTipoGrafica != tipoGrafica) {

							// Variable para modificar el valor del archivo de
							// configuración de la gráfica
							editorPreference.putInt(
									singleton_csp.KEY_LPTIPOGRAFICA,
									newTipoGrafica);
							editorPreference.commit();

							tipoGrafica = newTipoGrafica;
							modificado = true;

						}

						return true;
					}
				});

		listPreferenceValoresGrafica
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						/*
						 * TODO Si se ha modificado el valor entra en el if a
						 * modificar el archivo de configuracion
						 */
						int newValoresGrafica = Integer.valueOf(newValue
								.toString());

						if (newValoresGrafica != valoresGrafica) {

							// Variable para modificar el valor del archivo de
							// configuración de la gráfica
							editorPreference.putInt(
									singleton_csp.KEY_LPVALORESGRAFICA,
									newValoresGrafica);
							editorPreference.commit();

							valoresGrafica = newValoresGrafica;
							modificado = true;

							if (newValoresGrafica == 0) {
								listPreferenceYears.setEnabled(true);
								listPreferenceMonths.setEnabled(true);
							} else {
								listPreferenceYears.setEnabled(false);
								listPreferenceMonths.setEnabled(false);
							}

						}

						return true;
					}
				});

		listPreferenceYears
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						/*
						 * TODO // Si se ha modificado el valor entra en el if a
						 * modificar el archivo de configuracion
						 */
						int newAnyo = Integer.valueOf(newValue.toString());
						boolean correcto = false;

						if (newAnyo != anyoLimiteGrafica) {

							Calendar c = Calendar.getInstance();

							if ((newAnyo == c.get(Calendar.YEAR) &&
									mesLimiteGrafica <= (c.get(Calendar.MONTH) + 1)) || 
									newAnyo < c.get(Calendar.YEAR)) {

								// Actualizo el valor anyo del archivo de
								// configuración de la gráfica
								editorPreference.putInt(
										singleton_csp.KEY_LPYEARS, newAnyo);
								editorPreference.commit();

								anyoLimiteGrafica = newAnyo;
								modificado = true;

								correcto = true;

							} else {
								lanzarAdvertencia("No está permitido seleccionar fechas "
										+ "futuras para mostrar valores Históricos.");
							}
						}

						return correcto;
					}
				});

		listPreferenceMonths
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						/*
						 * TODO Si se ha modificado el valor entra en el if a
						 * modificar el archivo de configuracion
						 */
						int newMes = Integer.valueOf(newValue.toString());
						boolean correcto = false;

						if (newMes != mesLimiteGrafica) {

							Calendar c = Calendar.getInstance();

							if ( (anyoLimiteGrafica == c.get(Calendar.YEAR) &&
									newMes <= (c.get(Calendar.MONTH) + 1)) || 
									anyoLimiteGrafica < c.get(Calendar.YEAR)) {

								// Actualizo el valor mes del archivo de
								// configuración de la gráfica
								editorPreference.putInt(
										singleton_csp.KEY_LPMONTHS, newMes);
								editorPreference.commit();

								mesLimiteGrafica = newMes;
								modificado = true;

								correcto = true;

							} else {
								lanzarAdvertencia("No está permitido seleccionar fechas "
										+ "futuras para mostrar valores Históricos.");
							}
						}

						return correcto;
					}
				});

		cbLineaIngresos
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						mostrarLineaIngresos = (Boolean) newValue;
						// Actualizo el valor del archivo de
						// configuración de la gráfica
						editorPreference.putBoolean(
								singleton_csp.KEY_CBPLINEINGRESOS,
								mostrarLineaIngresos);
						editorPreference.commit();
						modificado = true;

						return true;
					}
				});

		cbLineaGastos
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						mostrarLineaGastos = (Boolean) newValue;
						// Actualizo el valor del archivo de
						// configuración de la gráfica
						editorPreference.putBoolean(
								singleton_csp.KEY_CBPLINEGASTOS,
								mostrarLineaGastos);
						editorPreference.commit();
						modificado = true;

						return true;
					}
				});

		cbLineaBalance
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						mostrarLineaBalance = (Boolean) newValue;
						// Actualizo el valor del archivo de
						// configuración de la gráfica
						editorPreference.putBoolean(
								singleton_csp.KEY_CBPLINEBALANCE,
								mostrarLineaBalance);
						editorPreference.commit();
						modificado = true;

						return true;
					}
				});

		cbValoresIngresos
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						mostrarValoresIngresos = (Boolean) newValue;
						// Actualizo el valor del archivo de
						// configuración de la gráfica
						editorPreference.putBoolean(
								singleton_csp.KEY_CBPVALUESINGRESOS,
								mostrarValoresIngresos);
						editorPreference.commit();
						modificado = true;

						return true;
					}
				});

		cbValoresGastos
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						mostrarValoresGastos = (Boolean) newValue;
						// Actualizo el valor del archivo de
						// configuración de la gráfica
						editorPreference.putBoolean(
								singleton_csp.KEY_CBPVALUESGASTOS,
								mostrarValoresGastos);
						editorPreference.commit();
						modificado = true;

						return true;
					}
				});

		cbValoresBalance
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						mostrarValoresBalance = (Boolean) newValue;
						// Actualizo el valor del archivo de
						// configuración de la gráfica
						editorPreference.putBoolean(
								singleton_csp.KEY_CBPVALUESBALANCE,
								mostrarValoresBalance);
						editorPreference.commit();
						modificado = true;

						return true;
					}
				});

		/**
		 * Inicio el proceso de lectura del archivo de configuración
		 * (SharedPreference) y habilito o deshabilito la fecha según las
		 * opciones guardadas
		 */
		leerArchivoConfiguracion();
		habilitarFecha();

		/*
		 * Inicializo los valores del preferencesactivity
		 */

		modificado = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		// BugSenseHandler.closeSession(PreferencesGraphicsActivity.this);
	}

	/*
	 * Método que lee del archivo de configuración y carga las variables para
	 * dibujar correctamente la gráfica
	 */
	private void leerArchivoConfiguracion() {

		tipoGrafica = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPTIPOGRAFICA, 0);
		valoresGrafica = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPVALORESGRAFICA, 0);
		anyoLimiteGrafica = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPYEARS, 0);
		mesLimiteGrafica = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_LPMONTHS, 0);
		mostrarLineaIngresos = preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPLINEINGRESOS, false);
		mostrarLineaGastos = preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPLINEGASTOS, false);
		mostrarLineaBalance = preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPLINEBALANCE, false);
		mostrarValoresIngresos = preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPVALUESINGRESOS, false);
		mostrarValoresGastos = preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPVALUESGASTOS, false);
		mostrarValoresBalance = preferenceConfiguracionPrivate.getBoolean(
				singleton_csp.KEY_CBPVALUESBALANCE, false);

	}

	/*
	 * Método que me habilita o deshabilita la fecha central a mostrar en la
	 * gráfica en función de si el usuario ha elegido la gráfica con valores
	 * históricos (habilita) o valores con previsión (deshabilita)
	 */
	private void habilitarFecha() {

		if (valoresGrafica == 0) {
			listPreferenceYears.setEnabled(true);
			listPreferenceMonths.setEnabled(true);
		} else if (valoresGrafica == 1) {
			listPreferenceYears.setEnabled(false);
			listPreferenceMonths.setEnabled(false);
		}

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

		builder.setTitle("ADVERTENCIA");
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Evento de levantar la pulsación de una tecla del móvil

		Intent resultData = getIntent();

		// Entra en el IF si la tecla pulsada es la de salir o retorno
		if (keyCode == KeyEvent.KEYCODE_BACK && modificado) {

			resultData.putExtra("tipoGrafica", this.tipoGrafica);
			resultData.putExtra("valoresGrafica", this.valoresGrafica);
			resultData.putExtra("anyo", this.anyoLimiteGrafica);
			resultData.putExtra("mes", this.mesLimiteGrafica);
			resultData.putExtra("lineaIngresos", this.mostrarLineaIngresos);
			resultData.putExtra("lineaGastos", this.mostrarLineaGastos);
			resultData.putExtra("lineaBalance", this.mostrarLineaBalance);
			resultData.putExtra("valoresIngresos", this.mostrarValoresIngresos);
			resultData.putExtra("valoresGastos", this.mostrarValoresGastos);
			resultData.putExtra("valoresBalance", this.mostrarValoresBalance);

			setResult(Activity.RESULT_OK, resultData);

			finish();

		} else {
			setResult(Activity.RESULT_CANCELED, resultData);

			finish();

		}

		return true;
	}

}
