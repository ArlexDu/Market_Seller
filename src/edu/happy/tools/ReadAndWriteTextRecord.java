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
 * 用于解析nfc标签的信息
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
	 * 从nfc标签中读信息
	 * NdEF 文本格式
	 * 偏移量 0  长度 1 状态字节
	 * 偏移量 1 长度  n 语言编码，这个n由状态字节的后六位指定
	 * 偏移量 n+1 长度 m 文本数据
	 * 
	 * 状态字节编码格式
	 * 字节位 7   数据：0/1 0表示utf-8编码，1表示utf-16编码
	 * 字节位 6   数据：默认值0
	 * 字节位0到5 数据 表示语言编码长度 
	 */
	private String parseinfromation(){
		//判断是否是ndef格式
		if(ndefRecord.getTnf()!=NdefRecord.TNF_WELL_KNOWN){
			return null;
		}
		if(!Arrays.equals(ndefRecord.getType(),NdefRecord.RTD_TEXT)){
			return null;
		}
		try{
			byte[] payload = ndefRecord.getPayload();
			//payload[0]获取第一个字节（8位），ox80二进制就是10000000想与操作的话呢就会判断字节的第一位是1还是0
			//根据ndef的格式，payload的第一个字节的最高位代表编码格式，1是utf-16,0是utf-8
			String textEncoding = ((payload[0]&0x80)== 0)?"UTF-8":"UTF-16";
//			payload[0]获取第一个字节（8位）他的后六位是编码长度，ox3f二进制就是00111111与操作的话呢就会获得字节的后六位
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
				int current = 0;//用于记录index数组的位置
				System.out.println("nfc info is "+info);
				//标签的信息例如：7733057006#丹麦香草威化饼干#食品#2014/10/29#2016/01/29#印尼#23#19.90
				String id,name,deadline,price;//获得商品的编号，名字，截止日期，价格
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
				good_price.setText("价格："+price);
				  //Date或者String转化为时间戳
			    SimpleDateFormat format =  new SimpleDateFormat("yyyy/MM/dd");
			    Date date;
				try {
					date = format.parse(deadline);
					long till_time = date.getTime();
					long current_time = System.currentTimeMillis();
		//			System.out.print("Format To times:"+date.getTime());
					if(current_time>till_time){
						deadline="这个商品已经过期!";
						good_time.setText(deadline);
					}else{
						good_time.setText("过期日期："+deadline);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//写入数据库
				database.RecordGood(good);
				return contentView;
			}
			
		};
		
		return message;
	}
	/*
	 * 向nfc标签中写入信息
	 * 
	 */
	private void createTextRecord(){
		byte[] langbytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
		Charset utfEncoding = Charset.forName("UTF-8");
		byte[] textBytes = mText.getBytes(utfEncoding);
		int utfbit = 0;
		char status =(char)(utfbit+langbytes.length);
		byte[] data = new byte[1+langbytes.length+textBytes.length];
		//写入状态 字节
		data[0] = (byte)status;
//		写入编码字节
		System.arraycopy(langbytes, 0, data, 1, langbytes.length);
//		写入实际数据
		System.arraycopy(textBytes, 0, data, 1+langbytes.length, textBytes.length);
		ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		
	}
}
