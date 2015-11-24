package edu.happy.tools;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import android.nfc.NdefRecord;
/*
 * ���ڽ���nfc��ǩ����Ϣ
 */
public class ReadAndWriteTextRecord {

	private String mText;
	private NdefRecord ndefRecord;
	 
	public ReadAndWriteTextRecord(NdefRecord record) {
		// TODO Auto-generated constructor stub
		ndefRecord =record;
		parseinfromation();
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
	private void parseinfromation(){
		//�ж��Ƿ���ndef��ʽ
		if(ndefRecord.getTnf()!=NdefRecord.TNF_WELL_KNOWN){
			return;
		}
		if(!Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_TEXT)){
			return;
		}
		try{
			byte[] payload = ndefRecord.getPayload();
			//payload[0]��ȡ��һ���ֽڣ�8λ����ox80�����ƾ���10000000��������Ļ��ؾͻ��ж��ֽڵĵ�һλ��1����0
			//����ndef�ĸ�ʽ��payload�ĵ�һ���ֽڵ����λ��������ʽ��1��utf-16,0��utf-8
			String textEncoding = ((payload[0]&0x80)== 0)?"UTF-8":"UTF-16";
//			payload[0]��ȡ��һ���ֽڣ�8λ�����ĺ���λ�Ǳ��볤�ȣ�ox3f�����ƾ���00111111������Ļ��ؾͻ����ֽڵĺ���λ
			int languageCodeLength = payload[0] & 0x3f;
			
			String languageCode = new String(payload,1,languageCodeLength,"US-ASCII");
            System.out.println("languageCode is "+languageCode);			
			mText = new String(payload,languageCodeLength+1,payload.length - languageCodeLength - 1,textEncoding);
				
		}catch(Exception e){
			e.printStackTrace();
		}
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
