package com.joseluisnn.tt;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.joseluisnn.databases.DBAdapter;
import com.joseluisnn.objetos.Concepto;

public class ConceptosGastosActivity extends ListActivity {

	/*
	 * Constantes para indicar el tipo de Dialog a crear
	 */
	private static final int DIALOGO_INSERTAR_CONCEPTO = 0;
	private static final int DIALOGO_ACTUALIZAR_CONCEPTO = 1;
	private static final int DIALOGO_BORRAR_CONCEPTO = 2;

	// Declaro los dos tipos de conceptos posibles
	private static String TIPO_CONCEPTO_GASTO = "gasto";

	// Variable para la BASE DE DATOS
	private DBAdapter dba;

	// ArrayList de conceptos que contiene el id,nombre y tipo
	private ArrayList<Concepto> listaConceptosGastos;
	// ArrayList de conceptos que contiene solo el nombre para poder
	// visualizarlo en el ListView
	private ArrayList<String> listaConceptosGastosAux;
	// Variable LinearLayout para poder añadir Conceptos de Gastos
	private LinearLayout llConceptoGastos;
	// Variable ArrayAdapter que se lo voy a asignar al ListView
	private ArrayAdapter<String> aadapter;

	// Variable para obtener el concepto pulsado para actualizarlo
	private int conceptoPulsado;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/*
		 * Quitamos barra de titulo de la Actividad Debe ser ejecutada esta
		 * instruccion antes del setContentView para que no cargue las imágenes
		 */
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_conceptos_gastos);

		// Instancio el LinearLayout donde al pulsar se van a insertar conceptos
		// de gastos
		llConceptoGastos = (LinearLayout) findViewById(R.id.LinearLayoutConceptosGGeneral);

		// Instancio la Base de Datos
		// dba = new DBAdapter(this);
		dba = DBAdapter.getInstance(this);

		llConceptoGastos.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog d = onCreateDialog(DIALOGO_INSERTAR_CONCEPTO);

				d.show();

			}
		});

		// incicializo el concepto pulsado
		conceptoPulsado = -1;

		// Abro la Base de Datos solo en modo lectura
		dba.openREAD();
		// Cargo en listaConceptos los conceptos de ingresos que tengo en la BD
		listaConceptosGastos = dba.listarConceptos(TIPO_CONCEPTO_GASTO);
		// Cargo el arrays de conceptos con String para iniciar el ListView
		listaConceptosGastosAux = dba.listarArrayConceptos(TIPO_CONCEPTO_GASTO);
		// Inicializo la variable ArrayAdapter
		aadapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listaConceptosGastosAux);

		// Cargo el ListView
		setListAdapter(aadapter);

		/*
		 * hay que registrar todos los items del listView para que suministren
		 * un ContextMenu al ser pulsado
		 */
		registerForContextMenu(getListView());

		// Cierro la BD en modo lectura
		dba.close();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub

		Dialog dialogo;

		switch (id) {
		case DIALOGO_INSERTAR_CONCEPTO:
			dialogo = crearDialogoInsertarConceptoGasto();
			break;
		case DIALOGO_ACTUALIZAR_CONCEPTO:
			dialogo = crearDialogoActualizarConceptoGasto();
			break;
		case DIALOGO_BORRAR_CONCEPTO:
			dialogo = crearDialogBorrarConceptoGasto();
			break;
		default:
			dialogo = new Dialog(this);
			break;
		}

		return dialogo;

	}

	/*
	 * Dialog para insertar un concepto de Gasto
	 */
	private Dialog crearDialogoInsertarConceptoGasto() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final EditText conceptoGasto = new EditText(this);

		builder.setTitle(getResources().getString(
				R.string.conceptos_insertar_cg));
		builder.setView(conceptoGasto);
		builder.setIcon(android.R.drawable.ic_menu_add);

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO inserto en la BD el concepto insertado por el
						// usuario

						if (!conceptoGasto.getText().toString().equals("")) {

							if (conceptoGasto.getText().toString().indexOf("'") > 0
									|| conceptoGasto.getText().toString()
											.indexOf('"') > 0) {

								lanzarAdvertencia(getResources()
										.getString(
												R.string.conceptos_advertencia_caracteres));

							} else {

								Concepto c = new Concepto(-1, conceptoGasto
										.getText().toString(),
										TIPO_CONCEPTO_GASTO);
								// lo inserto en la BD
								int registro = dba.insertarConcepto(c);

								// Obtengo el id del concepto
								int id = dba.getIdConcepto(c.getNombre(),
										c.getTipo());
								c.setId(id);

								// Si no se ha lanzado la excepción de
								// constraint en la BD actualizo los Arrays y el
								// ListView
								if (registro == 1) {
									// Lo inserto la lista de Conceptos
									listaConceptosGastos.add(c);
									// Lo inserto en el ArrayList para que se
									// muestre en el ListView
									listaConceptosGastosAux.add(c.getNombre());

									// Notifico al ArrayAdapter que mi ArrayList
									// ha sido modificado
									aadapter.notifyDataSetChanged();
								}

							}

						}
					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO No hago nada
						;
					}
				});

		return builder.create();

	}

	/*
	 * Dialog para modificar un concepto de Gasto
	 */
	private Dialog crearDialogoActualizarConceptoGasto() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		final EditText conceptoGasto = new EditText(this);
		final Concepto conceptoAnterior = listaConceptosGastos
				.get(getConceptoPulsado());

		builder.setTitle(getResources().getString(
				R.string.conceptos_actualizar_cg));
		builder.setView(conceptoGasto);
		builder.setIcon(android.R.drawable.ic_menu_edit);

		conceptoGasto.setText(conceptoAnterior.getNombre());

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO inserto en la BD el concepto insertado por el
						// usuario

						if (!conceptoGasto.getText().toString().equals("")
								&& !conceptoAnterior.getNombre().equals(
										conceptoGasto.getText().toString())) {

							if (conceptoGasto.getText().toString().indexOf("'") > 0
									|| conceptoGasto.getText().toString()
											.indexOf('"') > 0) {

								lanzarAdvertencia(getResources()
										.getString(
												R.string.conceptos_advertencia_caracteres));

							} else {

								Concepto c = listaConceptosGastos
										.get(getConceptoPulsado());
								c.setNombre(conceptoGasto.getText().toString());

								// Actualizo el valor en la BD
								int registro = dba.actualizarConcepto(c);

								// Si no se ha lanzado la excepción de
								// constraint en
								// la BD actualizo los Arrays y el ListView
								if (registro == 1) {
									// Actualizo en las listas
									listaConceptosGastos.get(
											getConceptoPulsado()).setNombre(
											conceptoGasto.getText().toString());
									listaConceptosGastosAux.set(
											getConceptoPulsado(), conceptoGasto
													.getText().toString());

									// Notifico al ArrayAdapter que mi ArrayList
									// ha
									// sido modificado
									aadapter.notifyDataSetChanged();
								}

							}
						}
					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO No hago nada
						;
					}
				});

		return builder.create();

	}

	/*
	 * Dialog para BORRAR un concepto de Gasto
	 */
	private Dialog crearDialogBorrarConceptoGasto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(
				R.string.conceptos_confirmacion));
		builder.setIcon(android.R.drawable.ic_menu_delete);
		builder.setMessage(getResources().getString(
				R.string.conceptos_borrar_concepto));

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Borro en el ListView y la BD
						// lanzarMensaje("Borrar Ingreso: " + posicioABorrar);

						Concepto c = listaConceptosGastos
								.get(getConceptoPulsado());

						// Si el borrado es correcto entro en el if y elimino
						// los valores del ListView
						if (dba.borrarValoresSegunConcepto(c) == 1) {

							// Lo borro de la lista de Conceptos
							listaConceptosGastos.remove(getConceptoPulsado());
							// Lo borro del ArrayList para que me lo quite del
							// ListView
							listaConceptosGastosAux
									.remove(getConceptoPulsado());

							// Notifico al ArrayAdapter que mi ArrayList ha sido
							// modificado
							aadapter.notifyDataSetChanged();

						}
					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO no hago nada
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Método en el que obtengo el concepto que ha sido pulsado para
	 * actualizarlo
	 */
	private int getConceptoPulsado() {
		return this.conceptoPulsado;
	}

	/*
	 * Método que me indica el concepto que ha sido pulsado para actualizarlo
	 */
	private void setConceptoPulsado(int item) {
		this.conceptoPulsado = item;
	}

	@Override
	protected void onDestroy() {
		// TODO Cierro la base de datos cuando se destruye la Actividad si
		// estuviera abierta

		if (dba.isOpen()) {
			dba.close();
			// lanzarToast("Me cierra la BD en OnDestroy()");
		}

		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Cierro la BD en OnPause() cuando la Actividad no está en el foco

		dba.close();
		// lanzarToast("Me cierra la BD en OnPause()");

		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Abro la base de datos mientras la Actividad está Activa

		dba.openREADWRITE();
		// lanzarToast("Me abre la BD en OnResume()");

		super.onResume();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Item seleccionado del ContextMenu asociado al ListView

		Dialog d = null;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.delete:
			setConceptoPulsado((int) info.id);
			d = onCreateDialog(DIALOGO_BORRAR_CONCEPTO);

			d.show();

			return true;

		case R.id.edit:

			setConceptoPulsado((int) info.id);
			d = onCreateDialog(DIALOGO_ACTUALIZAR_CONCEPTO);

			d.show();

			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_configuracion_conceptos, menu);
	}

	private void lanzarAdvertencia(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
