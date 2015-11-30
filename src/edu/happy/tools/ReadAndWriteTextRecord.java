package edu.happy.tools;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import edu.happy.supermarket.R;
/*
 * ���ڽ���nfc��ǩ����Ϣ
 */
public class ReadAndWriteTextRecord {

	private String mText;
	private NdefRecord ndefRecord;
	private String info; 
	public ReadAndWriteTextRecord(NdefRecord record) {
		// TODO Auto-generated constructor stub
		ndefRecord =record;
		info = parseinfromation();
	}
	
	public ReadAndWriteTextRecord(String text){
		mText =text;
		createTextRecord();
	}
	
	public String getText(){
		return mText;
	}
	public NdefRecord getNdefRecode(){
		return ndefRecord;
	}
	/*
	 * ��nfc��ǩ�ж���Ϣ
	 * NdEF �ı���ʽ
	 * ƫ���� 0  ���� 1 ״̬�ֽ�
	 * ƫ���� 1 ����  n ���Ա��룬���n��״̬�ֽڵĺ���λָ��
	 * ƫ���� n+1 ���� m �ı�����
	 * 
	 * ״̬�ֽڱ����ʽ
	 * �ֽ�λ 7   ���ݣ�0/1 0��ʾutf-8���룬1��ʾutf-16����
	 * �ֽ�λ 6   ���ݣ�Ĭ��ֵ0
	 * �ֽ�λ0��5 ���� ��ʾ���Ա��볤�� 
	 */
	private String parseinfromation(){
		//�ж��Ƿ���ndef��ʽ
		if(ndefRecord.getTnf()!=NdefRecord.TNF_WELL_KNOWN){
			return null;
		}
		if(!Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_TEXT)){
			return null;
		}
		try{
			byte[] payload = ndefRecord.getPayload();
			//payload[0]��ȡ��һ���ֽڣ�8λ����ox80�����ƾ���10000000��������Ļ��ؾͻ��ж��ֽڵĵ�һλ��1����0
			//����ndef�ĸ�ʽ��payload�ĵ�һ���ֽڵ����λ��������ʽ��1��utf-16,0��utf-8
			String textEncoding = ((payload[0]&0x80)== 0)?"UTF-8":"UTF-16";
//			payload[0]��ȡ��һ���ֽڣ�8λ�����ĺ���λ�Ǳ��볤�ȣ�ox3f�����ƾ���00111111������Ļ��ؾͻ����ֽڵĺ���λ
			int languageCodeLength = payload[0] & 0x3f;
			
			String languageCode = new String(payload,1,languageCodeLength,"US-ASCII");
      //      System.out.println("languageCode is "+languageCode);			
			return new String(payload,languageCodeLength+1,payload.length - languageCodeLength - 1,textEncoding);
				
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public ParseNdefMessage getInformation(DataBaseControl database){
		
		ParseNdefMessage message = new ParseNdefMessage() {
			
			@Override
			public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset,DataBaseControl database) {
				// TODO Auto-generated method stub
				int[] indexs = new int[7];
				int current = 0;//���ڼ�¼index�����λ��
				System.out.println("nfc info is "+info);
				//��ǩ����Ϣ���磺7733057006#���������������#ʳƷ#2014/10/29#2016/01/29#ӡ��#23#19.90
				String id,name,deadline,price;//�����Ʒ�ı�ţ����֣���ֹ���ڣ��۸�
				for(int i = 0;i < info.length();i++){
					if(info.charAt(i)=='#'){
						indexs[current] = i;
						current++;
					}
				}
				Goods good = new Goods();
				id = info.substring(0,indexs[0]);
				name = info.substring(indexs[0]+1,indexs[1]);
				deadline = info.substring(indexs[3]+1, indexs[4]);
				price = info.substring(indexs[5]+1,indexs[6]);
				good.setId(id);
				good.setPrice(price);
		//		System.out.println(" id is "+id+"\n name is "+name+"\n price is "+price+"\n deadline is "+deadline);
				View contentView = inflater.inflate(R.layout.detect_goods, null);
				TextView good_name = (TextView)contentView.findViewById(R.id.dete_good_name);
				TextView good_number = (TextView)contentView.findViewById(R.id.dete_good_number);
				TextView good_price = (TextView)contentView.findViewById(R.id.dete_good_price);
				TextView good_time = (TextView)contentView.findViewById(R.id.dete_good_time);
				ImageView good_icon = (ImageView)contentView.findViewById(R.id.detect_good_pic);
				String filepath = activity.getFilesDir()+"/GoodIcon/"+id;
		//		System.out.println("file path is "+ filepath);
				File file = new File(filepath);
				Bitmap goodicon;
				if(file.exists()){
					goodicon = BitmapFactory.decodeFile(filepath);
				}else{
					goodicon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.good);
				}
				good_icon.setImageBitmap(goodicon);
				good_name.setText(name);
				good_number.setText("1");
//				good_number.getId();
				System.out.println("id is "+good_number.getId());
				good_price.setText("�۸�"+price);
				  //Date����Stringת��Ϊʱ���
			    SimpleDateFormat format =  new SimpleDateFormat("yyyy/MM/dd");
			    Date date;
				try {
					date = format.parse(deadline);
					long till_time = date.getTime();
					long current_time = System.currentTimeMillis();
		//			System.out.print("Format To times:"+date.getTime());
					if(current_time>till_time){
						deadline="�����Ʒ�Ѿ�����!";
						good_time.setText(deadline);
					}else{
						good_time.setText("�������ڣ�"+deadline);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//д�����ݿ�
				database.RecordGood(good);
				return contentView;
			}
			
		};
		
		return message;
	}
	/*
	 * ��nfc��ǩ��д����Ϣ
	 * 
	 */
	private void createTextRecord(){
		byte[] langbytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEncoding = Charset.forName("UTF-8");
		byte[] textBytes = mText.getBytes(utfEncoding);
		int utfbit = 0;
		char status =(char)(utfbit+langbytes.length);
		byte[] data = new byte[1+langbytes.length+textBytes.length];
		//д��״̬ �ֽ�
		data[0] = (byte)status;
//		д������ֽ�
		System.arraycopy(langbytes, 0, data, 1, langbytes.length);
//		д��ʵ������
		System.arraycopy(textBytes, 0, data, 1+langbytes.length, textBytes.length);
		ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		
	}
}
