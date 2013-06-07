package com.joseluisnn.tt;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AgradecimientosActivity extends Activity {
	
	private TextView agradecimientos;
	/*private TextView version;
	private TextView copyright;
	private TextView derechos;*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_agradecimientos);

		// Modifico el tipo de fuente de los TextView
		// Variable para el tipo tipo de fuente
		/*Typeface fuente_title = Typeface.createFromAsset(getAssets(),
				"fonts/KidFromHell.ttf");*/
		Typeface fuente_text = Typeface.createFromAsset(getAssets(),
				"fonts/FreedanFont.ttf");
		
		agradecimientos = (TextView)findViewById(R.id.agradecimientos_textview_cabecera);
		//version = (TextView)findViewById(R.id.AcercaDe_version);
		//copyright = (TextView)findViewById(R.id.AcercaDe_copyright);
		///derechos = (TextView)findViewById(R.id.AcercaDe_derechos);
		
		agradecimientos.setTypeface(fuente_text);
		//version.setTypeface(fuente_text);
		//copyright.setTypeface(fuente_text);
		//derechos.setTypeface(fuente_text);

	}

}
