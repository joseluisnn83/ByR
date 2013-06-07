package com.joseluisnn.tt;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AcercaDeActivity extends Activity {
	
	private TextView title;
	private TextView version;
	private TextView copyright;
	private TextView derechos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acerca_de);

		// Modifico el tipo de fuente de los TextView
		// Variable para el tipo tipo de fuente
		Typeface fuente_title = Typeface.createFromAsset(getAssets(),
				"fonts/KidFromHell.ttf");
		Typeface fuente_text = Typeface.createFromAsset(getAssets(),
				"fonts/FreedanFont.ttf");
		
		title = (TextView)findViewById(R.id.AcercaDe_title);
		version = (TextView)findViewById(R.id.AcercaDe_version);
		copyright = (TextView)findViewById(R.id.AcercaDe_copyright);
		derechos = (TextView)findViewById(R.id.AcercaDe_derechos);
		
		title.setTypeface(fuente_title);
		version.setTypeface(fuente_text);
		copyright.setTypeface(fuente_text);
		derechos.setTypeface(fuente_text);

	}

}
