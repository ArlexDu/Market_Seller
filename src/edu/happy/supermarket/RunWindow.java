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
	
	private ImageView select,cancel,overview,add,black,price,done; //����ѡ��İ�ť
	private RelativeLayout dialog;
	private ImageView notice;
	private String mdata;//�洢�����������
	private NfcAdapter mNfcAdapter;
	private AlertDialog mDialog;
	private PendingIntent mPendingIntent;
	private int method;//��ʾ��ǰ�Ĺ���
	private ReadAndWriteTextRecord textRecord;
	private LinearLayout info_layout;
	private ProgressBar bar;
	private LayoutInflater inflater;
	private View show_info;
	private TextView show_title,count,money;
	private String id,name; //���ڼ�¼д��������ص�id��name
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
		//��һ�ε�½���ϵıȶԸ������µ���Ϣ
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

	        //û��nfcӲ������
	        if (mNfcAdapter == null) {
	            showMessage(R.string.error, R.string.no_nfc);
	            return;
	        }
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
	}
	
	//����һ�ε�½�Ż�����ݿ⽨��
	private void firstuse(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		int first = preferences.getInt("open", 0);
		System.out.println("open ��"+first);
		//��һ�ε�½  
		if(first == 0){
//			����image�ļ���,�趨Ĭ����ƷͼƬ
			File appFile = this.getFilesDir();
			String imagefile = "GoodIcon";
			File image = new File(appFile,imagefile);
			try {
				image.mkdir();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//�������ݿ�
			DataBaseControl control = new DataBaseControl(this);
			control.CloseDataBase();
		}
		SharedPreferences.Editor editor = preferences.edit();
		first++;
		editor.putInt("open", first);
		editor.putLong("updatetime", 0);
		editor.commit();
		
	}
	
	//  չʾû��nfc�Ĵ���
    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }
    
	//����¼��Ĵ�����
	public void onClick(View v){
		Intent intent;
		switch(v.getId()){
		//����ѡ��ģʽ
		case R.id.select_button:
			chooseFunction();
			break;
		case R.id.cancel_button:
			cancelSelect();
			break;
//		�鿴������Ʒͳ����Ϣ
		case R.id.overview:
			intent = new Intent(RunWindow.this,AllGoodsInfoListActivity.class);
			startActivity(intent);
//			 overridePendingTransition ����������startActivity()���� finish()�����ĺ��档
			overridePendingTransition(R.anim.in_from_left, R.anim.to_from_right);
			initanim();
			break;
//		д��uri�İ�ť
		case R.id.add:
			intent = new Intent(RunWindow.this,WriteActivity.class);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.in_form_right, R.anim.to_from_left);
			initanim();
			break;
//			��ʼ����
		case R.id.price_button:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = Message.obtain();
					msg.what = 1;
					// ���������Ϣ����Ϣ������
					myHander.sendMessage(msg);
				}
			}).start();
			break;
//			�������
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
	 //��ʼ������
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
	 //ѡ������ĵ���
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
	
	//ȡ��ѡ��Ķ���
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
		   if(resultCode == 1){//�����ı�
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
		//ʹ��ǰ���ڱ�����ȼ����
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
		if(mdata == null){//����ǩ
		//	System.out.println("����ǩ");
            readNfc(detectedTag,intent);          
		}else{//д��ǩ
			writeNfcTag(detectedTag);	
		}
	}
	private void readNfc(Tag tag,Intent intent){
		//�ж��Ƿ����ɶ�nfc��ǩ�򿪵Ĵ���
		//�Ѷ�ѡ�İ�ť���ص�
		if(select.getVisibility()==View.VISIBLE){
			select.setVisibility(View.INVISIBLE);
			cancel.setVisibility(View.INVISIBLE);
			price.setVisibility(View.VISIBLE);
		}
		//��֤һ�ν��������ʹ��ͬһ��database��
		    if(database == null){
		    	database = new DataBaseControl(this);
		    }
            if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            //	System.out.println("�����ж�");
            	Ndef ndef = Ndef.get(tag);
                Parcelable[] rawMgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage msg[] = null;
                int contentSize = 0;
                if(rawMgs != null){
                //	System.out.println("��Ϊnull");
                	msg = new NdefMessage[rawMgs.length];
                	for(int i =0; i<rawMgs.length ;i++){
                		msg[i] = (NdefMessage)rawMgs[i];
                		contentSize+=msg[i].toByteArray().length;
                	}
                }
                try{
                	
                	if(msg !=null){
                		//һ�������ֻ��һ��ndefmessage��ndefrecord
                		NdefRecord record = msg[0].getRecords()[0];
                		//��һ�ж������Ƿ�����֪���ͣ�����RTD_text��RTD_uri
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
//									System.out.println("����µ������");
									show_info = message.getView(RunWindow.this, inflater, info_layout, 0,database);
									Message msg = Message.obtain();
									msg.what = 0;
									// ���������Ϣ����Ϣ������
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
					Toast.makeText(this, "���NFC��ǩ�ǲ�����д��ģ�", Toast.LENGTH_LONG).show();
				}
				if(ndef.getMaxSize()<size){
					Toast.makeText(this, "���NFC��ǩ����������", Toast.LENGTH_LONG).show();
				}
				ndef.writeNdefMessage(message);
				data.add_Whole_Data(id,name,0);//��ӵ����ݿ�
				data.CloseDataBase();
				Toast.makeText(this, "�ɹ�д�룡", Toast.LENGTH_LONG).show();
			}else{//��ʽ����ǩ��Ϊndef��ʽ
//				tag����nfc��ǩ����Ļ�����Ϣ��nfc��ǩ�ĸ�ʽû��ʲô��ϵ
				NdefFormatable format = NdefFormatable.get(tag);
				if(format != null){//�����Ϊ�գ�����Ը�ʽ��Ϊndef��ʽ
					format.connect();
					//��ʽ����ͬʱҲ�����д��Ĳ���
					format.format(message);
					data.add_Whole_Data(id,name,0);//��ӵ����ݿ�
					data.CloseDataBase();
					Toast.makeText(this, "�ɹ�д�룡", Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(this, "�ñ�ǩ�޷���ʽ��Ϊndef��ʽ��", Toast.LENGTH_LONG).show();	
				}
				id = name = null;
			}
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "δ֪����", Toast.LENGTH_LONG).show();
		}
//		д��֮���Ҫд�����Ϣȥ��
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
			case 2://��ò�Ʒ���ݣ��������ݿ�
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
						data.add_Whole_Data(id,name,number);//��ӵ����ݿ�
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
			case 3://�������ݿ��ͼƬ
//				System.out.println("ͼƬ�������");
				break;
			}
		};
	};
	
	private long Max(long x,long y){
		return x>y?x:y;
	}
	//��д���ؼ������ⷵ�ؿ�ָ�����
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

