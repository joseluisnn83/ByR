package com.joseluisnn.tt;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.joseluisnn.mycalendar.CalendarioView;
import com.joseluisnn.mycalendar.CalendarioView.ObservadorCalendarioView;

public class DatasActivityScreen extends Activity {

	private RelativeLayout rlcalendario;
	// Clase para crear el Calendario
	private CalendarioView cv;
	// Iconos para la búsqueda de Fechas
	private ImageView ivToday;
	private ImageView ivSearchDate;
	// Variables referentes al mes y año elegidos por el usuario en los combobox
	private int mes;
	private int anyo;
	// Variables para la animación de los iconos
	private Animation animacionBotonPulsado, animacionBotonLevantado;
	// Tiempo de pulsación inicial del icono
	private long tiempoDePulsacionInicial;

	private Calendar calendarToday = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_datas_screen);

		animacionBotonPulsado = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_pulsado);
		animacionBotonLevantado = AnimationUtils.loadAnimation(this,
				R.anim.animacion_boton_levantado);

		rlcalendario = (RelativeLayout) findViewById(R.id.RLCalendario);

		cv = new CalendarioView(this, null);
		rlcalendario.addView(cv);

		ivToday = (ImageView) findViewById(R.id.ivActivityDatasScreenToday);
		ivSearchDate = (ImageView) findViewById(R.id.ivActivityDatasScreenSearchDate);

		/*
		 * Obtengo el mes y año actual para los spinner
		 */
		mes = calendarToday.get(Calendar.MONTH);
		anyo = calendarToday.get(Calendar.YEAR);

		// Evento onTouchListener del icono ivSearchDate el cual
		// me sitúa el calendario a la fecha elegida por el usuario
		ivSearchDate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Evento donde se pulsa el botón para buscar una fecha en
				// concreto
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					ivSearchDate.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						// lanzo el dialog con la fecha a buscar por el usuario
						ivSearchDate.startAnimation(animacionBotonLevantado);
						Dialog dialogo;
						dialogo = crearDialogoBuscarFecha();
						dialogo.show();
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					ivSearchDate.startAnimation(animacionBotonLevantado);
					break;
				case MotionEvent.ACTION_CANCEL:
					ivSearchDate.startAnimation(animacionBotonLevantado);
					break;

				}

				return true;
			}
		});

		// Evento onTouchListener del icono ivToday el cual
		// me sitúa el calendario a fecha de hoy
		ivToday.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO
				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					tiempoDePulsacionInicial = event.getEventTime();
					ivToday.startAnimation(animacionBotonPulsado);
					break;
				case MotionEvent.ACTION_UP:

					if (event.getEventTime() - tiempoDePulsacionInicial <= 2000) {
						// Pongo la fecha del calendario a HOY
						ivToday.startAnimation(animacionBotonLevantado);
						situarFechaDeHoy();
					}
					// Si he mantenido el botón pulsado más de dos segundos
					// cancelo la operación
					ivToday.startAnimation(animacionBotonLevantado);
					break;
				case MotionEvent.ACTION_CANCEL:
					ivToday.startAnimation(animacionBotonLevantado);
					break;

				}

				return true;

			}
		});

		/*
		 * Observador que implemento de la clase CalendarioView para ver el
		 * cambio de fecha en el Calendario y que me actualiza la variable
		 * calendarToday.
		 */
		cv.setObservadorCalendarioView(new ObservadorCalendarioView() {

			@Override
			public void cambioFecha(Calendar c) {
				/*
				 * TODO Recibo el Evento de la clase CalendarioView cuando se
				 * cambia de fecha en la Vista del calendario y actualizo la
				 * variable calendarToday
				 */
				calendarToday.set(Calendar.YEAR, c.get(Calendar.YEAR));
				calendarToday.set(Calendar.MONTH, c.get(Calendar.MONTH));
				calendarToday.set(Calendar.DAY_OF_MONTH,
						c.get(Calendar.DAY_OF_MONTH));

			}

			@Override
			public void lanzarActividadGestionDatos(Calendar c, int dia) {
				/*
				 * TODO Método que me va a lanzar la Actividad para gestionar
				 * los ingresos y gastos de un día en concreto
				 */

				lanzarActividadGD(c, dia);

			}
		});

	}	

	public void lanzarActividadGD(Calendar c, int dia) {

		String cadena_fecha = new String();
		String fecha = new String();
		String month;
		String day;
		Integer entero_fecha;

		// Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		// Si el mes solo tiene un dígito le pongo un cero delante
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + dia;
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Pongo la fecha de Calendar pasada por parámetro al día pasado también
		// por parámetro
		c.set(Calendar.DATE, dia);

		cadena_fecha = "" + obtenerDiaSemana(c.get(Calendar.DAY_OF_WEEK))
				+ ", " + dia + " "
				+ getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + obtenerMes(c.get(Calendar.MONTH)) + " "
				+ getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + c.get(Calendar.YEAR);

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;
		// Lo paso a entero
		entero_fecha = Integer.valueOf(fecha);

		// Me creo el bundle para pasarle los parámetros a la Acitvidad
		// DatasActivity
		Bundle bundle = new Bundle();
		bundle.putString("cadena_fecha", cadena_fecha);
		bundle.putInt("entero_fecha", entero_fecha.intValue());

		Intent intent = new Intent(this, DatasActivity.class);
		intent.putExtras(bundle);

		/*
		 * Lanzo la Activity de manera que me devuelva valores; para saber que
		 * proviene de la Actividad corresta el @requestcode se lo he puesto a 1
		 * aunque no haría falta ya que sólo vendrán valores de la Actividad
		 * DatasActivity
		 */
		startActivityForResult(intent, 1);

	}

	/*
	 * Método que me devuelve el mes según el entero pasado por parámetro
	 */
	private String obtenerMes(int month) {

		String m = new String();

		switch (month) {

		case 0:
			m = getResources().getString(R.string.datasactivity_ene);
			break;
		case 1:
			m = getResources().getString(R.string.datasactivity_feb);
			break;
		case 2:
			m = getResources().getString(R.string.datasactivity_mar);
			break;
		case 3:
			m = getResources().getString(R.string.datasactivity_abr);
			break;
		case 4:
			m = getResources().getString(R.string.datasactivity_may);
			break;
		case 5:
			m = getResources().getString(R.string.datasactivity_jun);
			break;
		case 6:
			m = getResources().getString(R.string.datasactivity_jul);
			break;
		case 7:
			m = getResources().getString(R.string.datasactivity_ago);
			break;
		case 8:
			m = getResources().getString(R.string.datasactivity_sep);
			break;
		case 9:
			m = getResources().getString(R.string.datasactivity_oct);
			break;
		case 10:
			m = getResources().getString(R.string.datasactivity_nov);
			break;
		case 11:
			m = getResources().getString(R.string.datasactivity_dic);
			break;
		default:
			m = "error";
			break;
		}

		return m;

	}

	/*
	 * Método que me devuelve el día de la semana según el entero pasado por
	 * parámetro
	 */
	private String obtenerDiaSemana(int day) {

		String d = new String();

		switch (day) {
		case 1:
			d = getResources().getString(R.string.datasactivity_dom);
			break;
		case 2:
			d = getResources().getString(R.string.datasactivity_lun);
			break;
		case 3:
			d = getResources().getString(R.string.datasactivity_martes);
			break;
		case 4:
			d = getResources().getString(R.string.datasactivity_mie);
			break;
		case 5:
			d = getResources().getString(R.string.datasactivity_jue);
			break;
		case 6:
			d = getResources().getString(R.string.datasactivity_vie);
			break;
		case 7:
			d = getResources().getString(R.string.datasactivity_sab);
			break;
		default:
			break;
		}

		return d;
	}

	/*
	 * Dialog para situar el calendario en una fecha determinada por el usuario
	 */
	private Dialog crearDialogoBuscarFecha() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		View layout = inflater.inflate(
				R.layout.dialog_search_date_datas_activity, null);

		final DatePicker date = (DatePicker) layout
				.findViewById(R.id.datePickerDatasActivity);

		// inicializo la fecha en el DatePicker
		date.updateDate(calendarToday.get(Calendar.YEAR),
				calendarToday.get(Calendar.MONTH),
				calendarToday.get(Calendar.DAY_OF_MONTH));

		// Le inserto el layout al Dialog con LayoutInflater
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(layout);

		builder.setTitle(getResources().getString(R.string.mycalendar_buscar_fecha));

		// builder.setView(valorIngreso);
		builder.setIcon(android.R.drawable.ic_menu_my_calendar);

		builder.setPositiveButton(R.string.botonAceptar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Actualizo la variable calendarToday
						calendarToday.set(Calendar.YEAR, date.getYear());
						calendarToday.set(Calendar.MONTH, date.getMonth());
						calendarToday.set(Calendar.DAY_OF_MONTH,
								date.getDayOfMonth());

						// lanzo el método que me situa la fecha del calendario
						// a la que ha elegido el usuario
						mes = calendarToday.get(Calendar.MONTH);
						anyo = calendarToday.get(Calendar.YEAR);

						cv.busquedaFechaCalendarioView(mes, anyo);

					}
				});

		builder.setNegativeButton(R.string.botonCancelar,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO No hago nada
						dialog.cancel();
					}
				});

		return builder.create();
	}

	/*
	 * Método que me sitúa el Calendario a fecha de hoy
	 */
	private void situarFechaDeHoy() {

		calendarToday = Calendar.getInstance();

		cv.busquedaFechaCalendarioView(calendarToday.get(Calendar.MONTH),
				calendarToday.get(Calendar.YEAR));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * TODO Método que recibe parámetros de la Actividad DatasActivity que
		 * acaba de ser destruida (ha finalizado) y para colorear las casillas
		 * modificadas del calendario vuelvo a realizar la busqueda de la última
		 * fecha que se modificó o que se situó el usuario (haya realizado
		 * cambios o no)
		 */
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1 && resultCode == Activity.RESULT_OK
				&& data != null) {

			int month = data.getExtras().getInt("mes");
			int year = data.getExtras().getInt("anyo");

			// Actualizo la variable calendarToday
			calendarToday.set(Calendar.YEAR, year);
			calendarToday.set(Calendar.MONTH, month);

			cv.busquedaFechaCalendarioView(month, year);

		}

	}

}
