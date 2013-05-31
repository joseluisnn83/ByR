package com.joseluisnn.singleton;

public class SingletonCreateDatabase {
	
	private static SingletonCreateDatabase INSTANCE = new SingletonCreateDatabase();
	
	/*
	 * Me creo las constantes para la creacion de la Base de Datos
	 */
	public final String CREATE_TABLE_CONCEPTOS = "CREATE TABLE conceptos( " +
			"id_concepto INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
			"nombre VARCHAR2(50) UNIQUE, " +
			"tipo_concepto VARCHAR2(10), " +
			"CONSTRAINT ck_tipo_concepto CHECK (tipo_concepto='" + "ingreso" + "' OR tipo_concepto='" + "gasto" + "'))";
	public final String CREATE_TABLE_FECHAS = "CREATE TABLE fechas( " +
			"id_fecha INTEGER NOT NULL, " +
			"CONSTRAINT pk_idFecha_fechas PRIMARY KEY(id_fecha), " +
			"CONSTRAINT ck_idFecha_fechas CHECK (id_fecha>19000101 and id_fecha<99999999))";
	public final String CREATE_TABLE_DATOS = "CREATE TABLE datos( " +
			"valor FLOAT NOT NULL, " +
			"id_concepto INTEGER NOT NULL, " +
			"id_fecha INTEGER NOT NULL, " +
			"CONSTRAINT pk_datos PRIMARY KEY(id_concepto,id_fecha), " +
			"CONSTRAINT fk_datos_idConcepto FOREIGN KEY(id_concepto) REFERENCES conceptos(id_concepto) ON UPDATE CASCADE, " +
			"CONSTRAINT fk_datos_idFecha FOREIGN KEY(id_fecha) REFERENCES fechas(id_fecha) ON UPDATE CASCADE)";
	public final String CREATE_TRIGGER_INSERTAR_DATO_IDCONCEPTO = 
			"CREATE TRIGGER fk_insertar_dato_idconcepto BEFORE INSERT ON datos " +
			"FOR EACH ROW " +
			"BEGIN " +
			"select raise(rollback, '" + "Inserción en datos viola la restricción fk_datos_idConcepto" + "') " +
			"where (select id_concepto from conceptos where id_concepto=new.id_concepto) is null;" +
			"END;";
	public final String CREATE_TRIGGER_INSERTAR_DATO_IDFECHA = 
			"CREATE TRIGGER fk_insertar_datos_idfecha BEFORE INSERT ON datos " +
			"FOR EACH ROW " +
			"BEGIN " +
			"select raise(rollback, '" + "Inserción en datos viola la restricción fk_datos_idFecha" + "') " +
			"where (select id_fecha from fechas where id_fecha=new.id_fecha) is null; " +
			"END;";
	public final String CREATE_TRIGGER_ACTUALIZAR_DATO_IDCONCEPTO = 
			"CREATE TRIGGER fk_update_dato_idconcepto BEFORE UPDATE ON datos " +
			"FOR EACH ROW " +
			"BEGIN " +
			"select raise(rollback, '" + "Actualización en datos viola la restricción fk_datos_idConcepto" + "') " +
			"where (select id_concepto from conceptos where id_concepto=new.id_concepto) is null; " +
			"END;";
	public final String CREATE_TRIGGER_ACTUALIZAR_DATO_IDFECHA = 
			"CREATE TRIGGER fk_update_dato_idfecha BEFORE UPDATE ON datos " +
			"FOR EACH ROW " +
			"BEGIN " +
			"select raise(rollback, '" + "Actualización en datos viola la restricción fk_datos_idFecha" + "') " +
			"where (select id_fecha from fechas where id_fecha=new.id_fecha) is null; " +
			"END;";
	public final String CREATE_TRIGGER_BORRAR_IDCONCEPTO = 
			"CREATE TRIGGER pk_delete_idconcepto BEFORE DELETE ON conceptos " +
			"FOR EACH ROW " +
			"BEGIN " +
			"select raise(rollback, '" + "Borrado en conceptos viola la restricción fk_datos_idConcepto, ya que existen en otras tablas referenciadas" + "') " +
			"where (select id_concepto from datos where id_concepto=old.id_concepto) is not null; " +
			"END;";
	public final String CREATE_TRIGGER_BORRAR_IDFECHA = 
			"CREATE TRIGGER pk_delete_idfecha BEFORE DELETE ON fechas " +
			"FOR EACH ROW " +
			"BEGIN " +
			"select raise(rollback, '" + "Borrado en fechas viola la restricción fk_datos_idFecha, ya que existen en otras tablas referenciadas" + "') " +
			"where (select id_fecha from datos where id_fecha=old.id_fecha) is not null; " +
			"END;";
	public final String INSERT_INTO_CONCEPTOS_INGRESO_BORRADO = 
			"insert into conceptos (nombre,tipo_concepto) " +
			"values ('" + "ingreso_borrado" + "','" + "ingreso" + "')";
	public final String INSERT_INTO_CONCEPTOS_GASTO_BORRADO = 
			"insert into conceptos (nombre,tipo_concepto) " +
			"values ('" + "gasto_borrado" + "','" + "gasto" + "')";
	public final String INSERT_INTO_CONCEPTOS_INGRESO = 
			"insert into conceptos (nombre,tipo_concepto) " +
			"values ('" + "caja diario" + "','" + "ingreso" + "')";
	public final String INSERT_INTO_CONCEPTOS_GASTO = 
			"insert into conceptos (nombre,tipo_concepto) " +
			"values ('" + "alquiler local" + "','" + "gasto" + "')";
	
	
	public SingletonCreateDatabase() {}
	
	/*
	 * creador sincronizado para protegerse de posibles problemas  multi-hilo otra prueba para evitar 
	 * instanciación múltiple 
	 */
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new SingletonCreateDatabase();
        }
    }
    
    public static SingletonCreateDatabase getInstance() {
        createInstance();
        return INSTANCE;
    }

}
