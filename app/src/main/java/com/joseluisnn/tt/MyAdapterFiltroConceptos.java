package com.joseluisnn.tt;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.joseluisnn.objetos.FiltroConcepto;

public class MyAdapterFiltroConceptos extends BaseAdapter {

	private final Context context;
	private final ArrayList<FiltroConcepto> filtro;

	public ObservadorMyAdapterFiltroConceptos observador;

	public interface ObservadorMyAdapterFiltroConceptos {

		void actualizarListaFiltro(int position, boolean b);

	}

	public MyAdapterFiltroConceptos(Context c, ArrayList<FiltroConcepto> f) {

		this.context = c;
		this.filtro = f;

	}

	@Override
	public int getCount() {
		/*
		 * TODO Devuelve el número de elementos de la Lista de Valores
		 */
		return filtro.size();
	}

	@Override
	public Object getItem(int position) {
		/*
		 * TODO Devuelve el elemento en una determinada posición de la lista
		 */
		return filtro.get(position);
	}

	@Override
	public long getItemId(int position) {
		/*
		 * TODO Devuelve el identificador de fila de una determinada posición de
		 * la lista
		 */
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		/*
		 * TODO Este método va a construir un nuevo objeto View con el Layout
		 * correspondiente a la posición @position y devolverlo
		 * 
		 * @convertView : vista base para generar más rápido las vistas
		 * 
		 * @parent : corresponde al padre al que la vista va a ser añadida
		 */
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.elemento_lista_filtro_conceptos, parent, false);

		TextView tvName = (TextView) rowView
				.findViewById(R.id.tvElementoFiltroConcepto);
		final CheckBox cbFiltrado = (CheckBox) rowView
				.findViewById(R.id.cbElementoFiltroConcepto);

		tvName.setText(this.filtro.get(position).getNombre());

		if (this.filtro.get(position).isFiltrado()) {
			cbFiltrado.setChecked(true);
		} else {
			cbFiltrado.setChecked(false);
		}

		if (position == 0) {
			
			tvName.setText("*");
			
		}
		
		cbFiltrado.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO envío la posición y el estado del check al observador
				if(position == 0 && cbFiltrado.isChecked() == true){
					filtro.get(0).setFiltrado(true);
					for (int i=1;i<getCount();i++){
						observador.actualizarListaFiltro(i, false);
						filtro.get(i).setFiltrado(false);
					}
				}else{
					observador.actualizarListaFiltro(position, cbFiltrado.isChecked());
					filtro.get(0).setFiltrado(false);
				}
				
				// Actualizo la Vista del Adaptador
				notifyDataSetChanged();
				
			}
		});

		return rowView;
	}
	
	public void setObservador (ObservadorMyAdapterFiltroConceptos o){
		
		this.observador = o;
		
	}

}
