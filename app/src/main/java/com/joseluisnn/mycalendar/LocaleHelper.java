package com.joseluisnn.mycalendar;

import android.content.Context;

/**
* 
* @author joseluisnn
*
*/
class LocaleHelper {
	/**
	 * is current locale - español.
	 * @param context context.
	 * @return is español.
*/
	public static boolean isSpanishLocale(Context context) {
		return context.getResources().getConfiguration().locale.getLanguage().equals("es");
	}
}
