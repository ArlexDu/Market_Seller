package edu.happy.supermarket;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.happy.tools.DataBaseControl;
import edu.happy.tools.NetWorkAccess;
import edu.happy.tools.ParseNdefMessage;
import edu.happy.tools.ReadAndWriteTextRecord;

public class RunWindow extends Activity {
	
	private ImageView select,cancel,overview,add,black,price,done; //用于选择的按钮
	private RelativeLayout dialog;
	private ImageView notice;
	private String mdata;//存储程序包的名字
	private NfcAdapter mNfcAdapter;
	private AlertDialog mDialog;
	private PendingIntent mPendingIntent;
	private int method;//表示当前的功能
	private ReadAndWriteTextRecord textRecord;
	private LinearLayout info_layout;
	private ProgressBar bar;
	private LayoutInflater inflater;
	private View show_info;
	private TextView show_title,count,money;
	private String id,name; //用于记录写入操作返回的id和name
	private DataBaseControl database = null;
	private NetWorkAccess netaccess;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_layout);
		firstuse();
		select = (ImageView)findViewById(R.id.select_button);
		cancel = (ImageView)findViewById(R.id.cancel_button);
		black = (ImageView)findViewById(R.id.black);
		add = (ImageView) findViewById(R.id.add);
		overview =(ImageView)findViewById(R.id.overview);
		notice = (ImageView) findViewById(R.id.notice);
		mNfcAdapter =NfcAdapter.getDefaultAdapter(this);
		info_layout = (LinearLayout)findViewById(R.id.show_infor);
		bar = (ProgressBar)findViewById(R.id.progressBar);
		show_title = (TextView) findViewById(R.id.show_title);
		done = (ImageView)findViewById(R.id.done_button);
		price = (ImageView)findViewById(R.id.price_button);
		dialog = (RelativeLayout)findViewById(R.id.dialog);
		count = (TextView)findViewById(R.id.dialog_count);
		money = (TextView)findViewById(R.id.dialog_price);
		inflater = LayoutInflater.from(this);
		//第一次登陆不断的比对更新最新的信息
		netaccess = new NetWorkAccess();
 		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences preferences = getSharedPreferences("first", Activity.MODE_PRIVATE);
				long updatetime = preferences.getLong("updatetime", 0);
				netaccess.GetInfo(updatetime,myHander,2);
			}
		}).start();
	    mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

	        //没有nfc硬件服务
	        if (mNfcAdapter == null) {
	            showMessage(R.string.error, R.string.no_nfc);
	            return;
	        }
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
	}
	
	//仅第一次登陆才会打开数据库建立
	private void firstuse(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int first = preferences.getInt("open", 0);
		System.out.println("open ："+first);
		//第一次登陆  
		if(first == 0){
//			创建image文件夹,设定默认商品图片
			File appFile = this.getFilesDir();
			String imagefile = "GoodIcon";
			File image = new File(appFile,imagefile);
			try {
				image.mkdir();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//创建数据库
			DataBaseControl control = new DataBaseControl(this);
			control.CloseDataBase();
		}
		SharedPreferences.Editor editor = preferences.edit();
		first++;
		editor.putInt("open", first);
		editor.putLong("updatetime", 0);
		editor.commit();
		
	}
	
	//  展示没有nfc的窗口
    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }
    
	//点击事件的处理方法
	public void onClick(View v){
		Intent intent;
		switch(v.getId()){
		//进入选择模式
		case R.id.select_button:
			chooseFunction();
			break;
		case R.id.cancel_button:
			cancelSelect();
			break;
//		查看卖出商品统计信息
		case R.id.overview:
			intent = new Intent(RunWindow.this,AllGoodsInfoListActivity.class);
			startActivity(intent);
//			 overridePendingTransition 方法必须在startActivity()或者 finish()方法的后面。
			overridePendingTransition(R.anim.in_from_left, R.anim.to_from_right);
			initanim();
			break;
//		写入uri的按钮
		case R.id.add:
			intent = new Intent(RunWindow.this,WriteActivity.class);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.in_form_right, R.anim.to_from_left);
			initanim();
			break;
//			开始结算
		case R.id.price_button:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = Message.obtain();
					msg.what = 1;
					// 发送这个消息到消息队列中
					myHander.sendMessage(msg);
				}
			}).start();
			break;
