package edu.happy.tools;


import com.android.volley.Request.Method;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.happy.supermarket.MyApplication;

public class NetWorkAccess {
    private static String base_url = "http://10.60.42.70:3000";
	
	public void GetInfo(final long updatetime,final Handler handler,final int what){	
		String url = base_url+"/android/updateinfo";
		StringRequest request = new StringRequest(Method.POST, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
//						Log.i("net", response);
						Message message = new Message();
						message.what = what;
						message.obj = response;
						handler.sendMessage(message);
					}
		       }, new Response.ErrorListener() {

			       @Override
			       public void onErrorResponse(VolleyError error) {
				      // TODO Auto-generated method stub
				        Log.e("net error", error.toString());
			   }
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				Log.i("updatetime is ", String.valueOf(updatetime));
				map.put("updatetime", String.valueOf(updatetime));
				return map;
			}
		};
		request.setTag("getinfo");
		MyApplication.Get_Queues().add(request);
	}
	
	//œ¬‘ÿÕº∆¨
	public void loadImage(final Context context,final String id){
		String url = base_url+"/image/"+id+".jpg"; 
		ImageRequest request = new ImageRequest(url, 
				new Listener<Bitmap>() {

					@Override
					public void onResponse(Bitmap response) {
						// TODO Auto-generated method stub
						new FileTools().SaveIcon(context,response, id);
					}
		        }, 320, 320, Config.RGB_565, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("net error", error.toString());
					}
				});
		MyApplication.Get_Queues().add(request);
	}
}
