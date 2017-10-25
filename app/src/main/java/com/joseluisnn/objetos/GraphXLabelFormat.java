package com.joseluisnn.objetos;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;

/*
 * Formato especial para los meses en el eje de las X en la gráfica
 */
public class GraphXLabelFormat extends Format {

	/**
	 * Formato Creado por joseluisnn
	 */
	private static final long serialVersionUID = 1312555777325976345L;
	
	private ArrayList<String> label;

	public GraphXLabelFormat(ArrayList<String> l) {
		// TODO Constructor de la clase
		label = l;
	}
	
	
	@Override
	public StringBuffer format(Object object, StringBuffer buffer,
			FieldPosition field) {
		//int parsedInt = Math.round(Float.parseFloat(object.toString()));
		//String labelString = LABELS[parsedInt];

		//buffer.append(labelString);
		//return buffer;
		
		return new StringBuffer(label.get(((Number)object).intValue()).toString());
		
	}

	@Override
	public Object parseObject(String string, ParsePosition position) {
		//return java.util.Arrays.asList(LABELS).indexOf(string);
		return null;
	}

}
