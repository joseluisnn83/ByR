package com.joseluisnn.objetos;

public class Concepto {
	
	private int id;
	private String nombre;
	private String tipo;
	
	public Concepto(int id, String nombre, String tipo){
		this.id = id;
		this.nombre = nombre;
		this.tipo = tipo;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setNombre(String n){
		this.nombre = n;
	}
	
	public String getNombre(){
		return this.nombre;
	}
	
	public void setTipo(String t){
		this.tipo = t;
	}
	
	public String getTipo(){
		return this.tipo;
	}

}
