package com.joseluisnn.databases;

import com.joseluisnn.singleton.SingletonCreateDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	
	// Nombre de la base de datos y versión de la misma
	private static final String DATABASE_NAME = "BD_BYR";
	private static final int DATABASE_VERSION = 1;

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Instancio la clase donde estan las cadenas con la creación de tablas
		SingletonCreateDatabase scdb = new SingletonCreateDatabase();
		// Creo las tablas conceptos, fechas y datos
		db.execSQL(scdb.CREAT_TABLE_CONCEPTOS);
		db.execSQL(scdb.CREATE_TABLE_FECHAS);
		db.execSQL(scdb.CREATE_TABLE_DATOS);
		// Creo los triggers de la Base de Datos
		db.execSQL(scdb.CREATE_TRIGGER_INSERTAR_DATO_IDCONCEPTO);
		db.execSQL(scdb.CREATE_TRIGGER_INSERTAR_DATO_IDFECHA);
		db.execSQL(scdb.CREATE_TRIGGER_ACTUALIZAR_DATO_IDCONCEPTO);
		db.execSQL(scdb.CREATE_TRIGGER_ACTUALIZAR_DATO_IDFECHA);
		db.execSQL(scdb.CREATE_TRIGGER_BORRAR_IDCONCEPTO);
		db.execSQL(scdb.CREATE_TRIGGER_BORRAR_IDFECHA);
		// Inserto los conceptos de ingresos y gastos por defecto
		db.execSQL(scdb.INSERT_INTO_CONCEPTOS_INGRESO_BORRADO);
		db.execSQL(scdb.INSERT_INTO_CONCEPTOS_GASTO_BORRADO);
		db.execSQL(scdb.INSERT_INTO_CONCEPTOS_INGRESO);
		db.execSQL(scdb.INSERT_INTO_CONCEPTOS_GASTO);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(DataBaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		// Borro el contenido de la BD
		db.execSQL("DROP TABLE IF EXISTS datos");
		db.execSQL("DROP TABLE IF EXISTS fechas");
		db.execSQL("DROP TABLE IF EXISTS conceptos");
		
		// se debera programar una logica para migrar los datos a una nueva version y no perder datos
		
		onCreate(db);
		
	}

}
