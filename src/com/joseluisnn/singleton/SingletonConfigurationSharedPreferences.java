package com.joseluisnn.singleton;

/*
 *  patrón de diseño singleton (instancia única) diseñado para restringir la creación de objetos pertenecientes a una clase 
 *  o el valor de un tipo a un único objeto.
 */
public class SingletonConfigurationSharedPreferences {
	
	private static SingletonConfigurationSharedPreferences INSTANCE = new SingletonConfigurationSharedPreferences();
	
	/*
	 * Me creo el valor constante para el nombre del archivo de preferencias de la configuración
	 */
	public final String NOMBRE_ARCHIVO_CONFIGURACION = "preferences_configuration";
	/*
	 *  nombre de los campos claves de los datos personales
	 */
	public final String KEY_CUENTA = "key_cuenta_creada";
	public final String KEY_TIPO_CONFIGURACION = "key_tipo_config";
	public final String KEY_INFORME_POR_DEFECTO = "key_informe_por_defecto";
	
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
