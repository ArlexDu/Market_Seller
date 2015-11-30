package edu.happy.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import edu.happy.supermarket.R;

public class DataBaseControl {
	
	private DBHelper dbhelper;
	private SQLiteDatabase database;
	private static String INSERT_DETIAL="INSERT INTO Detail_info VALUES(?,?,?,?,?)";
	private static String INSERT_WHOLE="INSERT INTO Whole_info VALUES(?,?,?)";
	private long time;
	private Context con;
	//����һ�����Ѽ�¼��ǰ�ĵ�����������Ʒ
	private class Buygoods{
		public String id;
		public int number;
		public float price;
	}
	private List<Buygoods> goodslist = new ArrayList<Buygoods>();
	
	private boolean isnew =true;//�����ж�����ɨ�����Ʒ�ڱ���ɨ��ǰ�Ƿ��Ѿ�ɨ���
	public DataBaseControl(Context context) {
		// TODO Auto-generated constructor stub
		dbhelper = new DBHelper(context);
		database = dbhelper.getWritableDatabase();
		time = System.currentTimeMillis();
		SimpleDateFormat mformat = new SimpleDateFormat("yyyy:MM:dd HH:mm"); 
		String stime = mformat.format(new Date(time));
		con = context;
	//	System.out.println("time is "+stime);
	}
	
	public void RecordGood(Goods good){
		for(int i = 0; i< goodslist.size();i++){
			Buygoods bg = goodslist.get(i);
			System.out.println("list id is "+bg.id);
			if(good.getId().equals(bg.id)){
				bg.number++;
				isnew = false;
//				LayoutInflater inflater = LayoutInflater.from(con);
//				View contentView = inflater.inflate(R.layout.run_layout, null);
//				TextView good_number = (TextView)contentView.findViewWithTag(bg.id);
//				String number  = good_number.getText().toString();
//				int num = Integer.parseInt(number)+1;
//				good_number.setText(num);
				break;
			}else{
				isnew = true;
			}
			
		}
		if(isnew){//���µ���Ʒ�Ļ��������ݿ��м���һ��
	//		System.out.println("new");
			Buygoods nbg = new Buygoods();
			nbg.id = good.getId();
			nbg.number = 1;
			nbg.price = Float.parseFloat(good.getPrice());
			goodslist.add(nbg);	
			add_Detail_Data(good);
		}//���Ǳ��ν����µĵĻ����ڽ�������������е����ݿ���Ŀ�и�������
	}
	
	public void add_Whole_Data(String id,String name){
		database.beginTransaction();
		try{
			database.execSQL(INSERT_WHOLE,new Object[]{id,name,0});
			database.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            database.endTransaction();			
		}
	}
	//����ϸ�ڱ�
	private void add_Detail_Data(Goods good){
		database.beginTransaction();
		try{
			database.execSQL(INSERT_DETIAL,new Object[]{null,good.getId(),good.getPrice(),time,1});
			database.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            database.endTransaction();			
		}
		
	}
	//������������ϸ�ڱ�
	public void Update_Detail_Data(){
		
		for(int i = 0;i<goodslist.size();i++){
			Buygoods bg = goodslist.get(i);
			database.beginTransaction();
			try{
				database.execSQL("update Detail_info set number = '"+bg.number+"' where id = '"+bg.id+"' and time= '"+time+"'");
				database.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
			}finally {
	            database.endTransaction();			
			}	
		}
	}
	
	//�����ܱ�
	public void Update_Whole_Data(){
		
		for(int i = 0;i<goodslist.size();i++){
			Buygoods bg = goodslist.get(i);
			database.beginTransaction();
			try{
				Cursor c_w  = database.rawQuery("SELECT number From whole_info where id ='"+bg.id+"'", null);
				c_w.moveToNext();
				int number = c_w.getInt(c_w.getColumnIndex("number"));
				number = number + bg.number;
				database.execSQL("update Whole_info set number = '"+number+"' where id = '"+bg.id+"'");
				database.setTransactionSuccessful();
			}catch(Exception e){
				e.printStackTrace();
			}finally {
	            database.endTransaction();			
			}	
		}
	}
	//����ͳ����Ϣ��Ʒ������
	public int getcount(){
		int count = 0;
		for(int i = 0;i<goodslist.size();i++){
			Buygoods bg = goodslist.get(i);
			count = count+bg.number;
		}
		return count;
	}
//	����ͳ����Ϣ��Ʒ���ܼ�
	public float getprice(){
		float count = 0;
		for(int i = 0;i<goodslist.size();i++){
			Buygoods bg = goodslist.get(i);
			count = count+bg.number*bg.price;
		}
		return count;
	}
	/*
	 * �����ڽ�����չʾ��ǰ�����۲�Ʒ����Ϣ��name price time number
	 */
	public ArrayList<Goods> GetDetialinfo(String id){
		/*Cursor c_w  = database.rawQuery("SELECT * From whole_info where id ='"+id+"'", null);
		c_w.moveToNext();
		String name = c_w.getString(c_w.getColumnIndex("name"));*/
		ArrayList<Goods> goods = new ArrayList<Goods>();
		Cursor c  = database.rawQuery("SELECT * From Detail_info where id= '"+id+"'", null);
		while(c.moveToNext()){
			Goods good = new Goods();
			good.setSale_time(c.getString(c.getColumnIndex("time")));
			good.setPrice(c.getString(c.getColumnIndex("price")));
			good.setNum(c.getInt(c.getColumnIndex("number")));
			goods.add(good);
		}
		return goods;
	}
	/*
	 * �����ڽ�����չʾ������Ʒ�Ĵ���������� :name price number 
	 * 
	 */
	public ArrayList<Goods> getWholeinfo(){
		
		ArrayList<Goods> goods = new ArrayList<Goods>();
		Cursor c  = database.rawQuery("SELECT * From Whole_info ", null);
		while(c.moveToNext()){
			Goods good = new Goods();
			good.setId(c.getString(c.getColumnIndex("id")));
			good.setName(c.getString(c.getColumnIndex("name")));
			good.setNum(c.getInt(c.getColumnIndex("number")));
			goods.add(good);
		}
		return goods;
	}
	//�ر����ݿ����
	public void CloseDataBase(){
		database.close();
	}

}
