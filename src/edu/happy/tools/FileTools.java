package edu.happy.tools;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;

public class FileTools {
	
	public FileTools() {
		// TODO Auto-generated constructor stub
	}
	
	
	//保存商品的标志图片
    public void SaveIcon(Context context,Bitmap Icon,String name){
			 File newimage = new File(context.getFilesDir()+"/GoodIcon/",name);
			 if(newimage.exists()){
				 newimage.delete();
			 }
			try {
				 FileOutputStream out = new FileOutputStream(newimage);
				 Icon.compress(Bitmap.CompressFormat.JPEG, 90, out);
				 out.flush();
				 out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

}
