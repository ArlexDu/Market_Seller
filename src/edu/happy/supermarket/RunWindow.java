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
	
	private ImageView select; //����ѡ��İ�ť
	private ImageView writeUri;//��������д�����д��Uri
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
	private TextView show_title;
	private String id,name; //���ڼ�¼д��������ص�id��name
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

	        //û��nfcӲ������
	        if (mNfcAdapter == null) {
	            showMessage(R.string.error, R.string.no_nfc);
	            return;
	        }
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
	}
	
	//����һ�ε�½�Ż�����ݿ⽨��
	private void firstuse(){
		SharedPreferences preferences = getSharedPreferences("first", Activity.MODE_PRIVATE);
		int first = preferences.getInt("open", 0);
		System.out.println("open ��"+first);
		//��һ�ε�½  
		if(first == 0){
			//�������ݿ�
			DataBaseControl control = new DataBaseControl(this);
			control.CloseDataBase();
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
		}
		SharedPreferences.Editor editor = preferences.edit();
		first++;
		editor.putInt("open", first);
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
//		�鿴������Ʒͳ����Ϣ
		case R.id.button_lookgoods:
			intent = new Intent(RunWindow.this,AllGoodsInfoListActivity.class);
			startActivity(intent);
			break;
//		д��uri�İ�ť
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
		   if(resultCode == 1){//�����ı�
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
		//System.out.println("��ǰ�ĸ�ʽ�ǣ�"+intent.getAction());
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
									ParseNdefMessage message = textRecord.getInformation();
//									System.out.println("����µ������");
									show_info = message.getView(RunWindow.this, inflater, info_layout, 0);
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
				data.add_Whole_Data(id,name);//��ӵ����ݿ�
				data.CloseDataBase();
				Toast.makeText(this, "�ɹ�д�룡", Toast.LENGTH_LONG).show();
			}else{//��ʽ����ǩ��Ϊndef��ʽ
//				tag����nfc��ǩ����Ļ�����Ϣ��nfc��ǩ�ĸ�ʽû��ʲô��ϵ
				NdefFormatable format = NdefFormatable.get(tag);
				if(format != null){//�����Ϊ�գ�����Ը�ʽ��Ϊndef��ʽ
					format.connect();
					//��ʽ����ͬʱҲ�����д��Ĳ���
					format.format(message);
					data.add_Whole_Data(id,name);//��ӵ����ݿ�
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
			if(msg.what == 0){
				bar.setVisibility(View.INVISIBLE);
				notice.setVisibility(View.INVISIBLE);
				show_title.setVisibility(View.VISIBLE);
				info_layout.addView(show_info);
			}
			
		};
	};
}

