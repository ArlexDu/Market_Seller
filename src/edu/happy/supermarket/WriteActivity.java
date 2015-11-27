package edu.happy.supermarket;


import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import edu.happy.tools.FileTools;
import edu.happy.tools.Goods;

public class WriteActivity extends Activity {

	private EditText id_text,name_text,class_text,protime_tex,expirytime_text,place_text,price_text,nutrition_text;
	private Button ok;
	private ImageView icon;
	private final int SELECT_PIC = 1;
	private final int SET_PIC = 2;
	private Bitmap bitmap = null;;
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
		icon = (ImageView)findViewById(R.id.good_icon);
		ok = (Button) findViewById(R.id.done);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 switch(v.getId()){
		    case R.id.good_icon://µã»÷Í¼Æ¬£¬¸Ä±äÍ¼±ê
			    SelectPicAfterKitKat();
			 break;
	        case R.id.done://µã»÷ok°´Å¥
	        	new FileTools().SaveIcon(this, bitmap, id_text.getText().toString());
	        	Intent intent = new Intent();
	        	String name = name_text.getText().toString();
	        	String id = id_text.getText().toString();
	        	String text = id+"#"+name+"#"+
	        			class_text.getText().toString()+"#"+protime_tex.getText().toString()+"#"+
	        			expirytime_text.getText().toString()+"#"+place_text.getText().toString()+"#"+
	        			price_text.getText().toString()+"#"+nutrition_text.getText().toString();
	        	System.out.println("input text is "+text);
	        	intent.putExtra("text", text);
	        	intent.putExtra("name", name);
	        	intent.putExtra("id", id);
				setResult(1,intent);
				finish();
				break;	
	        	}
		}
	
	
	private void SelectPicAfterKitKat(){
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
	    intent.addCategory(intent.CATEGORY_OPENABLE);
	    intent.setType("image/*");
	    startActivityForResult(intent, SELECT_PIC);
	}
	
	
	private void CropImageAfterKitKat(Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/jpeg");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("nofaceDetection", true);
		startActivityForResult(intent, SET_PIC);
	}
	//ÖØÐ´·µ»Ø¼ü£¬±ÜÃâ·µ»Ø¿ÕÖ¸Õë´íÎó
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
	private String getPath(Context context,Uri uri){
        // MediaProvider  
            final String docId = DocumentsContract.getDocumentId(uri);  
            System.out.println("return image uri :"+docId);//image:id
            final String[] split = docId.split(":");  
            final String type = split[0];  
  
            Uri contentUri = null;  
            if ("image".equals(type)) {  
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
            } 
            final String selection = "_id=?";  
            final String[] selectionArgs = new String[] {  
                    split[1]  
            };  
  
            Cursor cursor = null;  
            final String column = "_data";  
            final String[] projection = {  
                    column  
            };  
          
            try {  
                cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs,  
                        null);  
                if (cursor != null && cursor.moveToFirst()) {  
                    final int index = cursor.getColumnIndexOrThrow(column);  
                    return cursor.getString(index);  
                }  
            } finally {  
                if (cursor != null)  
                    cursor.close();  
            }  
            return null;
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		 case SELECT_PIC:
			 if(resultCode == RESULT_OK && data != null){
				 String realuri = getPath(this,data.getData());
				 System.out.println("real image uri :"+realuri);
				 CropImageAfterKitKat(Uri.fromFile(new File(realuri)));
			 }
			 break;
		 case SET_PIC:
			 bitmap = data.getParcelableExtra("data");
			 //±£´æÍ¼Æ¬
			 icon.setImageBitmap(bitmap);
			 break;
			
		}
	}
}
