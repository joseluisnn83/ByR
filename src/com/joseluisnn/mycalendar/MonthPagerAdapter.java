package com.joseluisnn.mycalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.joseluisnn.byr.R;
import com.joseluisnn.mycalendar.CalendarMonthView.ObservadorGestionDatos;
/*
 * Clase que hereda de PagerAdapter mediante la cual podré tener una lista que se deliza horizontalmente para
 * mostrar los meses del año con sus respectivos días
 */
class MonthPagerAdapter extends PagerAdapter {

	/*
	 * número máximo de pantallas. 998 cause (998 mod 3) == 1
	 */
	public static final int INFINITE = 998;
	/*
	 * Lista que va a contener tres objetos CalendarMonthView 
	 */
	private List<CalendarMonthView> pages = null;

	/*
	 * Variable ViewPager
	 */
	private ViewPager mPager;
	
	/*
	 * Me creo el observador para cuando se actualice la fecha en la vista del Calendario
	 * o para lanzar La Actividad que gestiona los datos
	 */
	public ObservadorMPA observador;
	
	public interface ObservadorMPA{
		
		void cambioFecha(Calendar c);
		void lanzarActividadGestionDatos(Calendar c, int dia);
		
	}

	/*
	 * @param inflater que me lo pasa CalendarioView 
	 * @param pager que me lo pasa CalendarioView
	 */
	public MonthPagerAdapter(LayoutInflater inflater, ViewPager pager) {
		
		this.pages = new ArrayList<CalendarMonthView>();
		this.mPager = pager;
		
		Calendar cal1 = DateHelper.createCurrentBeginDayCalendar();
		Calendar cal2 = DateHelper.createCurrentBeginDayCalendar();
		Calendar cal3 = DateHelper.createCurrentBeginDayCalendar();

		CalendarMonthView view1 = (CalendarMonthView) getView(inflater);
		CalendarMonthView view2 = (CalendarMonthView) getView(inflater);
		CalendarMonthView view3 = (CalendarMonthView) getView(inflater);

		cal1.add(Calendar.MONTH, -1);
		cal2.add(Calendar.MONTH, 0);
		cal3.add(Calendar.MONTH, 1);
		
		view1.setMonth(cal1);
		view2.setMonth(cal2);
		view3.setMonth(cal3);

		this.pages.add(view1);
		this.pages.add(view2);
		this.pages.add(view3);
		
		/*
		 * este observador se ejecuta cuando se ha pulsado un dia en la vista del calendario de la izquierda
		 * (movimiento de derecha del dedo)
		 */
		this.pages.get(0).setObservadorGestionDatos(new ObservadorGestionDatos() {
			
			@Override
			public void lanzarActividadGestionDatos(Calendar c, int dia) {
				// TODO Le indico a la clase padre que transmita el
				// lanzamiento de la Actividad a la siguiente clase padre
				observador.lanzarActividadGestionDatos(c, dia);
				
			}
		});
		
		/*
		 * este observador se ejecuta cuando se ha ppulsado un dia en la vista del calendario activa central
		 * Mes Actual en el calendario
		 */
		this.pages.get(1).setObservadorGestionDatos(new ObservadorGestionDatos() {
			
			@Override
			public void lanzarActividadGestionDatos(Calendar c, int dia) {
				// TODO Le indico a la clase padre que transmita el
				// lanzamiento de la Actividad a la siguiente clase padre
				observador.lanzarActividadGestionDatos(c, dia);
				
			}
		});
		
		/*
		 * este observador se ejecuta cuando se ha pulsado un dia en la vista del calendario de la derecha
		 * (movimiento de izquierda del dedo)
		 */
		this.pages.get(2).setObservadorGestionDatos(new ObservadorGestionDatos() {
			
			@Override
			public void lanzarActividadGestionDatos(Calendar c, int dia) {
				// TODO Le indico a la clase padre que transmita el
				// lanzamiento de la Actividad a la siguiente clase padre
				observador.lanzarActividadGestionDatos(c, dia);
				
			}
		});
		
	}

	/*
	 * @param currentDay
	 * 		Método que actualiza el dia actual
	 *             
	 */
	public void setCurrentDay(Calendar currentDay) {
		if (mPager == null) {
			return;
		}
		
		int pagerPosition = mPager.getCurrentItem();
		
		CalendarMonthView view1 = (CalendarMonthView) pages.get((pagerPosition - 1) % pages.size());
		CalendarMonthView view2 = (CalendarMonthView) pages.get(pagerPosition % pages.size());
		CalendarMonthView view3 = (CalendarMonthView) pages.get((pagerPosition + 1) % pages.size());

		view1.setCurrentDay(currentDay);
		view2.setCurrentDay(currentDay);
		view3.setCurrentDay(currentDay);
	}