//			结算完成
		case R.id.done_button:
			show_title.setVisibility(View.INVISIBLE);
			info_layout.removeAllViews();
			notice.setVisibility(View.VISIBLE);
			database.Update_Detail_Data();
			database.Update_Whole_Data();
			dialog.setVisibility(View.INVISIBLE);
			database = null;
			select.setVisibility(View.VISIBLE);
			done.setVisibility(View.INVISIBLE);
			break;
		}
		
	}
	 //初始化界面
	 public void initanim(){
				cancel.clearAnimation();
				cancel.invalidate();
				black.clearAnimation();
				black.invalidate();
				black.setVisibility(View.GONE);
				overview.setVisibility(View.GONE);
				add.setVisibility(View.GONE);
				cancel.setVisibility(View.INVISIBLE);
				select.setVisibility(View.VISIBLE);
			}
	 //选择操作的弹框
	 private void chooseFunction(){
				black.setVisibility(View.VISIBLE);
				select.clearAnimation();
				select.invalidate();
				select.setVisibility(View.INVISIBLE);
				cancel.setVisibility(View.VISIBLE);
				cancel.bringToFront();
			    Animation rotateanimation=AnimationUtils.loadAnimation(RunWindow.this, R.anim.select_rotate);
				rotateanimation.setFillAfter(true);
				cancel.startAnimation(rotateanimation);
				rotateanimation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						overview.setVisibility(View.VISIBLE);
						add.setVisibility(View.VISIBLE);
					}
				});
				
			}
	
	//取消选择的动画
	public void cancelSelect(){
		cancel.clearAnimation();
		cancel.invalidate();
		overview.setVisibility(View.GONE);
		add.setVisibility(View.GONE);
		cancel.setVisibility(View.INVISIBLE);
		select.setVisibility(View.VISIBLE);
		select.bringToFront();
		Animation rotateanimation=AnimationUtils.loadAnimation(RunWindow.this, R.anim.cancel_rotate);
		rotateanimation.setFillAfter(true);
		rotateanimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub	
			    Animation animation=AnimationUtils.loadAnimation(RunWindow.this, R.anim.black_cancel);
//				animation.setFillAfter(true);
				black.startAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation arg0) {
						// TODO Auto-generated method stub
						black.setVisibility(View.GONE);
					}
				});
			}
		});
		select.startAnimation(rotateanimation);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
		case 1:
		   if(resultCode == 1){//处理文本
				mdata = data.getStringExtra("text").toString();
				System.out.println("text is "+ mdata);
				method = 2;
				if(mdata.equals("")){
					mdata = null;
				}else{
					id = data.getStringExtra("id").toString();
					name = data.getStringExtra("name").toString();
				}
				break;	
			}
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//使当前窗口变得优先级最高
		if(mNfcAdapter!=null){
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mNfcAdapter!=null){
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if(mdata == null){//读标签
		//	System.out.println("读标签");
            readNfc(detectedTag,intent);          
		}else{//写标签
			writeNfcTag(detectedTag);	
		}
	}
	private void readNfc(Tag tag,Intent intent){
		//判断是否是由读nfc标签打开的窗口
		//把多选的按钮隐藏掉
		if(select.getVisibility()==View.VISIBLE){
			select.setVisibility(View.INVISIBLE);
			cancel.setVisibility(View.INVISIBLE);
			price.setVisibility(View.VISIBLE);
		}
		//保证一次结算过程中使用同一个database库
		    if(database == null){
		    	database = new DataBaseControl(this);
		    }
            if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            //	System.out.println("进入判断");
            	Ndef ndef = Ndef.get(tag);
                Parcelable[] rawMgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage msg[] = null;
                int contentSize = 0;
                if(rawMgs != null){
                //	System.out.println("不为null");
                	msg = new NdefMessage[rawMgs.length];
                	for(int i =0; i<rawMgs.length ;i++){
                		msg[i] = (NdefMessage)rawMgs[i];
                		contentSize+=msg[i].toByteArray().length;
                	}
                }
                try{
                	
                	if(msg !=null){
                		//一般情况下只有一个ndefmessage和ndefrecord
                		NdefRecord record = msg[0].getRecords()[0];
                		//第一判断内容是否是已知类型，包括RTD_text和RTD_uri
                		if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN){
                			if(textRecord == null){
                				textRecord = new ReadAndWriteTextRecord(record);
                			}
                			bar.setVisibility(View.VISIBLE);
                			new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									ParseNdefMessage message = textRecord.getInformation(database);
//									System.out.println("获得新的组件！");
									show_info = message.getView(RunWindow.this, inflater, info_layout, 0,database);
									Message msg = Message.obtain();
									msg.what = 0;
									// 发送这个消息到消息队列中
									myHander.sendMessage(msg);
								}
							}).start();
                		}
                	}
                	
                }catch(Exception e){
                	e.printStackTrace();
                }
                
            }
	}
	
	private void writeNfcTag(Tag tag){
//		System.out.println("method is "+method);
		NdefMessage message;
		if(tag == null)
			return;
		switch(method){
		case 0:
		   message = new NdefMessage(new NdefRecord[]
		        		{NdefRecord.createApplicationRecord(mdata)});
		   WriteMessage(message, tag);
		   break;
		case 1:
		   message = new NdefMessage(new NdefRecord[]
					{NdefRecord.createUri(Uri.parse(mdata))});
		   WriteMessage(message, tag);
		   break;
		case 2:
			message = new NdefMessage(new NdefRecord[]{new ReadAndWriteTextRecord(mdata).getNdefRecode()});
			WriteMessage(message, tag);
			break;
		}
	}
	private void WriteMessage(NdefMessage message,Tag tag){
		int size = message.toByteArray().length;
		Ndef ndef = Ndef.get(tag);
		DataBaseControl data = new DataBaseControl(this);
		try{
			if(ndef!=null){
				ndef.connect();
				System.out.println("connect!!!");
				if(!ndef.isWritable()){
					Toast.makeText(this, "这个NFC标签是不可以写入的！", Toast.LENGTH_LONG).show();
				}
				if(ndef.getMaxSize()<size){
					Toast.makeText(this, "这个NFC标签容量不够！", Toast.LENGTH_LONG).show();
				}
				ndef.writeNdefMessage(message);
				data.add_Whole_Data(id,name,0);//添加到数据库
				data.CloseDataBase();
				Toast.makeText(this, "成功写入！", Toast.LENGTH_LONG).show();
			}else{//格式化标签变为ndef格式
//				tag描述nfc标签里面的基本信息和nfc标签的格式没有什么关系
				NdefFormatable format = NdefFormatable.get(tag);
				if(format != null){//如果不为空，则可以格式化为ndef格式
					format.connect();
					//格式化的同时也完成了写入的操作
					format.format(message);
					data.add_Whole_Data(id,name,0);//添加到数据库
					data.CloseDataBase();
					Toast.makeText(this, "成功写入！", Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(this, "该标签无法格式化为ndef格式！", Toast.LENGTH_LONG).show();	
				}
				id = name = null;
			}
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "未知错误！", Toast.LENGTH_LONG).show();
		}
//		写入之后把要写入的信息去掉
		mdata = null;
	}
	
    Handler myHander = new Handler(){
		
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				bar.setVisibility(View.INVISIBLE);
				notice.setVisibility(View.INVISIBLE);
				show_title.setVisibility(View.VISIBLE);
			    info_layout.addView(show_info);	
				break;
			case 1:
				price.setVisibility(View.INVISIBLE);
				done.setVisibility(View.VISIBLE);
				String c = database.getcount()+"";
				System.out.println("count is "+c);
				count.setText(c);
				String s= database.getprice()+"";
				System.out.println("price is "+s);
				money.setText(s);
				dialog.setVisibility(View.VISIBLE);
			    break;
			case 2://获得产品数据，更新数据库
				System.out.println(msg.obj);
				long lastupdate = 0;
				try {
					JSONArray array = new JSONArray(msg.obj.toString());
					for(int i= 0 ; i< array.length() ; i++){
						JSONObject good = (JSONObject) array.get(i);
						final String id = good.getString("id");
						String name = good.getString("name");
						int number = good.getInt("number");
						String updatetime = good.getString("updatetime");
						lastupdate = Max(lastupdate,Long.parseLong(updatetime));
						DataBaseControl data = new DataBaseControl(getApplicationContext());
						data.add_Whole_Data(id,name,number);//添加到数据库
						data.CloseDataBase();
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								netaccess = new NetWorkAccess();
								netaccess.loadImage(getApplicationContext(),id);
							}
						}).start();
					}
//			    System.out.println("lasttime is "+ lastupdate);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
				SharedPreferences.Editor editor = preferences.edit();
				editor.putLong("updatetime",lastupdate);
				editor.commit();
//				long updatetime = preferences.getLong("updatetime", 0);
//				 System.out.println("sp lasttime is "+ lastupdate);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3://更新数据库的图片
//				System.out.println("图片下载完成");
				break;
			}
		};
	};
	
	private long Max(long x,long y){
		return x>y?x:y;
	}
	//重写返回键，避免返回空指针错误
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode == KeyEvent.KEYCODE_HOME){
				keyCode = KeyEvent.KEYCODE_BACK;
				return super.onKeyDown(keyCode, event);	
			}else{
				return super.onKeyDown(keyCode, event);	
			}
		}
}

