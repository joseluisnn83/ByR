package com.joseluisnn.tt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.joseluisnn.interfaces.AlmacenarDatos;
import com.joseluisnn.objetos.ValoresElementoListaGD;

public class AlmacenarDatosEnFichero implements AlmacenarDatos{

	private static String FICHERO = "informe.txt";
	private Context contexto;
	
	public AlmacenarDatosEnFichero(Context c){
		this.contexto = c;
	}
	
	@Override
	public void guardarDatos(
			ArrayList<ValoresElementoListaGD> listadoValoresIngresos,
			ArrayList<ValoresElementoListaGD> listadoValoresGastos,
			double totalIngresos, double totalGastos) {
		// TODO método donde exporto los datos a un fichero .txt
		try {
			FileOutputStream f = this.contexto.openFileOutput(FICHERO, Context.MODE_APPEND);
			
			String concepto = "";
			String linea = "                     Informes TANTOTENGO                    ";			
			f.write(linea.getBytes());
			linea = "                                                            ";
			f.write(linea.getBytes());
			linea = "Ingresos                                                    ";
			f.write(linea.getBytes());
			
			// Itero la lista de ingresos
			for(int i=0; i<listadoValoresIngresos.size(); i++){
				
				concepto = listadoValoresIngresos.get(i).getConcepto() + "                    ";
				concepto = concepto.substring(0, 14) + "     ";
				linea = concepto + listadoValoresIngresos.get(i).getCantidad() + " €";
			}
			
			linea = "                                                            ";
			f.write(linea.getBytes());
			linea = "============================================================";
			f.write(linea.getBytes());
			linea = "                                                            ";
			f.write(linea.getBytes());
			linea = "Gastos                                                      ";
			f.write(linea.getBytes());
			linea = "                                                            ";
			f.write(linea.getBytes());
			
			// Itero la lista de gastos
			for(int i=0; i<listadoValoresGastos.size(); i++){
				concepto = listadoValoresGastos.get(i).getConcepto() + "                    ";
				concepto = concepto.substring(0, 14) + "     ";
				linea = concepto + listadoValoresGastos.get(i).getCantidad() + " €";
			}
			
			f.close();
			
		} catch (FileNotFoundException e) {
			// TODO Si hay algún error me lo muestra
			Log.e("TantoTengo", e.getMessage(), e);
		}catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.e("TantoTengo", e.getMessage(), e);
		}
		
		
	}

	

}
