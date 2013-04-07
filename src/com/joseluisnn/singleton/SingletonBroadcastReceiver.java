package com.joseluisnn.singleton;

/*
 *  patrón de diseño singleton (instancia única) diseñado para restringir la creación de objetos pertenecientes a una clase 
 *  o el valor de un tipo a un único objeto.
 */
public class SingletonBroadcastReceiver {
	
	private static SingletonBroadcastReceiver INSTANCE = new SingletonBroadcastReceiver();
	
	/*
	 * Me creo el valor constante para el Intent que me quiero crear
	 */
	public final String CAMBIO_CONFIGURACION = "com.joseluisnn.byr.CONFIGURATION_UPDATE";
	public final String CAMBIO_FECHA_CALENDARIO_COMBO = "com.joseluisnn.byr.DATE_CHANGE_COMBO";
	public final String CAMBIO_FECHA_CALENDARIO_MYCALENDAR = "com.joseluisnn.byr.DATE_CHANGE_MYCALENDAR";
	
	public SingletonBroadcastReceiver(){}
	
	/*
	 * creador sincronizado para protegerse de posibles problemas  multi-hilo otra prueba para evitar 
	 * instanciación múltiple 
	 */
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new SingletonBroadcastReceiver();
        }
    }
    
    public static SingletonBroadcastReceiver getInstance() {
        createInstance();
        return INSTANCE;
    }

}
