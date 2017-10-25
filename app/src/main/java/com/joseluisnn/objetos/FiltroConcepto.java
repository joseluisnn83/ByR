package com.joseluisnn.objetos;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * 
 * @author joseluisnn83
 * Clase que implementa la interfaz Parcelable y así poder pasar un Arraylist de objetos 
 * FiltroConcepto de una Actividad a otra
 *
 */
public class FiltroConcepto implements Parcelable{
	
	private int idConcepto;
	private String nombre;
	private boolean filtrado;
	
	public FiltroConcepto(int id, String nombre, boolean b){
		super();
		this.setIdConcepto(id);
		this.setNombre(nombre);
		this.setFiltrado(b);
	}

	public int getIdConcepto() {
		return idConcepto;
	}

	public void setIdConcepto(int idConcepto) {
		this.idConcepto = idConcepto;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isFiltrado() {
		return filtrado;
	}

	public void setFiltrado(boolean filtrado) {
		this.filtrado = filtrado;
	}
	
	public FiltroConcepto(Parcel source) {
		
		idConcepto = source.readInt();
		nombre = source.readString();
		filtrado = (source.readInt() == 0) ? false : true;
		// otra maner de escribirlo ==> filtrado = source.readByte() != 0;   //filtrado == true if byte != 0
	 
	}
	
	public 	static 	final Parcelable.Creator<FiltroConcepto> CREATOR =	new Parcelable.Creator<FiltroConcepto>() {
		
		@Override
		public	FiltroConcepto createFromParcel(Parcel in) {
		   
			return new	FiltroConcepto(in);
		  
		}
		 
		  
		@Override
		public FiltroConcepto[] newArray(int size) {
			
			return	new FiltroConcepto[size];
		  
		}
		
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(this.idConcepto);
		dest.writeString(this.nombre);
		// true == 1 : false == 0
		dest.writeInt(this.filtrado ? 1 : 0);
		// otra manera de escribirlo ==> dest.writeByte((byte) (filtrado ? 1 : 0)); //if filtrado == true, byte == 1
		
	}

}
