package com.joseluisnn.mycalendar;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joseluisnn.byr.R;
import com.joseluisnn.databases.DBAdapter;

/**
 * calendar adapter.
 *
 *	Adaptador para las celdas del Calendario 
 */
public class CalendarAdapter extends BaseAdapter {

	/*
	 * references to our items.
	 * 
	 * Referencia a los días del calendario
	 */
	private DayCell[] days;

	/**
	 * context.
	 */
	private Context mContext;

	/*
	 * init month.
	 * Mes actual
	 */
	private java.util.Calendar mCurrentMonth;
	/*
	 * today.
	 * Dia de hoy 
	 */
	private Calendar mToday;

	/*
	 * day that was picked.
	 * Dia que fue clickado
	 */
	private Calendar mCurrentDay;
	
	/*
	 *  Variable para la BASE DE DATOS
	 */
	private DBAdapter dba;
	
	/*
	 * Lista para tener los días del calendario donde hay valores y poder colorear las casillas 
	 */
	private ArrayList<Integer> listaFechasConValores;
	
	/**
	 * @param context
	 *            context.
	 * @param monthCalendar
	 *            current month.
	 */
	protected CalendarAdapter(Context context, Calendar monthCalendar) {
		mToday = DateHelper.createCurrentBeginDayCalendar();
		mCurrentMonth = (Calendar) monthCalendar.clone();
		mContext = context;
		mCurrentMonth.set(Calendar.DAY_OF_MONTH, 1);
		
		// Instancio la Base de Datos
		dba = DBAdapter.getInstance(context);
		
		refreshDays();
	}

	/**
	 * @param currentDay
	 *            day that was picked.
	 */
	public void setCurrentDay(Calendar currentDay) {
		mCurrentDay = currentDay;
	}

	/*
	 * @param month
	 *            current month: actualiza el mes ACTIVO que se verá en pantalla
	 */
	public final void setMonth(Calendar month) {		
		mCurrentMonth = (Calendar) month.clone();
		
		int entero_fecha = formateaFechaAAAAMM(month);
		
		/*
		 * Recupero los valores de la Base de Datos para la fecha pasada por parámetro
		 */
		dba.openREAD();
		
		this.listaFechasConValores = dba.obtenerDiasConValores(entero_fecha);
		
		dba.close();
		
	}

	public int getCount() {
		return days.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	/*
	 * Método que me va a decir si el día clickado pertenece al mes que está ACTIVO en PANTALLA
	 */
	public boolean isDayOfMonth(int position, DayCell diaPulsado){
		
		// Booleano para saber si un dia pertenece al mes que está activo
		boolean isCurrentMonth = diaPulsado.mDate.get(Calendar.MONTH) == mCurrentMonth.get(Calendar.MONTH);
		
		return isCurrentMonth;
	}
	
	/*
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 * 
	 * Obtengo el View que hace referencia a la casilla de un día del mes
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View currentView = convertView;
		TextView dayViewTextView;
		ImageView ivInsertedData;
		DayCell dayCell = days[position];
		int fechaConValores = formateaFechaAAAAMMDD(dayCell.mDate);
		
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			currentView = vi.inflate(R.layout.calendar_item, null);
		}
		
		/* TextView para los días del mes*/			
		dayViewTextView = (TextView) currentView.findViewById(R.id.data);
		ivInsertedData = (ImageView) currentView.findViewById(R.id.imageViewInsertedData);
		
		// Booleano para saber si un dia pertenece al mes que está activo
		boolean isCurrentMonth = dayCell.mDate.get(Calendar.MONTH) == mCurrentMonth.get(Calendar.MONTH);
		
		// Si no es un día del mes actual entra por aquí
		if (!isCurrentMonth) {
			
			dayViewTextView.setTextColor(mContext.getResources().getColor(R.color.gris_claro));
			ivInsertedData.setVisibility(ImageView.INVISIBLE);
			
		} else {// Si es un día del mes actual pasa por el else
			
			dayViewTextView.setTextColor(mContext.getResources().getColor(R.color.CalendarioFondoPrincipal));
						
			// Lo días del mes actual que caigan en domingo los pinto de rojo
			if (dayCell.mDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				
				dayViewTextView.setTextColor(mContext.getResources().getColor(R.color.CalendarioDomingos));
				
			}
			
			/*
			 * Bloque if-else para indicar que casillas contiene valores
			 */
			if (listaFechasConValores.contains(Integer.valueOf(fechaConValores))){
				// Indico que en la casilla hay algún valor insertado y pongo a visible el icono
				ivInsertedData.setVisibility(ImageView.VISIBLE);
			}else{
				ivInsertedData.setVisibility(ImageView.INVISIBLE);
			}
			
		}

		// Escogiendo el FONDO (background) de las casillas
		if (mToday.equals(dayCell.mDate)) {
			currentView.setBackgroundResource(R.drawable.calendar_item_background_today);
		} else if (mCurrentDay != null
				&& DateHelper.equalsIgnoreTime(mCurrentDay.getTime(),
						dayCell.mDate.getTime())) {
			currentView.setBackgroundResource(R.drawable.calendar_item_background_current);
		} else {
			if (isCurrentMonth) {
				// fondo de la casilla para los días que sí son del mes actual
				currentView.setBackgroundResource(R.drawable.list_item_states);
			} else {
				// fondo de la casilla para los días que no son del mes actual
				currentView.setBackgroundResource(R.drawable.calendar_notcurrentmonth_item_states);
			}
		}
		// Inserto en el TextView de la casilla del día del mes el día (dayCell.getDescr())
		dayViewTextView.setText(dayCell.getDescr());

		// create date string for comparison
		String date = dayCell.getDescr();

		if (date.length() == 1) {
			date = "0" + date;
		}

		String monthStr = "" + (mCurrentMonth.get(Calendar.MONTH) + 1);
		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}
		
