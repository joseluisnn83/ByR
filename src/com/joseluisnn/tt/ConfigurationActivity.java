package com.joseluisnn.tt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
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
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.joseluisnn.singleton.SingletonBroadcastReceiver;
import com.joseluisnn.singleton.SingletonConfigurationSharedPreferences;

public class ConfigurationActivity extends Activity {

	/*
	 * Constantes para indicar el tipo de Dialog a crear
	 */
	private static final int DIALOGO_TIPO_CONFIGURACION = 0;
	private static final int DIALOGO_INFORMES = 1;
	private static final int DIALOGO_EXPORT_DB = 2;
	private static final int DIALOGO_IMPORT_DB = 3;
	private static final int DIALOGO_MONEDAS = 4;
	/*
	 * Constantes para indicar las rutas del archivo de la BD
	 */
	private static final String DIRECTORIO_COPIA_BD_TARJETA_SD = "/copia_tt";
	private static final String CURRENT_DB_PATH = "//data//"
			+ "com.joseluisnn.tt" + "//databases//" + "BD_TT";
	private static final String BACKUP_DB_PATH = "/copia_tt/BD_TT";

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
	private LinearLayout llMonedas;
	private LinearLayout llDumpDB;
	private LinearLayout llLoadDB;

	// Variable que contiene la constante para saber qué Broadcast se ha enviado
	private SingletonBroadcastReceiver sbr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_configuration);

		// Obtengo la clase que contiene las constantes para enviar el Broadcast
		sbr = new SingletonBroadcastReceiver();
		// Obtengo los valores que se encuentran en el archivo de configuración
		singleton_csp = new SingletonConfigurationSharedPreferences();

		// Instancio los objetos View del diseño
		llTipoConfiguracion = (LinearLayout) findViewById(R.id.llTipoConfiguracion);
		llConceptosIngresos = (LinearLayout) findViewById(R.id.llConceptosIngresos);
		llConceptosGastos = (LinearLayout) findViewById(R.id.llConceptosGastos);
		llInformes = (LinearLayout) findViewById(R.id.llInformes);
		llMonedas = (LinearLayout) findViewById(R.id.llMonedas);
		llDumpDB = (LinearLayout) findViewById(R.id.llDumpDB);
		llLoadDB = (LinearLayout) findViewById(R.id.llLoadDB);

		// Evento OnClick del Layout del tipo de configuración
		llTipoConfiguracion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Bundle b = new Bundle();
				// b.putInt("tipoConfig", tipoConfig);

				Dialog d = onCreateDialog(DIALOGO_TIPO_CONFIGURACION, b);

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

		// Evento OnClick del Layout de los Informes
		llInformes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Bundle b = new Bundle();
				Dialog d = onCreateDialog(DIALOGO_INFORMES, b);

				d.show();
			}
		});

		// Evento OnClick del Layout de las monedas
		llMonedas.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Bundle b = new Bundle();
				Dialog d = onCreateDialog(DIALOGO_MONEDAS, b);

				d.show();
			}
		});

		// Evento OnClick del Layout de la copia de seguridad de la BD
		llDumpDB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Realizo la copia de la BD en la tarjeta SD

				// creating a new folder for the database to be backuped to
				File direct = new File(Environment
						.getExternalStorageDirectory()
						+ DIRECTORIO_COPIA_BD_TARJETA_SD);

				if (!direct.exists()) {
					if (direct.mkdir()) {
						// directory is created;
					}
				}

				Dialog d = onCreateDialog(DIALOGO_EXPORT_DB, null);

				d.show();
			}
		});

		llLoadDB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Copio y sobreescribo la BD guardada en la tarjeta SD al
				// directorio de la BD original

				// creating a new folder for the database to be backuped to
				File direct = new File(Environment
						.getExternalStorageDirectory()
						+ DIRECTORIO_COPIA_BD_TARJETA_SD);

				if (direct.exists()) {
					Dialog d = onCreateDialog(DIALOGO_IMPORT_DB, null);

					d.show();
				} else {
					lanzarAdvertencia(getResources().getString(
							R.string.configuration_activity_importar_valores));
				}
			}
		});

	}

	/*
	 * Método que me crea los distintos tipos de Dialog según la opción de
	 * configuración elegida por el usuario
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub

		Dialog dialogo;

		switch (id) {
		case DIALOGO_TIPO_CONFIGURACION:
			dialogo = crearDialogoSeleccionTipoConfiguracion();
			break;
		case DIALOGO_INFORMES:
			dialogo = crearDialogoSeleccionInformes();
			break;
		case DIALOGO_EXPORT_DB:
			dialogo = crearDialogExportDB();
			break;
		case DIALOGO_IMPORT_DB:
			dialogo = crearDialogImportDB();
			break;
		case DIALOGO_MONEDAS:
			dialogo = crearDialogoTipoMoneda();
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
	private Dialog crearDialogoSeleccionTipoConfiguracion() {
		final String[] items = {
				getResources().getString(R.string.configuracion_basica),
				getResources().getString(R.string.configuracion_avanzada) };
		// Obtengo el valor del tipo de configuración guardado previamente
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		final int tipoConfiguracion = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_CONFIGURACION, 0);

		// Variable para modificar los valores del archivo de configuración
		editorPreference = preferenceConfiguracionPrivate.edit();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(R.string.mtTipoConfiguracion));
		builder.setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_preferences));
		builder.setSingleChoiceItems(items, tipoConfiguracion,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (tipoConfiguracion != item) {// Si se ha pulsado una
														// opción diferente a la
														// anterior guardada se
														// realiza el cambio en
														// el fichero
							editorPreference.putInt(
									singleton_csp.KEY_TIPO_CONFIGURACION, item);
							editorPreference.commit();

							// Envío la señal de Broadcast para que
							// PrincipalScreenActivity lo reciba
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
	 * Dialog para elegir el tipo de infrome que quiere el usuario seleccionar
	 * por defecto
	 */
	private Dialog crearDialogoSeleccionInformes() {
		final String[] items = {
				getResources().getString(R.string.PestanyaInformeSemanal_title),
				getResources().getString(R.string.PestanyaInformeMensual_title),
				getResources().getString(
						R.string.PestanyaInformeTrimestral_title),
				getResources().getString(R.string.PestanyaInformeAnual_title),
				getResources().getString(R.string.PestanyaInformeLibre_title) };
		// Obtengo el valor del informe a seleccionar por defecto guardado
		// previamente
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		final int informeSeleccionado = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_INFORME_POR_DEFECTO, 0);

		// Variable para modificar los valores del archivo de configuración
		editorPreference = preferenceConfiguracionPrivate.edit();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(
				R.string.configuracion_dialog_informes_title));
		builder.setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_agenda));
		builder.setSingleChoiceItems(items, informeSeleccionado,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (informeSeleccionado != item) {
							// Si se ha pulsado una opción diferente a la
							// anterior guardada se realiza el cambio en el
							// fichero
							editorPreference
									.putInt(singleton_csp.KEY_INFORME_POR_DEFECTO,
											item);
							editorPreference.commit();

						}
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Dialog para elegir el tipo de moneda que quiere el usuario utilizar
	 */
	private Dialog crearDialogoTipoMoneda() {
		final String[] items = {
				getResources().getString(R.string.moneda_euro),
				getResources().getString(R.string.moneda_dolar),
				getResources().getString(R.string.moneda_libra) };
		// Obtengo el valor del tipo de moneda guardado
		// previamente
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		final int tipoMoneda = preferenceConfiguracionPrivate.getInt(
				singleton_csp.KEY_TIPO_MONEDA, 0);

		// Variable para modificar los valores del archivo de configuración
		editorPreference = preferenceConfiguracionPrivate.edit();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(
				R.string.configuracion_dialog_tipo_moneda_title));
		builder.setIcon(getResources().getDrawable(
				android.R.drawable.ic_menu_mylocation));
		builder.setSingleChoiceItems(items, tipoMoneda,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int item) {
						if (tipoMoneda != item) {
							// Si se ha pulsado una opción diferente a la
							// anterior guardada se realiza el cambio en el
							// fichero
							editorPreference
									.putInt(singleton_csp.KEY_TIPO_MONEDA,
											item);
							editorPreference.commit();

						}
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Método que lanza un Dialog de una advertencia producida
	 */
	private void lanzarAdvertencia(String advice) {

		Dialog d = crearDialogAdvertencia(advice);

		d.show();

	}

	/*
	 * Dialog para preguntar si el usuario quiere importar datos guardados en
	 * una BD en la tarjeta SD
	 */
	private Dialog crearDialogAdvertencia(String aviso) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(
				R.string.configuracion_advertencia));
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(aviso);

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Cierro el Dialog
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Dialog para preguntar si el usuario quiere realizar la copia de seguridad
	 */
	private Dialog crearDialogExportDB() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(
				R.string.configuracion_informacion));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage("¿Desea guardar una copia de seguridad de la base de datos?");

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Realizo la copia de seguridad
						exportDB();
					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Cierro el Dialog
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Dialog para preguntar si el usuario quiere importar los datos guardados
	 * en la BD almacenados en la tarjeta SD
	 */
	private Dialog crearDialogImportDB() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Obtengo la fecha de copia de seguridad a la BD abriendo previamente
		// el archivo de configuración
		preferenceConfiguracionPrivate = getSharedPreferences(
				singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
				Context.MODE_PRIVATE);

		builder.setTitle(getResources().getString(
				R.string.configuracion_informacion));
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setMessage(getResources().getString(
				R.string.configuracion_dialog_message_importar_valores)
				+ " "
				+ preferenceConfiguracionPrivate.getString(
						singleton_csp.KEY_FECHA_COPIA_SEGURIDAD, "?") + " ?");

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Copio los datos de la tarjeta SD al directorio
						// del programa
						importDB();
					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Cierro el Dialog
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Método que lanza la Activity ConceptosIngresosActivity para insertar,
	 * modificar o borrar conceptos de ingresos
	 */
	private void lanzarPantallaConceptosIngresos() {
		Intent intent = new Intent(this, ConceptosIngresosActivity.class);
		startActivity(intent);
	}

	/*
	 * Método que lanza la Activity ConceptosGastosActivity para insertar,
	 * modificar o borrar conceptos de gastos
	 */
	private void lanzarPantallaConceptosGastos() {
		Intent intent = new Intent(this, ConceptosGastosActivity.class);
		startActivity(intent);
	}

	/*
	 * Exporting database
	 */
	private void exportDB() {
		// TODO Método donde realizo la copia de seguridad de la BD

		try {
			// Obtengo el directorio raiz de la tarjeta SD
			File sd = Environment.getExternalStorageDirectory();
			// Obtengo el directorio del dispositivo /data
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {

				// copio del directio /data/data al ...
				File currentDB = new File(data, CURRENT_DB_PATH);
				// directorio de la tarjeta SD
				File backupDB = new File(sd, BACKUP_DB_PATH);

				// Origen (src) en el directorio /data/data/ .... del
				// dispositivo
				FileChannel src = new FileInputStream(currentDB).getChannel();
				// Destino (dst) es el directorio /copia_tt en SD
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();

				// Inserto la fecha de la copia de seguridad en el archivo de
				// configuración
				String copia_fecha = obtenerFechaHoy();

				preferenceConfiguracionPrivate = getSharedPreferences(
						singleton_csp.NOMBRE_ARCHIVO_CONFIGURACION,
						Context.MODE_PRIVATE);
				// Variable para modificar los valores del archivo de
				// configuración
				editorPreference = preferenceConfiguracionPrivate.edit();

				editorPreference.putString(
						singleton_csp.KEY_FECHA_COPIA_SEGURIDAD, copia_fecha);
				editorPreference.commit();

				// Toast.makeText(getBaseContext(), backupDB.toString(),
				// Toast.LENGTH_LONG).show();
				Toast.makeText(
						getBaseContext(),
						getResources().getString(
								R.string.configuracion_bd_guardada)
								+ " " + copia_fecha, Toast.LENGTH_LONG).show();

			} else if (sd.canRead() && !sd.canWrite()) {

				// Sólo se puede leer en la tarjeta SD
				Toast.makeText(
						getBaseContext(),
						getResources().getString(
								R.string.configuracion_bd_solo_lectura),
						Toast.LENGTH_LONG).show();

			} else if (!sd.canRead() && !sd.canWrite()) {

				// No se puede leer ni escribir
				Toast.makeText(
						getBaseContext(),
						getResources().getString(
								R.string.configuracion_bd_error_escritura),
						Toast.LENGTH_LONG).show();

			}

		} catch (Exception e) {

			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
					.show();

		}
	}

	/*
	 * Importing database
	 */
	private void importDB() {
		// TODO Método donde importo la copia de la BD de la tarjeta SD al
		// directorio del programa donde se guarda en el dispositivo

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			// copio del directorio directorio de la tarjeta SD al ...
			File currentDB = new File(sd, BACKUP_DB_PATH);
			// directio /data/data
			File backupDB = new File(data, CURRENT_DB_PATH);

			if (backupDB.canWrite()) {

				// Origen (src) es el directorio /copia_tt en SD
				FileChannel src = new FileInputStream(currentDB).getChannel();
				// Destino (dst) en el directorio /data/data/ .... del
				// dispositivo
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(
						getBaseContext(),
						getResources().getString(
								R.string.configuracion_bd_importada),
						Toast.LENGTH_LONG).show();

			} else if (backupDB.canRead() && !backupDB.canWrite()) {

				// Sólo se puede leer en el directorio del programa de la app
				Toast.makeText(
						getBaseContext(),
						getResources()
								.getString(
										R.string.configuracion_bd_importacion_cancelada),
						Toast.LENGTH_LONG).show();

			} else if (!backupDB.canRead() && !backupDB.canWrite()) {

				// No se puede leer ni escribir
				Toast.makeText(
						getBaseContext(),
						getResources()
								.getString(
										R.string.configuracion_bd_error_escritura_directorio),
						Toast.LENGTH_LONG).show();

			}

		} catch (Exception e) {

			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
					.show();

		}
	}

	/*
	 * Método que me devuelve en un String la fecha de hoy
	 */
	private String obtenerFechaHoy() {

		String fecha;
		String month;
		String day;

		Calendar c = Calendar.getInstance();

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato DDMMAAAA
		fecha = day + "-" + month + "-" + c.get(Calendar.YEAR);

		return fecha;

	}

}
