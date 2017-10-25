package com.joseluisnn.singleton;

import java.util.Calendar;

import android.content.Context;

import com.joseluisnn.tt.R;

public class SingletonFechas {

	private static SingletonFechas INSTANCE = new SingletonFechas();
	/*
	 * Me creo una serie de constantes para buscar el Trimestre que quiera
	 */
	private static final int TRIMESTRE1_ANYO_ANTERIOR = 0;
	private static final int TRIMESTRE2_ANYO_ANTERIOR = 1;
	private static final int TRIMESTRE3_ANYO_ANTERIOR = 2;
	private static final int TRIMESTRE4_ANYO_ANTERIOR = 3;
	private static final int TRIMESTRE1_ANYO_ACTUAL = 4;
	private static final int TRIMESTRE2_ANYO_ACTUAL = 5;
	private static final int TRIMESTRE3_ANYO_ACTUAL = 6;
	private static final int TRIMESTRE4_ANYO_ACTUAL = 7;
	
	private SingletonFechas (){}
	
	/*
	 * creador sincronizado para protegerse de posibles problemas multi-hilo
	 * otra prueba para evitar instanciación múltiple
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SingletonFechas();
		}
	}
	
	public static SingletonFechas getInstance(){
		createInstance();
		return INSTANCE;
	}
	
	/**
	 * Aquí me defino los métodos que se repiten en varias clases a los cuales
	 * podré acceder, definiendo e instanciando una única vez la clase 
	 * SingletonFechas en varias clases del programa 
	 */
	/*
	 * Método que me devuelve en un entero el primer dia de la semana pasada
	 */
	public int obtenerInicioEnteroFechaSemanaAnterior() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha de la semana anterior en el
		// lunes
		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR, -1);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el primer dia de la semana pasada
	 */
	public int obtenerFinEnteroFechaSemanaAnterior() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha de la semana anterior en el
		// domingo
		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR, -1);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el primer dia de la semana actual
	 */
	public int obtenerInicioEnteroFechaSemanaActual() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha actual en el lunes
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el último dia de la semana actual En
	 * mi caso me interesa obtener el día de hoy para visualizar exclusivamente
	 * los valores pasados y el de hoy, no valores de futuro.
	 */
	public int obtenerFinEnteroFechaSemanaActual() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha actual
		Calendar c = Calendar.getInstance();
		// Aquí me sitúa la fecha actual en el domingo, el último día
		// c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		/*
		 * Si el día solo tiene un dígito le pongo un cero delante Le estoy
		 * sumando 7 para que me de el útimo día de la semana
		 */
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el primer dia del mes anterior
	 */
	public int obtenerInicioEnteroFechaMesAnterior() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha del primer día del mes
		// anterior
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DATE, 1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el último dia del mes anterior
	 */
	public int obtenerFinEnteroFechaMesAnterior() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha del último día del mes
		// anterior
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		c.add(Calendar.DATE, -1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el primer dia del mes actual
	 */
	public int obtenerInicioEnteroFechaMesActual() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha actual en el lunes
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el último dia de la semana actual En
	 * mi caso me interesa que el útimo día sea el de hoy para que no me muestre
	 * valores futuros
	 */
	public int obtenerFinEnteroFechaMesActual() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha del día actual
		Calendar c = Calendar.getInstance();

		// Aquí se obtendría el último dia del mes actual
		// c.add(Calendar.MONTH, 1);
		// c.set(Calendar.DATE, 1);
		// c.add(Calendar.DATE, -1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		/*
		 * Si el día solo tiene un dígito le pongo un cero delante Le estoy
		 * sumando 7 para que me de el útimo día de la semana
		 */
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve la fecha de inicio en un entero correspondiente al
	 * trimestre pedido por parámetro
	 */
	public int obtenerInicioEnteroFechaTrimestre(int trimestre) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		Calendar c = Calendar.getInstance();

		switch (trimestre) {

		case TRIMESTRE1_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 1º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE2_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 2º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 3);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE3_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 6);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE4_ANYO_ANTERIOR:

			// Situo la variable Calendar en el inicio del 4º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE1_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 1º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE2_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 2º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 3);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE3_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 6);
			c.set(Calendar.DATE, 1);

			break;
		case TRIMESTRE4_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// actual
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);

			break;

		default:
			break;
		}

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve la fecha de fin en un entero correspondiente al
	 * trimestre pedido por parámetro
	 */
	public int obtenerFinEnteroFechaTrimestre(int trimestre) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		Calendar c = Calendar.getInstance();

		int mesActual = c.get(Calendar.MONTH);

		switch (trimestre) {

		case TRIMESTRE1_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 1º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 3);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE2_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 2º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 6);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE3_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 3º Trimestre del año
			// anterior
			c.add(Calendar.YEAR, -1);
			c.set(Calendar.MONTH, 0);
			c.add(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE4_ANYO_ANTERIOR:

			// Situo la variable Calendar en el final del 4º Trimestre del año
			// anterior
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.DATE, 1);
			c.add(Calendar.DATE, -1);

			break;
		case TRIMESTRE1_ANYO_ACTUAL:

			if (mesActual > 2) {
				// Situo la variable Calendar en el final del 1º Trimestre del
				// año
				// actual
				c.set(Calendar.MONTH, 0);
				c.add(Calendar.MONTH, 3);
				c.set(Calendar.DATE, 1);
				c.add(Calendar.DATE, -1);
			}
			break;
		case TRIMESTRE2_ANYO_ACTUAL:

			if (mesActual > 5) {
				// Situo la variable Calendar en el inicio del 2º Trimestre del
				// año
				// actual
				c.set(Calendar.MONTH, 0);
				c.add(Calendar.MONTH, 6);
				c.set(Calendar.DATE, 1);
				c.add(Calendar.DATE, -1);
			}
			break;
		case TRIMESTRE3_ANYO_ACTUAL:

			if (mesActual > 8) {
				// Situo la variable Calendar en el inicio del 3º Trimestre del
				// año
				// actual
				c.set(Calendar.MONTH, 0);
				c.add(Calendar.MONTH, 9);
				c.set(Calendar.DATE, 1);
				c.add(Calendar.DATE, -1);
			}
			break;
		case TRIMESTRE4_ANYO_ACTUAL:

			// Situo la variable Calendar en el inicio del 3º Trimestre del año
			// actual
			// c.add(Calendar.YEAR, 1);
			// c.set(Calendar.MONTH, 0);
			// c.set(Calendar.DATE, 1);
			// c.add(Calendar.DATE, -1);

			break;

		default:
			break;
		}

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el primer dia del Año anterior
	 */
	public int obtenerInicioEnteroFechaAnyoAnterior() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha del primer día del año
		// anterior
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -1);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DATE, 1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el último dia del año anterior
	 */
	public int obtenerFinEnteroFechaAnyoAnterior() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha del último día del año
		// anterior
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DATE, 1);
		c.add(Calendar.DATE, -1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el primer dia del año actual
	 */
	public int obtenerInicioEnteroFechaAnyoActual() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar la fecha del primer día del año actua
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DATE, 1);

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un entero el último dia del año actual
	 */
	public int obtenerFinEnteroFechaAnyoActual() {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		// Obtengo de la variable Calendar el día de hoy
		Calendar c = Calendar.getInstance();

		/*
		 * fecha del último dia del año actual c.add(Calendar.YEAR, 1);
		 * c.set(Calendar.MONTH, 0); c.set(Calendar.DATE, 1);
		 * c.add(Calendar.DATE, -1);
		 */

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (c.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		/*
		 * Si el día solo tiene un dígito le pongo un cero delante Le estoy
		 * sumando 7 para que me de el útimo día de la semana
		 */
		day = "" + c.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + c.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	/*
	 * Método que me devuelve en un String la fecha de inicio en el formato Día
	 * de Mes de Año
	 */
	public String obtenerFechaInicioLibre(Context c, Calendar cInicio) {

		String fecha;

		// Obtengo la fecha en el formato Día de Mes de Año
		fecha = "" + cInicio.get(Calendar.DAY_OF_MONTH) + " "
				+ c.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + obtenerMes(c,cInicio.get(Calendar.MONTH)) + " "
				+ c.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + cInicio.get(Calendar.YEAR);

		return fecha;

	}
	
	/*
	 * Método que me devuelve en un String la fecha de fin en el formato Día de
	 * Mes de Año
	 */
	public String obtenerFechaFinLibre(Context c, Calendar cFin) {

		String fecha;

		// Obtengo la fecha en el formato Día de Mes de Año
		fecha = "" + cFin.get(Calendar.DAY_OF_MONTH) + " "
				+ c.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + obtenerMes(c,cFin.get(Calendar.MONTH)) + " "
				+ c.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + cFin.get(Calendar.YEAR);

		return fecha;

	}
	
	/*
	 * Método que me devuelve la fecha de INICIO Calendar en una variable entero
	 */
	public int obtenerEnteroFechaInicio(Calendar cInicio) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (cInicio.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + cInicio.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + cInicio.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}

	/*
	 * Método que me devuelve la fecha de FIN en una variable entero
	 */
	public int obtenerEnteroFechaFin(Calendar cFin) {

		String fecha;
		String month;
		String day;
		int entero_fecha;

		/*
		 * Al mes le sumo +1 porque el mes inicial (Enero) empieza desde cero:0
		 * Si el mes solo tiene un dígito le pongo un cero delante
		 */
		month = "" + (cFin.get(Calendar.MONTH) + 1);
		if (month.length() == 1) {
			month = "0" + month;
		}

		// Si el día solo tiene un dígito le pongo un cero delante
		day = "" + cFin.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1) {
			day = "0" + day;
		}

		// Obtengo la fecha en el formato AAAAMMDD
		fecha = "" + cFin.get(Calendar.YEAR) + month + day;

		// Paso la fecha a entero
		entero_fecha = Integer.valueOf(fecha).intValue();

		return entero_fecha;

	}
	
	
	/*
	 * Método para obtener la fecha en una Cadena Formateada
	 */
	public String obtenerCadenaFecha(Context contexto,int enteroFecha) {

		String fecha = new String("" + enteroFecha);
		String cadenaFecha;
		String year = fecha.substring(0, 4);
		String month = fecha.substring(4, 6);
		String day = fecha.substring(6);
		int y = Integer.valueOf(year).intValue();
		int m = Integer.valueOf(month).intValue();
		int d = Integer.valueOf(day).intValue();

		// Obtengo la variable Calendar
		Calendar c = Calendar.getInstance();
		// Inicio la variable Calendar con la fecha que tengo actualmente
		c.set(y, m - 1, d);
		
		cadenaFecha = obtenerDiaSemana(contexto,c.get(Calendar.DAY_OF_WEEK)) + ", "
				+ c.get(Calendar.DAY_OF_MONTH) + " "
				+ contexto.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + obtenerMes(contexto,c.get(Calendar.MONTH)) + " "
				+ contexto.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + c.get(Calendar.YEAR);

		return cadenaFecha;
	}
	
	/*
	 * Método para obtener la fecha MENSUAL en una Cadena Formateada 
	 */
	public String obtenerCadenaFechaMensual(Context contexto,int enteroFecha) {

		String fecha = new String("" + enteroFecha);
		String cadenaFecha;
		String year = fecha.substring(0, 4);
		String month = fecha.substring(4, 6);
		String day = fecha.substring(6);
		int y = Integer.valueOf(year).intValue();
		int m = Integer.valueOf(month).intValue();
		int d = Integer.valueOf(day).intValue();

		// Obtengo la variable Calendar
		Calendar c = Calendar.getInstance();
		// Inicio la variable Calendar con la fecha que tengo actualmente
		c.set(y, m - 1, d);

		cadenaFecha = obtenerMes(contexto,c.get(Calendar.MONTH)) + " "
				+ contexto.getResources().getString(R.string.datasactivity_conjuncion)
				+ " " + c.get(Calendar.YEAR);

		return cadenaFecha;
	}
	
	/*
	 * Método para obtener la fecha en una Cadena Formateada con el día de la
	 * semana y el día del mes; Ejemplo: Lun, 25
	 */
	public String obtenerCadenaFechaParcial(Context contexto,int enteroFecha) {

		String fecha = new String("" + enteroFecha);
		String cadenaFecha;
		String year = fecha.substring(0, 4);
		String month = fecha.substring(4, 6);
		String day = fecha.substring(6);
		int y = Integer.valueOf(year).intValue();
		int m = Integer.valueOf(month).intValue();
		int d = Integer.valueOf(day).intValue();

		// Obtengo la variable Calendar
		Calendar c = Calendar.getInstance();
		// Inicio la variable Calendar con la fecha que tengo actualmente
		c.set(y, m - 1, d);

		cadenaFecha = obtenerDiaSemana(contexto,c.get(Calendar.DAY_OF_WEEK)) + ". "
				+ c.get(Calendar.DAY_OF_MONTH);

		return cadenaFecha;
	}
	
	/*
	 * Método que me devuelve el mes según el entero pasado por parámetro
	 */
	public String obtenerMes(Context c, int month) {

		String m = new String();

		switch (month) {

		case 0:
			m = c.getResources().getString(R.string.informes_ene);
			break;
		case 1:
			m = c.getResources().getString(R.string.informes_feb);
			break;
		case 2:
			m = c.getResources().getString(R.string.informes_mar);
			break;
		case 3:
			m = c.getResources().getString(R.string.informes_abr);
			break;
		case 4:
			m = c.getResources().getString(R.string.informes_may);
			break;
		case 5:
			m = c.getResources().getString(R.string.informes_jun);
			break;
		case 6:
			m = c.getResources().getString(R.string.informes_jul);
			break;
		case 7:
			m = c.getResources().getString(R.string.informes_ago);
			break;
		case 8:
			m = c.getResources().getString(R.string.informes_sep);
			break;
		case 9:
			m = c.getResources().getString(R.string.informes_oct);
			break;
		case 10:
			m = c.getResources().getString(R.string.informes_nov);
			break;
		case 11:
			m = c.getResources().getString(R.string.informes_dic);
			break;
		default:
			m = "error";
			break;
		}

		return m;

	}
	
	/*
	 * Método que me devuelve el mes según el entero pasado por parámetro
	 */
	public String obtenerMesDatasActivity(Context c,int month) {

		String m = new String();

		switch (month) {

		case 0:
			m = c.getResources().getString(R.string.datasactivity_ene);
			break;
		case 1:
			m = c.getResources().getString(R.string.datasactivity_feb);
			break;
		case 2:
			m = c.getResources().getString(R.string.datasactivity_mar);
			break;
		case 3:
			m = c.getResources().getString(R.string.datasactivity_abr);
			break;
		case 4:
			m = c.getResources().getString(R.string.datasactivity_may);
			break;
		case 5:
			m = c.getResources().getString(R.string.datasactivity_jun);
			break;
		case 6:
			m = c.getResources().getString(R.string.datasactivity_jul);
			break;
		case 7:
			m = c.getResources().getString(R.string.datasactivity_ago);
			break;
		case 8:
			m = c.getResources().getString(R.string.datasactivity_sep);
			break;
		case 9:
			m = c.getResources().getString(R.string.datasactivity_oct);
			break;
		case 10:
			m = c.getResources().getString(R.string.datasactivity_nov);
			break;
		case 11:
			m = c.getResources().getString(R.string.datasactivity_dic);
			break;
		default:
			m = "error";
			break;
		}

		return m;

	}
	
	/*
	 * Método que me devuelve el día de la semana según el entero pasado por
	 * parámetro
	 */
	public String obtenerDiaSemana(Context c, int day) {

		String d = new String();

		switch (day) {
		case 1:
			d = c.getResources().getString(R.string.datasactivity_dom);
			break;
		case 2:
			d = c.getResources().getString(R.string.datasactivity_lun);
			break;
		case 3:
			d = c.getResources().getString(R.string.datasactivity_martes);
			break;
		case 4:
			d = c.getResources().getString(R.string.datasactivity_mie);
			break;
		case 5:
			d = c.getResources().getString(R.string.datasactivity_jue);
			break;
		case 6:
			d = c.getResources().getString(R.string.datasactivity_vie);
			break;
		case 7:
			d = c.getResources().getString(R.string.datasactivity_sab);
			break;
		default:
			break;
		}

		return d;
	}
	
}
