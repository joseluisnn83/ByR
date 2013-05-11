package com.joseluisnn.singleton;

/*
 *  patrón de diseño singleton (instancia única) diseñado para restringir la creación de objetos pertenecientes a una clase 
 *  o el valor de un tipo a un único objeto.
 */
public class SingletonConfigurationSharedPreferences {
	
	private static SingletonConfigurationSharedPreferences INSTANCE = new SingletonConfigurationSharedPreferences();
	
	/*
	 * Me creo el valor constante para los nombres de los archivos de preferencias de la configuración
	 */
	public final String NOMBRE_ARCHIVO_CONFIGURACION = "preferences_configuration";
	public final String NOMBRE_ARCHIVO_CONFIGURACION_GRAFICAS = "preferences_configuration_graphics";
	/*
	 *  nombre de los campos claves de los datos de configuración de la aplicación
	 */
	public final String KEY_CUENTA = "key_cuenta_creada";
	public final String KEY_TIPO_CONFIGURACION = "key_tipo_config";
	public final String KEY_INFORME_POR_DEFECTO = "key_informe_por_defecto";
	/*
	 *  nombre de los campos claves de los datos de configuración de las gráficas
	 */
	 public final String KEY_PRIMER_ACCESO_CONFIG_GRAFICA = "primeracceso";
	 public final String KEY_LPTIPOGRAFICA = "lpTipoGrafica";
	 public final String KEY_LPVALORESGRAFICA = "lpValoresGrafica";
	 public final String KEY_PYEARS = "pYears";
	 public final String KEY_PMONTHS = "pMonths";
	 public final String KEY_PDAY = "pDay";
	 public final String KEY_CBPLINEINGRESOS = "cbpLineIngresos";
	 public final String KEY_CBPLINEGASTOS = "cbpLineGastos";
	 public final String KEY_CBPLINEBALANCE = "cbpLineBalance";
	 public final String KEY_CBPVALUESINGRESOS = "cbpValuesIngresos";
	 public final String KEY_CBPVALUESGASTOS = "cbpValuesGastos";
	 public final String KEY_CBPVALUESBALANCE = "cbpValuesBalance";
	
	public SingletonConfigurationSharedPreferences(){}
	
	/*
	 * creador sincronizado para protegerse de posibles problemas  multi-hilo otra prueba para evitar 
	 * instanciación múltiple 
	 */
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new SingletonConfigurationSharedPreferences();
        }
    }
    
    public static SingletonConfigurationSharedPreferences getInstance() {
        createInstance();
        return INSTANCE;
    }

}
