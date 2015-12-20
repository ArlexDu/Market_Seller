package edu.happy.tools;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.happy.supermarket.MyApplication;

public class NetWorkAccess {
    
	
	public void Volly_Get(final Handler handler,final int what){
		
		String url = "10.60.42.70:3000/android/updateinfo";
		StringRequest request = new StringRequest(Method.GET, url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.i("net", response);
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
		
		});
		request.setTag("getinfo");
		MyApplication.Get_Queues().add(request);
	}
}
