package edu.happy.tools;


import com.android.volley.Request.Method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.happy.supermarket.MyApplication;

public class NetWorkAccess {
    public static String base_url = "http://10.60.42.70:3000";
	
	public void ChangeInfo(final String uri,final Handler handler,final int what,final Map<String, String> map){	
		String url = base_url+uri;
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
				return map;
			}
			
		};
		request.setTag("getinfo");
		MyApplication.Get_Queues().add(request);
	}
	
	//œ¬‘ÿÕº∆¨
	public void loadImage(final Context context,final String id,final String pic){
		String url = base_url+pic; 
		ImageRequest request = new ImageRequest(url, 
				new Listener<Bitmap>() {

					@Override
					public void onResponse(Bitmap response) {
						// TODO Auto-generated method stub
						new FileTools().SaveIcon(context,response, id);
					}
		        }, 150, 150, Config.RGB_565, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.e("net error", error.toString());
					}
				});
		MyApplication.Get_Queues().add(request);
	}
}