		currentView.setTag(dayCell);
		
		return currentView;
	}

	/**
	 * refresh. 
	 * Actualiza los Datos del Calendario
	 */
	public final void refreshDays() {
		
		Calendar currentDate = DateHelper.fromDateToCalendar(DateHelper.clearTime(((Calendar) mCurrentMonth.clone()).getTime()));
		
		currentDate.set(Calendar.DAY_OF_MONTH, 1);

		int firstDay = currentDate.get(Calendar.DAY_OF_WEEK);
		int dayShift;
		
		if (DateHelper.isMondayFirst()) {
			if (firstDay == Calendar.SUNDAY) {
				dayShift = 6;
			} else {
				dayShift = firstDay - 2;
			}
		} else {
			dayShift = firstDay - 1;
		}
		
		currentDate.add(Calendar.DATE, -dayShift);
		days = new DayCell[42];
		// relleno los 42 dias que tendrá la vista del mes
		for (int i = 0; i < days.length; i++) {
			days[i] = new DayCell(currentDate, "" + currentDate.get(Calendar.DAY_OF_MONTH));
			DateHelper.increment(currentDate);
		}
	}

	/*
	 * Clase interna para los días de la celda del GridView
	 */
	public class DayCell {
		/*
		 * descr ==> Esto se refiere al día de la celda
		 */
		private String mDescription;
		/**
		 * date.
		 */
		private Calendar mDate;

		/**
		 * @param currentDate
		 *            date.
		 * @param text
		 *            descr.
		 */
		public DayCell(Calendar currentDate, String text) {
			mDescription = text;
			mDate = (Calendar) currentDate.clone();
		}

		/*
		 * @return descr ==> devuelve en un String el día de la celda
		 * 			
		 */
		public final String getDescr() {
			return mDescription;
		}

		/**
		 * @return date.
		 */
		public final Calendar getDate() {
			return mDate;
		}

		@Override
		public final int hashCode() {
			return super.hashCode();
		}

		@Override
		public final boolean equals(Object other) {
			// Not strictly necessary, but often a good optimization
			if (this == other) {
				return true;
			}
			if (!(other instanceof DayCell)) {
				return false;
			}
			DayCell otherA = (DayCell) other;
			return (mDate.equals(otherA.mDate))
					&& ((mDate == null) ? otherA.mDate == null : mDate
							.equals(otherA.mDate));
		}
	}
	
	/*
	 * Método que de una variable Calendar me formatea la fecha a AAAAMM
	 * y siendo un entero
	 */
	private int formateaFechaAAAAMM(Calendar month){
		
		String fecha = new String();
		String mes;		
		Integer entero_fecha;
		
		// Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		// Si el mes solo tiene un dígito le pongo un cero delante
		mes = "" + (month.get(Calendar.MONTH)+1);
		if (mes.length() == 1){
			mes = "0" + mes;
		}
		
		// Obtengo la fecha en el formato AAAAMM
		fecha = "" + month.get(Calendar.YEAR) +  mes;
		// Lo paso a entero
		entero_fecha = Integer.valueOf(fecha);
		
		return entero_fecha;
	}
	
	/*
	 * Método que de una variable Calendar me formatea la fecha a AAAAMMDD
	 * y siendo un entero
	 */
	private int formateaFechaAAAAMMDD(Calendar c){
		
		String fecha = new String();
		String month;
		String day;
		Integer entero_fecha;
		
		// Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		// Si el mes solo tiene un dígito le pongo un cero delante
		month = "" + (c.get(Calendar.MONTH)+1);
		if (month.length() == 1){
			month = "0" + month;
		}
		
		
		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + (c.get(Calendar.DAY_OF_MONTH));
		if(day.length() == 1){
			day = "0" + day;
		}
		
		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) +  month + day;
		// Lo paso a entero
		entero_fecha = Integer.valueOf(fecha);
		
		return entero_fecha;
	}
	
}
