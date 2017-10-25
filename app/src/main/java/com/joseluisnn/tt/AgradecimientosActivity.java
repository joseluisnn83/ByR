package com.joseluisnn.tt;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AgradecimientosActivity extends Activity {
	
	private TextView agradecimientos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_agradecimientos);

		// Modifico el tipo de fuente de los TextView
		// Variable para el tipo tipo de fuente
		Typeface fuente_text = Typeface.createFromAsset(getAssets(),
				"fonts/FreedanFont.ttf");
		
		agradecimientos = (TextView)findViewById(R.id.agradecimientos_textview_cabecera);
				
		agradecimientos.setTypeface(fuente_text);
		
	}

}
