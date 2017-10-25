package com.joseluisnn.objetos;

public class ValoresElementosGraficas {

	private String fecha;
	private double cantidad;
	
	public ValoresElementosGraficas(String fecha, double d){
		
		this.fecha = fecha;		
		this.cantidad = d;
	}
	
	public String getFecha(){
		return this.fecha;
	}
	
	public void setFecha(String f){
		this.fecha = f;
	}
	
	public double getCantidad(){
		return this.cantidad;
	}
	
	public void setCantidad(double d){
		this.cantidad = d;
	}
	
}
