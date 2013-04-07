package com.joseluisnn.byr;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.joseluisnn.mycalendar.CalendarioView;
import com.joseluisnn.mycalendar.CalendarioView.ObservadorCalendarioView;

public class DatasActivityScreen extends Activity{

	private RelativeLayout rlcalendario;
	// Clase para crear el Calendario
	private CalendarioView cv;
	private Spinner spinnerMes;
	private Spinner spinnerAnyo;
	private ImageView ivSearchDate;
	private boolean primeraVezSpinnerMes;
	private boolean primeraVezSpinnerAnyo;
	// Variables referentes al mes y año elegidos por el usuario en los combobox
	private int mes;
	private int anyo;
	private ArrayList<Integer> listAnyos;
	private Calendar calendarToday = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datas_screen);

		primeraVezSpinnerMes = true;
		primeraVezSpinnerAnyo = true;
		
		rlcalendario = (RelativeLayout)findViewById(R.id.RLCalendario);

		cv = new CalendarioView(this,null);
		rlcalendario.addView(cv);
		
		spinnerMes = (Spinner)findViewById(R.id.spinnerMes);
		spinnerAnyo = (Spinner)findViewById(R.id.spinnerAnyo);
		ivSearchDate = (ImageView)findViewById(R.id.ivActivityDatasScreenSearchDate);
		
		// Me creo el adaptador para el Spinner de los meses
		//android.R.layout.simple_spinner_item
		ArrayAdapter<CharSequence> adapterMeses = ArrayAdapter.createFromResource(this, 
											R.array.mostrarmeses,
											R.layout.own_spinner
											);	
		adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerMes.setAdapter(adapterMeses);
		
		// Me creo el adaptador para el Spinner de los años 2005 hasta el 2050
		listAnyos = new ArrayList<Integer>();
		for (int i=2005; i<2051; i++){
			listAnyos.add(Integer.valueOf(i));
		}
		ArrayAdapter<Integer> adapterAnyos = new ArrayAdapter<Integer>(this, R.layout.own_spinner, listAnyos);
		adapterAnyos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerAnyo.setAdapter(adapterAnyos);
				
		/*
		 * Obtengo el mes y año actual y actualizo los Spinner
		 */
		mes = calendarToday.get(Calendar.MONTH);
		spinnerMes.setSelection(mes);
		anyo = calendarToday.get(Calendar.YEAR);
		spinnerAnyo.setSelection(buscarPosicionPorAnyo(anyo));
		
		spinnerMes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int posicion, long arg3) {
				// TODO Mes seleccionado del Spinner
				
				if(primeraVezSpinnerMes){
					
					primeraVezSpinnerMes = false;
										
				}else{
					
					mes = posicion;
					//lanzarSMSMes(mes);
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		spinnerAnyo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int posicion, long arg3) {
				// TODO Mes seleccionado del Sqpinner
				
				if(primeraVezSpinnerAnyo){
					
					primeraVezSpinnerAnyo = false;
										
				}else{
					
					anyo = listAnyos.get(posicion).intValue();
					//lanzarSMSAnyo(anyo);
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		ivSearchDate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Evento donde se pulsa el botón para buscar una fecha en concreto
				cv.busquedaFechaCalendarioView(mes, anyo);
				
			}
		});
		
		/* 
		 * Observador que implemento de la clase CalendarioView para ver el 
		 * cambio de fecha en el Calendario y que me actualiza los Spinner
		 */
		cv.setObservadorCalendarioView(new ObservadorCalendarioView() {
			
			@Override
			public void cambioFecha(Calendar c) {
				/*	TODO 
				 *  Recibo el Evento de la clase CalendarioView cuando se cambia de fecha
				 *  en la Vista del calendario
				 */
				if (c.get(Calendar.YEAR) >= 2005 && c.get(Calendar.YEAR) <= 2050){
				
					if( (spinnerMes.getSelectedItemPosition() == 11 &&  c.get(Calendar.MONTH) == 0) 
						|| (spinnerMes.getSelectedItemPosition() == 0 &&  c.get(Calendar.MONTH) == 11)){
						
						spinnerAnyo.setSelection(buscarPosicionPorAnyo(c.get(Calendar.YEAR)));
					}
								
					spinnerMes.setSelection(c.get(Calendar.MONTH));
				}
				
			}

			@Override
			public void lanzarActividadGestionDatos(Calendar c, int dia) {
				/* TODO 
				 * Método que me va a lanzar la Actividad para gestionar los
				 * ingresos y gastos de un día en concreto
				 */
					
				lanzarActividadGD(c,dia);
				
				
			}
		});
		
	}
	
	public void lanzarSMSMes(int i){
		Toast.makeText(this, "Mes pulsado: " + i , Toast.LENGTH_SHORT).show();
	}
	
	public void lanzarSMSAnyo(int i){
		Toast.makeText(this, "Anyo pulsado: " + i , Toast.LENGTH_SHORT).show();
	}
	
	public void lanzarActividadGD(Calendar c, int dia){
		
		String cadena_fecha = new String();
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
		day = "" + dia;
		if(day.length() == 1){
			day = "0" + day;
		}
		
		// Pongo la fecha de Calendar pasada por parámetro al día pasado también por parámetro
		c.set(Calendar.DATE, dia);
		
		cadena_fecha = "" + obtenerDiaSemana(c.get(Calendar.DAY_OF_WEEK)) + ", " 
				+ dia + " de "
				+ obtenerMes(c.get(Calendar.MONTH)) + " de " 
				+ c.get(Calendar.YEAR);
		
		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) +  month + day;
		// Lo paso a entero
		entero_fecha = Integer.valueOf(fecha);
		
		// Me creo el bundle para pasarle los parámetros a la Acitvidad DatasActivity
		Bundle bundle = new Bundle();		
		bundle.putString("cadena_fecha", cadena_fecha);
		bundle.putInt("entero_fecha", entero_fecha.intValue());
		
		Intent intent = new Intent(this,DatasActivity.class);
		intent.putExtras(bundle);
				
		/*
		 *  Lanzo la Activity de manera que me devuelva valores; para saber que proviene de la Actividad corresta
		 *  el @requestcode se lo he puesto a 1 aunque no haría falta ya que sólo vendrán valores de la Actividad
		 *  DatasActivity
		 */		
		startActivityForResult(intent, 1);
		
	}
	
	/*
	 * Método por el cual voy a saber la posición de un año en el Spinner a través de un año pasado por paŕametro
	 */
	public int buscarPosicionPorAnyo(int a){
		
		boolean encontrado = false;		
		int pos = -1;
				
		do{
			pos ++;
			if(listAnyos.get(pos).intValue() == a){
				encontrado = true;
			}
			
		}while (!encontrado);
		
		return pos;
	}
	
	/*
	 * Método que me devuelve el mes según el entero pasado por parámetro
	 */
	private String obtenerMes(int month){
		
		String m = new String();
		
		switch(month){
		
		case 0:
			m = "Ene.";
			break;
		case 1:
			m = "Feb.";
			break;
		case 2:
			m = "Mar.";
			break;
		case 3:
			m = "Abr.";
			break;
		case 4:
			m = "May.";
			break;
		case 5:
			m = "Jun.";
			break;
		case 6:
			m = "Jul.";
			break;
		case 7:
			m = "Ago.";
			break;
		case 8:
			m = "Sep.";
			break;
		case 9:
			m = "Oct.";
			break;
		case 10:
			m = "Nov.";
			break;
		case 11:
			m = "Dic.";
			break;
		default:
			m= "error";
			break;
		}
		
		return m;
		
	}
	
	/*
	 * Método que me devuelve el día de la semana según el entero pasado por parámetro
	 */
	private String obtenerDiaSemana(int day){
		
		String d = new String();		
		
		switch (day) {
		case 1:
			d = "Dom";
			break;
		case 2:
			d = "Lun";
			break;
		case 3:
			d = "Mar";
			break;
		case 4:
			d = "Mié";
			break;
		case 5:
			d = "Jue";
			break;
		case 6:
			d = "Vie";
			break;
		case 7:
			d = "Sáb";
			break;
		default:
			break;
		}
		
		return d;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 *  TODO 
		 *  Método que recibe parámetros de la Actividad DatasActivity que acaba de ser 
		 *  destruida (ha finalizado) y para colorear las casillas modificadas del 
		 *  calendario vuelvo a realizar la busqueda de la última fecha que se modificó
		 *  o que se situó el usuario (haya realizado cambios o no)
		 */
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
			
			int month = data.getExtras().getInt("mes");
			int year = data.getExtras().getInt("anyo");
			
			cv.busquedaFechaCalendarioView(month, year);
			
		}
		
	}
	
}
