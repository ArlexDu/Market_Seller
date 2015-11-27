package edu.happy.supermarket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import edu.happy.tools.DataBaseControl;
import edu.happy.tools.FileTools;
import edu.happy.tools.ParseNdefMessage;
import edu.happy.tools.ReadAndWriteTextRecord;

public class RunWindow extends Activity {
	
	private ImageView select; //用于选择的按钮
	private ImageView writeUri;//用于跳入写入界面写入Uri
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
	private TextView show_title;
	private String id,name; //用于记录写入操作返回的id和name
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_layout);
		firstuse();
		select = (ImageView)findViewById(R.id.button_lookgoods);
		writeUri = (ImageView) findViewById(R.id.button_select_uri);
		notice = (ImageView) findViewById(R.id.notice);
		mNfcAdapter =NfcAdapter.getDefaultAdapter(this);
		info_layout = (LinearLayout)findViewById(R.id.show_infor);
		bar = (ProgressBar)findViewById(R.id.progressBar);
		show_title = (TextView) findViewById(R.id.show_title);
		inflater = LayoutInflater.from(this);
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
		SharedPreferences preferences = getSharedPreferences("first", Activity.MODE_PRIVATE);
		int first = preferences.getInt("open", 0);
		System.out.println("open ："+first);
		//第一次登陆  
		if(first == 0){
			//创建数据库
			DataBaseControl control = new DataBaseControl(this);
			control.CloseDataBase();
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
		}
		SharedPreferences.Editor editor = preferences.edit();
		first++;
		editor.putInt("open", first);
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
//		查看卖出商品统计信息
		case R.id.button_lookgoods:
			intent = new Intent(RunWindow.this,AllGoodsInfoListActivity.class);
			startActivity(intent);
			break;
//		写入uri的按钮
		case R.id.button_select_uri:
			intent = new Intent(RunWindow.this,WriteActivity.class);
			startActivityForResult(intent, 1);
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
		case 1:
		   if(resultCode == 1){//处理文本
				mdata = data.getStringExtra("text").toString();
				id = data.getStringExtra("id").toString();
				name = data.getStringExtra("name").toString();
				System.out.println("text is "+ mdata);
				method = 2;
				nodata();
				break;	
			}
		}
	}
	
	private void nodata(){
		if(mdata.equals("")){
			mdata = null;
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
		//System.out.println("当前的格式是："+intent.getAction());
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
									ParseNdefMessage message = textRecord.getInformation();
//									System.out.println("获得新的组件！");
									show_info = message.getView(RunWindow.this, inflater, info_layout, 0);
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
				data.add_Whole_Data(id,name);//添加到数据库
				data.CloseDataBase();
				Toast.makeText(this, "成功写入！", Toast.LENGTH_LONG).show();
			}else{//格式化标签变为ndef格式
//				tag描述nfc标签里面的基本信息和nfc标签的格式没有什么关系
				NdefFormatable format = NdefFormatable.get(tag);
				if(format != null){//如果不为空，则可以格式化为ndef格式
					format.connect();
					//格式化的同时也完成了写入的操作
					format.format(message);
					data.add_Whole_Data(id,name);//添加到数据库
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
			if(msg.what == 0){
				bar.setVisibility(View.INVISIBLE);
				notice.setVisibility(View.INVISIBLE);
				show_title.setVisibility(View.VISIBLE);
				info_layout.addView(show_info);
			}
			
		};
	};
}