	/*
	 * @param month
	 *            Método que actualiza el mes activo del Calendario
	 */
	public final void setMonth(Calendar month) {
		if (mPager == null) {
			return;
		}
		Calendar cal1 = (Calendar) month.clone();
		Calendar cal2 = (Calendar) month.clone();
		Calendar cal3 = (Calendar) month.clone();
		
		int pagerPosition = mPager.getCurrentItem();
		
		CalendarMonthView view1 = (CalendarMonthView) pages.get((pagerPosition - 1) % pages.size());
		CalendarMonthView view2 = (CalendarMonthView) pages.get(pagerPosition % pages.size());
		CalendarMonthView view3 = (CalendarMonthView) pages.get((pagerPosition + 1) % pages.size());

		cal1.add(Calendar.MONTH, -1);
		cal2.add(Calendar.MONTH, 0);
		cal3.add(Calendar.MONTH, 1);
		
		view1.setMonth(cal1);
		view2.setMonth(cal2);
		view3.setMonth(cal3);
		
	}

	/*
	 * Método que devuelve el mes que está activo en el ViewPager
	 */
	public final Calendar getMonth() {
		
		int pagerPosition = mPager.getCurrentItem();
		
		CalendarMonthView view2 = (CalendarMonthView) pages.get(pagerPosition % pages.size());
		
		return view2.getMonth();
	}
	
	/*
	 * @param inflater pasado por parametro por CalendarioView 
	 *            
	 * @return view. Devuelve un objeto de tipo CalendarMonthView
	 */
	private CalendarMonthView getView(LayoutInflater inflater) {
		View p = inflater.inflate(R.layout.calendar_month, null, false);
		return (CalendarMonthView) p;
	}

	/*
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.View, int)
	 * Método que te instancia 
	 */
	@Override
	public final Object instantiateItem(View collection, int position) {
		
		// cojo el ViewPager que me pasan por parámetro
		mPager = (ViewPager) collection;
		// posición actual del ViewPager
		int pagerPosition = mPager.getCurrentItem();
		// Coge uno de los tres CalendarMonthView creados en la Lista pages 
		CalendarMonthView calView = pages.get(position % pages.size());
		
		if (position == pagerPosition) {
			// first init. (occure then pager just created)
			Calendar cal = calView.getMonth();
			Calendar calBefore = (Calendar) cal.clone();
			Calendar calAfter = (Calendar) cal.clone();
			calBefore.add(Calendar.MONTH, -1);
			calAfter.add(Calendar.MONTH, 1);

			CalendarMonthView calViewBefore = pages.get((position - 1) % pages.size());
			CalendarMonthView calViewAfter = pages.get((position + 1) % pages.size());

			calViewAfter.setMonth(calAfter);
			calViewBefore.setMonth(calBefore);
			
			
		}
		
		if (mPager.getChildCount() == pages.size()) {
			
			Calendar cal = pages.get(pagerPosition % pages.size()).getMonth();
			
			if (position > pagerPosition) {
				// slide right
				Calendar calAfter = (Calendar) cal.clone();
				calAfter.add(Calendar.MONTH, 1);
				CalendarMonthView calViewAfter = pages.get((position) % pages.size());
				calViewAfter.setMonth(calAfter);
				
			} else if (position < pagerPosition) {
				// slide left
				Calendar calBefore = (Calendar) cal.clone();
				calBefore.add(Calendar.MONTH, -1);
				CalendarMonthView calViewBefore = pages.get((position) % pages.size());
				calViewBefore.setMonth(calBefore);
				
			}
			// le indico que el calendario ha cambiado de mes y me lo cambie en los combo de anyo y fecha
			observador.cambioFecha(cal);
		}

		if (mPager.getChildCount() < pages.size()) {
			// Si la Vista no ha sido creada se agrega
			mPager.addView(calView, 0);
		}
		
		return calView;
	}
	
	/*
	 * Método para que se visualice la interfaz en otra clase
	 */
	public void setObservadorMPA(ObservadorMPA ompa){
		this.observador = ompa;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
	}

	@Override
	public final int getCount() {
		return INFINITE;
	}

	@Override
	public final boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public final Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
}
