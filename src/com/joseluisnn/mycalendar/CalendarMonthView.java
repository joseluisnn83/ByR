package com.joseluisnn.mycalendar;

import java.util.Calendar;
import java.util.Locale;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.joseluisnn.tt.R;
import com.joseluisnn.mycalendar.CalendarAdapter.DayCell;

/**
 * one month view.
 * 
 * Clase para la vista de un mes en concreto
 */
public class CalendarMonthView extends LinearLayout {

	/*
	 * Observador de los eventos del Calendario
	 */
	//private CalendarDatePick mObserver;

	/*
	 * Fecha con el que la vista (View) será iniciada
	 */
	private Calendar mInitialMonth;
	/*
	 * Adaptador para el Calendario
	 */
	private CalendarAdapter mDaysAdapter;

	/**
	 * context.
	 */
	private Context mContext;
	
	/*
	 * Interface para indicar que un día del mes ha sido pulsado
	 * y debo lanzar la Actividad que gestiona los ingresos y gastos
	 * Me creo su Observador
	 */
	public ObservadorGestionDatos observadorgt;
	
	public interface ObservadorGestionDatos{
		
		/*
		 * Calendar tendrá el mes y anyo
		 * dia contendrá el día pulsado
		 */
		void lanzarActividadGestionDatos(Calendar c, int dia);
		
	}

	/*
	 * Constructor de la clase con un solo parámetro
	 */
	public CalendarMonthView(final Context context) {
		this(context, null);
	}

	/*
	 * Constructor de la clase
	 */
	public CalendarMonthView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		
		/*
		 * LayoutInflate instancia un archivo XML dentro del objeto View correspondiente
		 * En este caso lo instanciará en el LinearLayout de la clase CalendarMonthView
		 */
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		/*
		 * R.layout.calendar es un LinearLayout con los siguientes componentes:
		 * Un RelativeLayout con un TextView que tendrá el año y mes
		 * 2 GridViews:
		 * 		1) uno donde estan las etiquetas de los dias de la semana lun, mar, mier, jue ...
		 * 		2) otro el cual está compuesto por 
		 */
		inflater.inflate(R.layout.calendar, this, true);
		
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mInitialMonth = Calendar.getInstance();

		// le paso por parámetro al adpatador el contexto y el mes actual o donde esté el calendario activo
		mDaysAdapter = new CalendarAdapter(mContext, mInitialMonth);

		// instancio el grid del calendario
		GridView gridview = (GridView) findViewById(R.id.calendar_days_gridview);
		// le paso el adaptador
		gridview.setAdapter(mDaysAdapter);
		
		// instancio las etiquetas donde se mostrarán los días de la semana
		initDayCaptions(mContext);
		
		initMonthCaption();
		
		// evento Onclick del Calendario
		gridview.setOnItemClickListener(new OnItemClickListener() {
			// en este método recupero el View (celda del calendario pulsada)
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				if (v.getTag() != null) {
					
					// si el dia pulsado pertenece al mes ACTIVO, entro en el SI y lanzo la actividad para insertar ingresos o gastos
					if (mDaysAdapter.isDayOfMonth(position, (DayCell)v.getTag() )){
						/*
						 * Con este observador indico a las clases padres que se lance la Actividad para gestionar
						 * ingresos o gastos ya que una casilla del mes ha sido pulsada 
						 */
						observadorgt.lanzarActividadGestionDatos(
								getMonth(),
								Integer.valueOf(((DayCell)v.getTag()).getDescr()).intValue()
								);
					}
				}
			}
		});
	}

	/**
	 * set current.
	 * 
	 * @param month
	 *            month.
	 */
	public final void setCurrentDay(Calendar currentDay) {
		// mInitialMonth = month;
		mDaysAdapter.setCurrentDay(currentDay);
		refreshCalendar();
	}

	/**
	 * set current.
	 * 
	 * @param month
	 *            Actualiza el Mes Activo en el adaptador del calendario CalendarAdapter
	 */
	public final void setMonth(Calendar month) {
		mInitialMonth = month;
		mDaysAdapter.setMonth(month);
		refreshCalendar();
	}

	/**
	 * @return current month.
	 */
	public final Calendar getMonth() {
		return mInitialMonth;
	}

	/*
	 * Inicio la etiqueta del título del calendario con el mes y año
	 */
	private void initMonthCaption() {

		TextView title = (TextView) findViewById(R.id.titleCalendar);
		String month;
	
		// Elijo los mese es español
		//if (LocaleHelper.isSpanishLocale(mContext)) {
		//	String[] months = mContext.getResources().getStringArray(R.array.months_long);
		//	month = months[mInitialMonth.get(Calendar.MONTH)];
		//} else {
			month = android.text.format.DateFormat.format("MMMM", mInitialMonth).toString();
		//}
		
		if (month.length() > 1) {
			// make first letter in upper case
			month = month.substring(0, 1).toUpperCase(Locale.getDefault()) + month.substring(1);
		}
		
		title.setText(String.format("%s %s", month,	android.text.format.DateFormat.format("yyyy", mInitialMonth)));
		
	}

	/*
	 * @param context
	 * Método en el que la parte de arriba del calendario la carga con las etiquetas de los días de la semana
	 * Mon, Tue, Wed ....
	 */
	private void initDayCaptions(final Context context) {
		final int week = 7;
		String[] dayCaptions = new String[week];
		/*
		 * modificado por joseluisnn83
		 */
		
		//  SimpleDateFormat weekDateFormat = new SimpleDateFormat("EE",Locale.US);
		Calendar weekDay = DateHelper.createCurrentBeginDayCalendar();

		if (DateHelper.isMondayFirst()) {
			weekDay.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		} else {
			weekDay.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		}
		/*
		for (int i = 0; i < week; i++) {
			dayCaptions[i] = weekDateFormat.format(weekDay.getTime());
			DateHelper.increment(weekDay);
		}
		 */
		dayCaptions[0] = "LUN";
		DateHelper.increment(weekDay);
		dayCaptions[1] = "MAR";
		DateHelper.increment(weekDay);
		dayCaptions[2] = "MIE";
		DateHelper.increment(weekDay);
		dayCaptions[3] = "JUE";
		DateHelper.increment(weekDay);
		dayCaptions[4] = "VIE";
		DateHelper.increment(weekDay);
		dayCaptions[5] = "SAB";
		DateHelper.increment(weekDay);
		dayCaptions[6] = "DOM";
		DateHelper.increment(weekDay);
		
		// Obtengo por el ID el gridview donde van las etiquetas de los días de la semana
		GridView captionsGridView = (GridView) findViewById(R.id.calendar_captions_gridview);
		
		/* 
		 * instancio el adaptador del grid de etiquetas donde le paso por parámetro los elementos
		 * que se van a utilizar en el ArrayList: tanto los layouts de los elementos como los string
		 * 
		 */
		captionsGridView.setAdapter(new ArrayAdapter<String>(
				context, 
				R.layout.calendar_caption_item, 
				R.id.calendar_caption_date,
				dayCaptions)
				);
		
	}

	/*
	 * refresh view: actualizar la Vista del Calendario
	 */
	public final void refreshCalendar() {
		mDaysAdapter.refreshDays();
		mDaysAdapter.notifyDataSetChanged();
		initMonthCaption();
	}
	
	/*
	 * Set del observador
	 */
	public void setObservadorGestionDatos (ObservadorGestionDatos ogd){
		this.observadorgt = ogd;
	}
	
}
