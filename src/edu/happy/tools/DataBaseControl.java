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

public class DataBaseControl {
	
	private DBHelper dbhelper;
	private SQLiteDatabase database;
	private static String INSERT_DETIAL="INSERT INTO Detail_info VALUES(?,?,?,?,?)";
	private static String INSERT_WHOLE="INSERT INTO Whole_info VALUES(?,?,?)";
	private long time;
	//用于一次消费记录当前的的所有消费商品
	private class Buygoods{
		public String id;
		public int number;
	}
	private List<Buygoods> goodslist = new ArrayList<Buygoods>();
	
	private boolean isnew =true;//用于判断最新扫描的商品在本次扫描前是否已经扫描过
	public DataBaseControl(Context context) {
		// TODO Auto-generated constructor stub
		dbhelper = new DBHelper(context);
		database = dbhelper.getWritableDatabase();
		time = System.currentTimeMillis();
		SimpleDateFormat mformat = new SimpleDateFormat("yyyy:MM:dd HH:mm"); 
		String stime = mformat.format(new Date(time));
		System.out.println("time is "+stime);
	}
	
	public void RecordGood(Goods good){
		for(int i = 0; i< goodslist.size();i++){
			Buygoods bg = goodslist.get(i);
			if(good.getId()==bg.id){
				bg.number++;
				isnew = false;
				break;
			}
			
		}
		if(isnew){//是新的商品的话就在数据库中加入一条
			Buygoods nbg = new Buygoods();
			nbg.id = good.getId();
			nbg.number = 1;
			goodslist.add(nbg);	
			add_Detail_Data(good);
		}//不是本次结算新的的话就在结算结束后向已有的数据库条目中更新数量
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
	//新增细节表
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
	//结算结束后更新细节表
	public void update_Detail_Data(Goods good){
		
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
	
	//更新总表
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
	/*
	 * 用于在界面上展示当前的销售产品的信息：name price time number
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
	 * 用于在界面上展示所有商品的大体销售情况 :name price number 
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
	//关闭数据库对向
	public void CloseDataBase(){
		database.close();
	}

}
