package com.joseluisnn.tt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joseluisnn.tt.R;
import com.joseluisnn.objetos.ValoresElementoListaGD;

/*
 * Clase Adaptador para la lista con los conceptos y su cantidad en euros respectivas
 */
public class MyAdapterListaGestionDatos extends BaseAdapter{
	
	private final int EDITAR = 0;
	private final int BORRAR = 1;
	private final int PULSACION_LARGA = 2;
	
	private final Context context;
	private final String tipoConcepto;
	private final ArrayList<ValoresElementoListaGD> values;
	
	// Variable para el formato de los números DOUBLE
	private DecimalFormatSymbols separadores;
	private DecimalFormat numeroAFormatear;
	
	public ObservadorMyAdapterListViewGD observador;
	
	public interface ObservadorMyAdapterListViewGD{
		
		void editar(int position);
		void borrar(int position);
		void pulsacionLarga(int position);
		
	}
 	
	public MyAdapterListaGestionDatos(Context c, ArrayList<ValoresElementoListaGD> lista, String tipo){
		super();
		this.context = c;
		this.tipoConcepto = tipo;
		this.values = lista;
		
		// Instancio los formateadores de números
		separadores = new DecimalFormatSymbols();
		separadores.setDecimalSeparator(',');
		separadores.setGroupingSeparator('.');
		numeroAFormatear = new DecimalFormat("###,###.##", separadores);
	}

	@Override
	public int getCount() {
		/* TODO
		 * Devuelve el número de elementos de la Lista de Valores 
		 */
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		/* TODO
		 * Devuelve el elemento en una determinada posición de la lista 
		 */
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		/* TODO
		 * Devuelve el identificador de fila de una determinada posición de la lista 
		 */
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		/* TODO
		 *  Este método va a construir un nuevo objeto View con el Layout correspondiente
		 *  a la posición @position y devolverlo
		 *  @convertView : vista base para generar más rápido las vistas
		 *  @parent : corresponde al padre al que la vista va a ser añadida
		 */
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.elemento_lista_gestion_datos, parent, false);
		
		TextView tvConcepto = (TextView)rowView.findViewById(R.id.tvElementoListaGDConcepto);
		TextView tvCantidad = (TextView)rowView.findViewById(R.id.tvSubElementoListaGDCantidad);
		ImageButton ibEditar = (ImageButton)rowView.findViewById(R.id.buttonElementoListaGDEditar);
		ImageButton ibBorrar = (ImageButton)rowView.findViewById(R.id.buttonElementoListaGDBorrar);
		
		tvConcepto.setText(values.get(position).getConcepto());
		tvCantidad.setText(numeroAFormatear.format(values.get(position).getCantidad())+" €");
		
		tvConcepto.setTextColor(this.context.getResources().getColor(com.joseluisnn.tt.R.color.dim_gray));
		
		// Según si es ingreso o gasto pongo la letra de un color u otro
		if(this.tipoConcepto.equals("ingreso")){
			tvCantidad.setTextColor(this.context.getResources().getColor(com.joseluisnn.tt.R.color.verde));
		}else{
			tvCantidad.setTextColor(this.context.getResources().getColor(com.joseluisnn.tt.R.color.rojo));
		}
		
		/*
		 * Aquí controlo el click a los botones dentro de cada Item del ListView (borrar ó editar)
		 */
		
		ibEditar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO botón editar clickado
				
				lanzarObservador(EDITAR,position);
				
			}
		});
		
		ibBorrar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO botón borrar clickado
				
				lanzarObservador(BORRAR,position);
				
			}
		});
		
		rowView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Informo que se ha pulsado de manera prolongada un item del ListView
				// 		y despliego el menú de previsión si es un día futuro				
				lanzarObservador(PULSACION_LARGA,position);
				
				return false;
			}
		});
		
		return rowView;
	}
	
	public void setObservadorMyAdapterListViewGD(ObservadorMyAdapterListViewGD obs){
		this.observador = obs;
	}
	
	private void lanzarObservador(int accion, int position){
		
		switch (accion) {
		case EDITAR:
			observador.editar(position);
			break;
		case BORRAR:
			observador.borrar(position);
			break;
		case PULSACION_LARGA:
			observador.pulsacionLarga(position);
			break;
		default:
			break;
		}
		
	}

}
