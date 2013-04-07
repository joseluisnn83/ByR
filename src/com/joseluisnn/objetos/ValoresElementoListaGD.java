package com.joseluisnn.objetos;

/*
 * Clase para guardar el concepto y su cantidad en euros respectivos
 */
public class ValoresElementoListaGD {

	private int id_concepto;
	private int id_fecha;
	private String concepto;
	private double cantidad;
	
	public ValoresElementoListaGD(int concepto, int fecha, String c, double d){
		this.id_concepto = concepto;
		this.id_fecha = fecha;
		this.concepto = c;
		this.cantidad = d;
	}
	
	public int getIdConcepto(){
		return this.id_concepto;
	}
	
	public void setIdConcepto(int id){
		this.id_concepto = id;
	}
	
	public int getIdFecha(){
		return this.id_fecha;
	}
	
	public void setIdFecha(int id){
		this.id_fecha = id;
	}
	
	public String getConcepto(){
		return this.concepto;
	}
	
	public void setConcepto(String s){
		this.concepto = s;
	}
	
	public double getCantidad(){
		return this.cantidad;
	}
	
	public void setCantidad(double d){
		this.cantidad = d;
	}
	
}
