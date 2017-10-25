package com.joseluisnn.mycalendar;

import java.util.Calendar;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.joseluisnn.mycalendar.MonthPagerAdapter.ObservadorMPA;
import com.joseluisnn.tt.R;

public class CalendarioView extends LinearLayout {

	/*
	 * Variable ViewPager 
	 */
	private ViewPager pager;

	/*
	 * Variable adapter para manejar el contenido del Calendario.
	 */
	private MonthPagerAdapter adapter;
	
	/*
	 * Me creo el observador para cuando se actualice la fecha en la vista del Calendario
	 */
	public ObservadorCalendarioView observador;
	
	public interface ObservadorCalendarioView{
		
		void cambioFecha(Calendar c);
		void lanzarActividadGestionDatos(Calendar c, int dia);
		
	}

	/*
	 * Constructor de la clase
	 */
	public CalendarioView(final Context context) {
		this(context, null);
	}

	/*
	 * Constructor de la clase
	 */
	public CalendarioView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.calendar_view, this, true);
		
		// onFinishInflate.
		pager = ((ViewPager) findViewById(R.id.calendar_view_pager));
		adapter = new MonthPagerAdapter(inflater, pager);
		pager.setAdapter(adapter);
		pager.setCurrentItem(MonthPagerAdapter.INFINITE / 2);
		
		adapter.setObservadorMPA(new ObservadorMPA() {
			
			@Override
			public void cambioFecha(Calendar c) {
				// TODO Fecha modificada en el calendario y parametro traido desde MonthPagerAdapter
				observador.cambioFecha(c);
			}

			@Override
			public void lanzarActividadGestionDatos(Calendar c, int dia) {
				// TODO Auto-generated method stub
				observador.lanzarActividadGestionDatos(c, dia);
				
			}
		});
		
	}
	
	/*
	 * Se actualiza al dia pasado por parámetro.
	 * 
	 * @param currentDay
	 */
	public final void setCurrentDay(Calendar currentDay) {
		adapter.setCurrentDay(currentDay);
	}

	/*
	 * se actualiza al mes pasado por parámetro.
	 * 
	 * @param month
	 *            month.
	 */
	public final void setMonth(Calendar month) {
		adapter.setMonth(month);
	}

	/*
	 * Obtener el mes que tiene el adaptador 
	 * 
	 * @return month month.
	 */
	public final Calendar getMonth() {
		return adapter.getMonth();
	}
	
	/*
	 * Método para que se visualice la interfaz en otra clase
	 */
	public void setObservadorCalendarioView(ObservadorCalendarioView ocf){
		this.observador = ocf;
	}
	
	/*
	 * Método que va a ser llamado desde la clase padre DatasActivity al pulsar el botón de búsqueda de 
	 * fecha en los Spinner
	 */
	public void busquedaFechaCalendarioView(int mes, int anyo){
		
		Calendar fecha = DateHelper.createCalendar(anyo, mes, 1, "UTC");
		
		adapter.setMonth(fecha);
		
	}
	
}
