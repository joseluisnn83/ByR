package com.joseluisnn.interfaces;

import java.util.ArrayList;
import com.joseluisnn.objetos.ValoresElementoListaGD;

public interface AlmacenarDatos {

	/**
	 * Método donde guardaré o exportaré en algún soporte físico, ya sea bbdd o fichero
	 * los valores pasados por parámetro
	 * @param listadoValoresIngresos
	 * @param listadoValoresGastos
	 * @param totalIngresos
	 * @param totalGastos
	 */
	public void guardarDatos(
			ArrayList<ValoresElementoListaGD> listadoValoresIngresos,
			ArrayList<ValoresElementoListaGD> listadoValoresGastos,
			double totalIngresos, double totalGastos);

}
