package edu.happy.mynfcapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import edu.happy.mynfcapp.R;
import edu.happy.tools.ReadInformation;
import edu.happy.tools.ReadUriRecord;
import edu.happy.tools.ReadAndWriteTextRecord;

public class RunWindow extends Activity {
	
	private ImageView select; //����ѡ��İ�ť
	private ImageView writeUri;//��������д�����д��Uri
	private String mdata;//�洢�����������
	private NfcAdapter mNfcAdapter;
	private AlertDialog mDialog;
	private PendingIntent mPendingIntent;
	private int method;//��ʾ��ǰ�Ĺ���
	private TextView showinformation;//����չʾnfc��ǩ������
	private ProgressBar bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_layout);
		
		select = (ImageView)findViewById(R.id.button_select_app);
		writeUri = (ImageView) findViewById(R.id.button_select_uri);
		showinformation = (TextView) findViewById(R.id.showinformation);
		bar = (ProgressBar)findViewById(R.id.progressBar);
		mNfcAdapter =NfcAdapter.getDefaultAdapter(this);
	    mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

	        //û��nfcӲ������
	        if (mNfcAdapter == null) {
	            showMessage(R.string.error, R.string.no_nfc);
	            return;
	        }
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()), 0);
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
//		ѡ��app�İ�ť
		case R.id.button_select_app:
			intent = new Intent(RunWindow.this,IntstalledApplicationListActivity.class);
			startActivityForResult(intent, 0);
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
		String temp ;
		switch (requestCode){
		//Ӧ�ó����ѡ��
		case 0:
			temp = data.getStringExtra("package_name").toString();
			mdata = temp.substring(temp.indexOf("\n")+1);
			String name = data.getStringExtra("appname");
			System.out.println("mpackageName is "+ name);
			showinformation.setText(name);
			method = 0;
			nodata();
			break;
		//��ҳ��д��
		case 1:
			if(resultCode == 0){//������ҳ
				temp = data.getStringExtra("Uri").toString();
				mdata = "http://"+temp; 
				System.out.println("Uri is "+ mdata);
				showinformation.setText(mdata);
				method = 1;
				nodata();
				break;	
			}else if(resultCode == 1){//�����ı�
				mdata = data.getStringExtra("text").toString();
				System.out.println("text is "+ mdata);
				showinformation.setText(mdata);
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
                ReadInformation information = new ReadInformation();
                information.setTextType(ndef.getType()+"\n");
                information.setMaxsize(ndef.getMaxSize()+"\n");
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
                		boolean istext = false;
                		//��һ�ж������Ƿ�����֪���ͣ�����RTD_text��RTD_uri
                		if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN){
                			//������֪uri��ʽ
                			ReadUriRecord urirecord = new ReadUriRecord();
                			String uri = urirecord.ParseWellKnowUri(record);
                			if(uri == null){
                				istext = true;
                			}else{
                				information.setData(uri);
                				System.out.println("Type is uri ");	
                			}
                	
                			if (istext){//����text��ʽ
                				ReadAndWriteTextRecord textRecord = new ReadAndWriteTextRecord(record);
                        		information.setData(textRecord.getText()+"\n");
                        		System.out.println("Type is text ");
                			}
                			
                		}else if(record.getTnf() == NdefRecord.TNF_ABSOLUTE_URI){//��������uri��ʽ
                			ReadUriRecord urirecord = new ReadUriRecord();
            				String uri =urirecord.ParseAbsoluteUri(record);
            				information.setData(uri);
            				System.out.println("Type is aburi ");
                		}
                		showinformation.setText(information.getTextType()+information.getMaxsize()+information.getData());
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
				Toast.makeText(this, "�ɹ�д�룡", Toast.LENGTH_LONG).show();
			}else{//��ʽ����ǩ��Ϊndef��ʽ
//				tag����nfc��ǩ����Ļ�����Ϣ��nfc��ǩ�ĸ�ʽû��ʲô��ϵ
				NdefFormatable format = NdefFormatable.get(tag);
				if(format != null){//�����Ϊ�գ�����Ը�ʽ��Ϊndef��ʽ
					format.connect();
					//��ʽ����ͬʱҲ�����д��Ĳ���
					format.format(message);
					Toast.makeText(this, "�ɹ�д�룡", Toast.LENGTH_LONG).show();	
				}else{
					Toast.makeText(this, "�ñ�ǩ�޷���ʽ��Ϊndef��ʽ��", Toast.LENGTH_LONG).show();	
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "δ֪����", Toast.LENGTH_LONG).show();
		}
//		д��֮���Ҫд�����Ϣȥ��
		showinformation.setText("");
		mdata = null;
	}
}

