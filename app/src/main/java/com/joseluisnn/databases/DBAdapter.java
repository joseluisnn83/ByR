package com.joseluisnn.databases;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.joseluisnn.objetos.Concepto;
import com.joseluisnn.objetos.FiltroConcepto;
import com.joseluisnn.objetos.ValoresElementoListaGD;
import com.joseluisnn.objetos.ValoresElementosGraficas;

public class DBAdapter {

	private Context context;
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private static DBAdapter INSTANCE = null;

	// Constructor de la clase
	public DBAdapter(Context cont) {
		this.context = cont;
	}

	// creador sincronizado para protegerse de posibles problemas multi-hilo
	// otra prueba para evitar instanciación múltiple : es un Singleton para
	// evitar instanción múltiple al objeto
	private synchronized static void createInstance(Context cont) {
		if (INSTANCE == null) {
			INSTANCE = new DBAdapter(cont);
		}
	}

	public static DBAdapter getInstance(Context cont) {
		createInstance(cont);
		return INSTANCE;
	}

	// Apertura de la base de datos en modo lectura
	public DBAdapter openREAD() throws SQLException {

		dbHelper = new DataBaseHelper(this.context);
		database = dbHelper.getReadableDatabase();

		return this;
	}

	// Apertura de la base de datos en modo lectura/escritura
	public DBAdapter openREADWRITE() throws SQLException {

		dbHelper = new DataBaseHelper(this.context);
		database = dbHelper.getWritableDatabase();

		return this;
	}

	// Método para cerrar la base de datos
	public void close() {
		dbHelper.close();
		database = null;
	}

