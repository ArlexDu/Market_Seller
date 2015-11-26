package edu.happy.supermarket;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class WriteActivity extends Activity {

	private EditText id_text,name_text,class_text,protime_tex,expirytime_text,place_text,price_text,nutrition_text;
	private Button ok;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write);
		id_text = (EditText) findViewById(R.id.newgood_id);
		name_text = (EditText) findViewById(R.id.newgood_name);
		class_text = (EditText) findViewById(R.id.newgood_class);
		protime_tex = (EditText) findViewById(R.id.newgood_pro_time);
		expirytime_text = (EditText) findViewById(R.id.newgood_expirytime);
		place_text = (EditText) findViewById(R.id.newgood_place);
		price_text = (EditText) findViewById(R.id.newgood_price);
		nutrition_text = (EditText) findViewById(R.id.newgood_nutrition);
		ok = (Button) findViewById(R.id.done);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 switch(v.getId()){
	        case R.id.done:
	        	Intent intent = new Intent();
	        	String text = id_text.getText().toString()+"#"+name_text.getText().toString()+"#"+
	        			class_text.getText().toString()+"#"+protime_tex.getText().toString()+"#"+
	        			expirytime_text.getText().toString()+"#"+place_text.getText().toString()+"#"+
	        			price_text.getText().toString()+"#"+nutrition_text.getText().toString();
	        	System.out.println("input text is "+text);
	        	intent.putExtra("text", text);
				setResult(1,intent);
				finish();
				break;	
	        	}
		}
	
	//÷ÿ–¥∑µªÿº¸£¨±‹√‚∑µªÿø’÷∏’Î¥ÌŒÛ
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
        	String text = "";
			intent.putExtra("text", text);
			setResult(1,intent);
			finish();
			return true;
		}else{
			return super.onKeyDown(keyCode, event);	
		}
	}
}