	// Método para saber si la base de datos está abierta o no
	public boolean isOpen() {

		if (database != null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Método para insertar un concepto
	 */
	public int insertarConcepto(Concepto c) {

		int registro = 1;

		try {

			database.execSQL("INSERT INTO conceptos (nombre,tipo_concepto) "
					+ "VALUES ('" + c.getNombre() + "','" + c.getTipo() + "')");

		} catch (SQLiteConstraintException e) {

			registro = -1;
			lanzarMensaje("Error: Concepto ya existente, introduzca otro nombre");

		}

		return registro;

	}

	/*
	 * Método para actualizar un concepto
	 */
	public int actualizarConcepto(Concepto c) {

		int registro = 1;

		try {

			database.execSQL("UPDATE conceptos SET nombre = '" + c.getNombre()
					+ "' " + "WHERE id_concepto=" + c.getId());

		} catch (SQLException e) {

			registro = -1;
			lanzarMensaje("Error: Concepto ya existente,\nintroduzca otro nombre");

		}

		return registro;

	}

	/*
	 * Método para borrar un concepto
	 */
	public void borrarConcepto(Concepto c) {

		/*
		 * primero ver si hay dependencia con valores de la tabla datos; si los
		 * hubiera habría que borrar primero los datos de la tabla datos que
		 * tienen conceptos como clave ajena
		 */
		try {

			database.execSQL("DELETE FROM conceptos WHERE id_concepto="
					+ c.getId());

		} catch (SQLException e) {
			lanzarMensajeError(e);
		}
	}

	/*
	 * Método para listar los conceptos del tipo pasado por parámetro
	 */
	public ArrayList<Concepto> listarConceptos(String tipo) {

		ArrayList<Concepto> lista = new ArrayList<Concepto>();
		String consulta = "select * from conceptos " + "where tipo_concepto='"
				+ tipo + "' " + "and  nombre not like 'ingreso_borrado' "
				+ "and nombre not like 'gasto_borrado' "
				+ "ORDER BY nombre ASC";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {
			lista.add(new Concepto(cursor.getInt(cursor
					.getColumnIndex("id_concepto")), cursor.getString(cursor
					.getColumnIndex("nombre")), cursor.getString(cursor
					.getColumnIndex("tipo_concepto"))));
		}

		// cierro el cursor
		cursor.close();

		return lista;
	}

	public ArrayList<String> listarArrayConceptos(String tipo) {

		ArrayList<String> lista = new ArrayList<String>();
		String consulta = "select * from conceptos " + "where tipo_concepto='"
				+ tipo + "' " + "and nombre not like 'ingreso_borrado' "
				+ "and nombre not like 'gasto_borrado' "
				+ "ORDER BY nombre ASC";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {
			lista.add(new String(cursor.getString(cursor
					.getColumnIndex("nombre"))));
		}

		// cierro el cursor
		cursor.close();

		return lista;
	}
	
	/*
	 * Método para listar el filtro de los conceptos para listarlos en su adaptador y clase correspondiente
	 */
	public ArrayList<FiltroConcepto> listarFiltroConceptos(String tipo) {

		ArrayList<FiltroConcepto> lista = new ArrayList<FiltroConcepto>();
		String consulta = "select * from conceptos " + "where tipo_concepto='"
				+ tipo + "' " + "and  nombre not like 'ingreso_borrado' "
				+ "and nombre not like 'gasto_borrado' "
				+ "ORDER BY nombre ASC";

		Cursor cursor = database.rawQuery(consulta, null);

		// Inserto el filtro de todos los conceptos
		lista.add(new FiltroConcepto(-1,"*", true));
		
		while (cursor.moveToNext()) {
			lista.add( new FiltroConcepto(cursor.getInt(cursor.getColumnIndex("id_concepto")),cursor.getString(cursor.getColumnIndex("nombre")), false));
		}

		// cierro el cursor
		cursor.close();

		return lista;
	}

	/*
	 * Método para obtener el id de un concepto a partir de su nombre y tipo
	 */
	public int getIdConcepto(String nombre, String tipo) {

		String consulta = "select id_concepto from conceptos where tipo_concepto='"
				+ tipo + "' and nombre='" + nombre + "'";
		int id_concepto = -1;

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {
			id_concepto = cursor.getInt(cursor.getColumnIndex("id_concepto"));
		}

		// cierro el cursor
		cursor.close();

		return id_concepto;

	}

	/*
	 * Método donde se inserta en la tabla FECHAS una fecha pasada por parámetro
	 */
	public int insertarFecha(int fecha) {

		int registro = 1;

		try {

			database.execSQL("insert into fechas values (" + fecha + ")");

		} catch (SQLiteConstraintException e) {

			registro = -1;
			// lanzarMensaje("ERROR al introducir la fecha\n de ingreso o gasto");

		}

		return registro;

	}

	/*
	 * Método que me dice si la fecha pasada por parámetro existe o no
	 */
	public int fechaYaExistente(int fecha) {

		String consulta = "select * from fechas where id_fecha=" + fecha;
		int id_concepto = -1;

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {
			id_concepto = cursor.getInt(cursor.getColumnIndex("id_fecha"));
		}

		// cierro el cursor
		cursor.close();

		return id_concepto;

	}

	/*
	 * Método que me comprueba si hay mas valores de la tabla DATOS donde tengan
	 * la misma fecha pasado por parámetro. -Si existen datos con esa fecha no
	 * borro dicha fecha de la tabla Fechas -Si no hay datos con la fecha pasada
	 * por parámetro entonces la borro de la tabla Fechas ya que no me sirve
	 */
	public int comprobacionDatosConEstaFecha(int fecha) {

		int numeroFechas;

		String consulta = "select count(*)  as 'numeroFechas' " + "from datos "
				+ "where id_fecha=" + fecha;

		// Abro y ejecuto cursor
		Cursor cursor = database.rawQuery(consulta, null);

		// Leo del cursor
		if (cursor.moveToNext()) {
			numeroFechas = cursor.getInt(cursor.getColumnIndex("numeroFechas"));
		} else {
			numeroFechas = 0;
		}

		// cierro el cursor
		cursor.close();

		return numeroFechas;

	}

	/*
	 * Método donde inserto un valor (ingreso o gasto) en la tabla DATOS
	 * 
	 * @registro = 1 : todo correcto
	 * 
	 * @registro = -1 : error al insertar el valor, por duplicar clave primaria
	 * 
	 * @registro = -2 : error al insertar la fecha
	 */
	public int insertarValor(double valor, int idConcepto, int fecha) {

		int registro = 1;

		String consulta = "insert into datos (valor,id_concepto,id_fecha) "
				+ "values(" + valor + "," + idConcepto + "," + fecha + ")";

		// Si no existe la fecha, entra en el IF e inserto la fecha
		if (fechaYaExistente(fecha) < 1) {
			// Inserto la fecha en la tabla FECHAS
			if (insertarFecha(fecha) == 1) {

				try {
					// Inserto el valor en la tabla DATOS
					database.execSQL(consulta);

				} catch (SQLiteConstraintException e) {

					registro = -1;
					// lanzarMensaje("Error al introducir el VALOR");

				}

			} else {

				registro = -2;

			}

		} else {
			// entra por aquí si la fecha existe y no necesito insertarla

			try {
				// Inserto el valor en la tabla DATOS
				database.execSQL(consulta);

			} catch (SQLiteConstraintException e) {

				registro = -1;
				// lanzarMensaje("Error al introducir el VALOR");

			}
		}

		return registro;

	}

	/*
	 * Método donde borro un valor (ingreso o gasto) de la tabla de DATOS
	 * 
	 * @registro = 1 : todo va bien
	 * 
	 * @registro = -1 : error al borrar el valor de la tabla Datos
	 * 
	 * @registro = -2 : error al borrar la fecha de la tabla Fechas
	 */
	public int borrarValor(int idConcepto, int fecha) {

		int registro = 1;

		String consulta = "delete from datos " + "where id_concepto="
				+ idConcepto + " and id_fecha=" + fecha;
		String consulta2 = "delete from fechas where id_fecha=" + fecha;

		try {
			// Borro el valor en la tabla DATOS
			database.execSQL(consulta);

		} catch (SQLiteConstraintException e) {

			registro = -1;
			// lanzarMensaje("Error al borrar el VALOR con fecha");

		}

		if (registro == 1 && comprobacionDatosConEstaFecha(fecha) == 0) {

			try {
				// Inserto el valor en la tabla DATOS
				database.execSQL(consulta2);

			} catch (SQLiteConstraintException e) {

				registro = -2;
				// lanzarMensaje("Error al borrar la fecha \n de la tabla FECHAS");

			}
		}

		return registro;

	}

	/*
	 * Método que me borra todos los valores de la tabla DATOS según el
	 * idConcepto pasado por parámetro
	 */
	public int borrarValoresSegunConcepto(Concepto c) {

		int registro = 1;
		int fecha;
		ArrayList<Integer> listaFechas = new ArrayList<Integer>();
		String consulta1 = "delete from datos " + "where id_concepto="
				+ c.getId();
		String consulta2 = "";

		/*
		 * Recupero todas las fechas asociadas a los valores que tiene
		 * idConcepto para despúes borrarlas de la tabla FECHAS
		 */
		listaFechas = recuperaFechas(c.getId());

		/*
		 * Borro los valores de los datos asociados
		 */
		try {
			// borro los valores de la tabla DATOS
			database.execSQL(consulta1);

		} catch (SQLiteConstraintException e) {

			registro = -1;
			lanzarMensaje("Error al borrar valores \n de la tabla DATOS");

		}

		if (registro != -1) {

			/*
			 * Borro el concepto
			 */
			borrarConcepto(c);

			/*
			 * Borro las fechas que no tienen clave ajena a la tabla DATOS
			 */
			for (int i = 0; i < listaFechas.size(); i++) {

				fecha = listaFechas.get(i).intValue();

				if (comprobacionDatosConEstaFecha(fecha) == 0) {

					consulta2 = "delete from fechas where id_fecha=" + fecha;

					try {
						// Inserto el valor en la tabla DATOS
						database.execSQL(consulta2);

					} catch (SQLiteConstraintException e) {

						lanzarMensaje("Error al borrar la fecha \n de la tabla FECHAS");

					}
				}
			}
		}

		return registro;

	}

	private ArrayList<Integer> recuperaFechas(int idConcepto) {

		String consulta = "select id_fecha " + "from datos "
				+ "where id_concepto=" + idConcepto;
		ArrayList<Integer> listaFechas = new ArrayList<Integer>();

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaFechas.add(Integer.valueOf(cursor.getInt(cursor
					.getColumnIndex("id_fecha"))));

		}

		return listaFechas;

	}

	/*
	 * Método donde ACTUALIZO un valor (ingreso o gasto) o concepto en la tabla
	 * DATOS
	 */
	public int actualizarRegistroTablaDatos(double valor,
			int idConceptoAnterior, int idConceptoNuevo, int fecha) {

		int registro = 1;

		String consulta = "update datos " + "set valor=" + valor
				+ ", id_concepto=" + idConceptoNuevo + " where id_concepto="
				+ idConceptoAnterior + " and id_fecha=" + fecha;

		try {
			// Inserto el valor en la tabla DATOS
			database.execSQL(consulta);

		} catch (SQLiteConstraintException e) {

			registro = -1;
			// lanzarMensaje("Error al editar el VALOR");

		}

		return registro;

	}

	/*
	 * Método donde ACTUALIZO un valor (ingreso o gasto) en la tabla DATOS
	 */
	public int actualizarValor(double valor, int idConcepto, int fecha) {

		int registro = 1;

		String consulta = "update datos " + "set valor=" + valor
				+ " where id_concepto=" + idConcepto + " and id_fecha=" + fecha;

		try {
			// Inserto el valor en la tabla DATOS
			database.execSQL(consulta);

		} catch (SQLiteConstraintException e) {

			registro = -1;

		}

		return registro;

	}

	/*
	 * Método donde se recupera los conceptos de ingresos con sus respectivos
	 * valores en euros y se vuelca a una Lista
	 */
	public ArrayList<ValoresElementoListaGD> obtenerValoresIngresos(int fecha) {

		ArrayList<ValoresElementoListaGD> listaVELGD = new ArrayList<ValoresElementoListaGD>();

		String consulta = "select b.id_concepto,b.id_fecha,a.nombre,b.valor "
				+ "from conceptos a, datos b "
				+ "where a.id_concepto = b.id_concepto " + "and b.id_fecha = "
				+ fecha + " and a.tipo_concepto = 'ingreso'";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVELGD.add(new ValoresElementoListaGD(cursor.getInt(cursor
					.getColumnIndex("id_concepto")), cursor.getInt(cursor
					.getColumnIndex("id_fecha")), cursor.getString(cursor
					.getColumnIndex("nombre")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVELGD;
	}

	/*
	 * Método donde se recupera los conceptos de gastos con sus respectivos
	 * valores en euros y se vuelca a una Lista
	 */
	public ArrayList<ValoresElementoListaGD> obtenerValoresGastos(int fecha) {

		ArrayList<ValoresElementoListaGD> listaVELGD = new ArrayList<ValoresElementoListaGD>();

		String consulta = "select b.id_concepto,b.id_fecha,a.nombre,b.valor "
				+ "from conceptos a, datos b "
				+ "where a.id_concepto = b.id_concepto " + "and b.id_fecha = "
				+ fecha + " and a.tipo_concepto = 'gasto'";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVELGD.add(new ValoresElementoListaGD(cursor.getInt(cursor
					.getColumnIndex("id_concepto")), cursor.getInt(cursor
					.getColumnIndex("id_fecha")), cursor.getString(cursor
					.getColumnIndex("nombre")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVELGD;
	}

	/*
	 * Método que me va a devolver las fechas de un mes en concreto las cuales
	 * tengan valores (ingresos o gastos) y poder colorear las casillas del
	 * calendario para indicarlo
	 * 
	 * @fecha: fecha en formato AAAAMM
	 */
	public ArrayList<Integer> obtenerDiasConValores(int fecha) {

		ArrayList<Integer> listaODCV = new ArrayList<Integer>();
		String sFechaInicial = new String(fecha + "01");
		String sFechaFinal = new String(fecha + "31");

		Integer iFechaInicial = Integer.valueOf(sFechaInicial);
		Integer iFechaFinal = Integer.valueOf(sFechaFinal);

		String consulta = "select distinct id_fecha " + "from datos "
				+ "where id_fecha between " + iFechaInicial.intValue()
				+ " and " + iFechaFinal.intValue() + " order by id_fecha";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaODCV.add(Integer.valueOf(cursor.getInt(cursor
					.getColumnIndex("id_fecha"))));

		}

		return listaODCV;

	}

	/*
	 * Método que me va a Listar las fechas, conceptos y valores asociados entre
	 * dos fechas pasadas por parámetro para los INGRESOS
	 */
	public ArrayList<ValoresElementoListaGD> listadoValoresIngresosPorFecha(
			int fechaini, int fechafin, ArrayList<FiltroConcepto> filtro) {

		Boolean salir = false;
		boolean esPrimeraIteracion = true;
		int i = 0;
		String f = " ( ";
		
		// Escribo el filtro de INGRESOS a mostrar				
		while (!salir && i<filtro.size()) {
			
			if (!filtro.get(i).getNombre().equals("*")){
				
				if(filtro.get(i).isFiltrado()){
				
					if(esPrimeraIteracion){
						f = f + " b.id_concepto = " + filtro.get(i).getIdConcepto() + " ";
						esPrimeraIteracion = false;
					}else{
						f = f + " or " + " b.id_concepto = " + filtro.get(i).getIdConcepto() + " ";
					}
				}
				
			}else{
				if(filtro.get(i).isFiltrado()){
					
					f = f + " 1=1 ";
					salir = true;
				}
			}
			
			i++;
		} 
		
		f = f + " ) ";		
		
		ArrayList<ValoresElementoListaGD> listaVELGD = new ArrayList<ValoresElementoListaGD>();

		String consulta = "select a.id_fecha,a.id_concepto,b.nombre,a.valor "
				+ "from datos a, conceptos b "
				+ "where a.id_concepto = b.id_concepto "
				+ "and b.tipo_concepto like 'ingreso' "
				+ "and " + f
				+ "and a.id_fecha between " + fechaini + " and " + fechafin
				+ " order by a.id_fecha asc";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVELGD.add(new ValoresElementoListaGD(cursor.getInt(cursor
					.getColumnIndex("id_concepto")), cursor.getInt(cursor
					.getColumnIndex("id_fecha")), cursor.getString(cursor
					.getColumnIndex("nombre")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVELGD;
	}

	/*
	 * Método que me va a Listar las fechas y valores agrupadas (suma de
	 * valores) asociados entre dos fechas pasadas por parámetro para los
	 * INGRESOS listadoValoresIngresosAnualesGraficas
	 */
	public ArrayList<ValoresElementosGraficas> listadoValoresIngresosPorFechaGraficas(
			int fechaini, int fechafin) {

		ArrayList<ValoresElementosGraficas> listaVEG = new ArrayList<ValoresElementosGraficas>();

		String consulta = "select substr(cast(a.id_fecha as varchar),1,6) as fecha,sum(a.valor) as valor "
				+ "from datos a, conceptos b "
				+ "where a.id_concepto = b.id_concepto "
				+ "and b.tipo_concepto like 'ingreso' "
				+ "and a.id_fecha between "
				+ fechaini
				+ " and "
				+ fechafin
				+ " group by fecha " + "order by fecha";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVEG.add(new ValoresElementosGraficas(cursor.getString(cursor
					.getColumnIndex("fecha")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVEG;
	}

	/*
	 * Método que me va a Listar las fechas, conceptos y valores asociados entre
	 * dos fechas pasadas por parámetro para los GASTOS
	 */
	public ArrayList<ValoresElementoListaGD> listadoValoresGastosPorFecha(
			int fechaini, int fechafin, ArrayList<FiltroConcepto> filtro) {

		Boolean salir = false;
		boolean esPrimeraIteracion = true;
		int i = 0;
		String f = " ( ";
		
		// Escribo el filtro de GASTOS a mostrar				
		while (!salir && i<filtro.size()) {
			
			if (!filtro.get(i).getNombre().equals("*")){
				
				if(filtro.get(i).isFiltrado()){
				
					if(esPrimeraIteracion){
						f = f + " b.id_concepto = " + filtro.get(i).getIdConcepto() + " ";
						esPrimeraIteracion = false;
					}else{
						f = f + " or " + " b.id_concepto = " + filtro.get(i).getIdConcepto() + " ";
					}
				}
				
			}else{
				if(filtro.get(i).isFiltrado()){
					
					f = f + " 1=1 ";
					salir = true;
				}
			}
			
			i++;
		} 
		
		f = f + " ) ";
		
		ArrayList<ValoresElementoListaGD> listaVELGD = new ArrayList<ValoresElementoListaGD>();

		String consulta = "select a.id_fecha,a.id_concepto,b.nombre,a.valor "
				+ "from datos a, conceptos b "
				+ "where a.id_concepto = b.id_concepto "
				+ "and b.tipo_concepto like 'gasto' "
				+ "and " + f 
				+ "and a.id_fecha between " + fechaini + " and " + fechafin
				+ " order by a.id_fecha asc";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVELGD.add(new ValoresElementoListaGD(cursor.getInt(cursor
					.getColumnIndex("id_concepto")), cursor.getInt(cursor
					.getColumnIndex("id_fecha")), cursor.getString(cursor
					.getColumnIndex("nombre")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVELGD;
	}

	/*
	 * Método que me va a Listar las fechas, valores agrupadas (suma de valores)
	 * asociados entre dos fechas pasadas por parámetro para los GASTOS
	 */
	public ArrayList<ValoresElementosGraficas> listadoValoresGastosPorFechaGraficas(
			int fechaini, int fechafin) {

		ArrayList<ValoresElementosGraficas> listaVEG = new ArrayList<ValoresElementosGraficas>();

		String consulta = "select substr(cast(a.id_fecha as varchar),1,6) as fecha,sum(a.valor) as valor "
				+ "from datos a, conceptos b "
				+ "where a.id_concepto = b.id_concepto "
				+ "and b.tipo_concepto like 'gasto' "
				+ "and a.id_fecha between "
				+ fechaini
				+ " and "
				+ fechafin
				+ " group by fecha " + "order by fecha";

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVEG.add(new ValoresElementosGraficas(cursor.getString(cursor
					.getColumnIndex("fecha")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVEG;
	}

	/*
	 * Método que me va a mostrar la (suma de valores) de los INGRESOS ó GASTOS
	 * agrupadas ANUALMENTE para la representación de la gráfica
	 */
	public ArrayList<ValoresElementosGraficas> listadoValoresAnualesGraficas(
			int fechaini, int fechafin, String tipoDato) {

		ArrayList<ValoresElementosGraficas> listaVEG = new ArrayList<ValoresElementosGraficas>();
		String consulta;

		if (tipoDato.equals("ingreso")) {

			consulta = "select substr(cast(a.id_fecha as varchar),1,4) as fecha,sum(a.valor) as valor "
					+ "from datos a, conceptos b "
					+ "where a.id_concepto = b.id_concepto "
					+ "and b.tipo_concepto like 'ingreso' "
					+ "and a.id_fecha between "
					+ fechaini
					+ " and "
					+ fechafin
					+ " group by fecha " + "order by fecha";
		} else {

			consulta = "select substr(cast(a.id_fecha as varchar),1,4) as fecha,sum(a.valor) as valor "
					+ "from datos a, conceptos b "
					+ "where a.id_concepto = b.id_concepto "
					+ "and b.tipo_concepto like 'gasto' "
					+ "and a.id_fecha between "
					+ fechaini
					+ " and "
					+ fechafin
					+ " group by fecha " + "order by fecha";

		}

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVEG.add(new ValoresElementosGraficas(cursor.getString(cursor
					.getColumnIndex("fecha")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVEG;
	}

	/*
	 * Método que me va a mostrar la (suma de valores) de los INGRESOS ó GASTOS
	 * agrupadas MENSUALMENTE para la representación de la gráfica
	 */
	public ArrayList<ValoresElementosGraficas> listadoValoresMensualesGraficas(
			int fechaini, int fechafin, String tipoDato) {

		ArrayList<ValoresElementosGraficas> listaVEG = new ArrayList<ValoresElementosGraficas>();
		String consulta;

		if (tipoDato.equals("ingreso")) {

			consulta = "select substr(cast(a.id_fecha as varchar),1,6) as fecha,sum(a.valor) as valor "
					+ "from datos a, conceptos b "
					+ "where a.id_concepto = b.id_concepto "
					+ "and b.tipo_concepto like 'ingreso' "
					+ "and a.id_fecha between "
					+ fechaini
					+ " and "
					+ fechafin
					+ " group by fecha " + "order by fecha";
		} else {

			consulta = "select substr(cast(a.id_fecha as varchar),1,6) as fecha,sum(a.valor) as valor "
					+ "from datos a, conceptos b "
					+ "where a.id_concepto = b.id_concepto "
					+ "and b.tipo_concepto like 'gasto' "
					+ "and a.id_fecha between "
					+ fechaini
					+ " and "
					+ fechafin
					+ " group by fecha " + "order by fecha";

		}

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVEG.add(new ValoresElementosGraficas(cursor.getString(cursor
					.getColumnIndex("fecha")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVEG;
	}

	/*
	 * Método que me va a mostrar la (suma de valores) de los INGRESOS ó GASTOS
	 * agrupadas DIARIAMENTE para la representación de la gráfica
	 */
	public ArrayList<ValoresElementosGraficas> listadoValoresDiariosGraficas(
			int fechaini, int fechafin, String tipoDato) {

		ArrayList<ValoresElementosGraficas> listaVEG = new ArrayList<ValoresElementosGraficas>();
		String consulta;

		if (tipoDato.equals("ingreso")) {

			consulta = "select a.id_fecha as fecha,sum(a.valor) as valor "
					+ "from datos a, conceptos b "
					+ "where a.id_concepto = b.id_concepto "
					+ "and b.tipo_concepto like 'ingreso' "
					+ "and a.id_fecha between " + fechaini + " and " + fechafin
					+ " group by fecha " + "order by fecha";
		} else {

			consulta = "select a.id_fecha as fecha,sum(a.valor) as valor "
					+ "from datos a, conceptos b "
					+ "where a.id_concepto = b.id_concepto "
					+ "and b.tipo_concepto like 'gasto' "
					+ "and a.id_fecha between " + fechaini + " and " + fechafin
					+ " group by fecha " + "order by fecha";

		}

		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {

			listaVEG.add(new ValoresElementosGraficas(cursor.getString(cursor
					.getColumnIndex("fecha")), cursor.getDouble(cursor
					.getColumnIndex("valor"))));

		}

		return listaVEG;
	}
	
	/*
	 * Método que me devuelve el total de ingresos o gastos
	 * desde la fecha pasada por parámetro hacia atrás
	 */
	public Double obtenerAcumulados(int fechaini, String tipoDato){
		
		Double acum = 0.0;
		
		String consulta;

		if (tipoDato.equals("ingreso")) {

			consulta = "select sum(a.valor) as valor " +
					"from datos a, conceptos b " +
					"where a.id_concepto = b.id_concepto " +
					"and b.tipo_concepto like 'ingreso' " +
					"and a.id_fecha <= " + fechaini;
		} else {

			consulta = "select sum(a.valor) as valor " +
					"from datos a, conceptos b " +
					"where a.id_concepto = b.id_concepto " +
					"and b.tipo_concepto like 'gasto' " +
					"and a.id_fecha <= " + fechaini;

		}
		
		Cursor cursor = database.rawQuery(consulta, null);

		while (cursor.moveToNext()) {
			acum = cursor.getDouble(cursor.getColumnIndex("valor"));		
		}
		
		return acum;
		
	}

	public void lanzarMensaje(String e) {
		Toast t = Toast.makeText(this.context, e, Toast.LENGTH_LONG);
		t.show();
	}

	public void lanzarMensajeError(Exception e) {
		Toast t = Toast.makeText(this.context, e.getMessage().toString(),
				Toast.LENGTH_SHORT);
		t.show();
	}
}
