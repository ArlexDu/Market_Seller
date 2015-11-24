package edu.happy.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseControl {
	
	private DBHelper dbhelper;
	private SQLiteDatabase database;
	private static String INSERT_DETIAL="INSERT INTO Detial_info VALUES(?,?,?)";
	
	public DataBaseControl(Context context) {
		// TODO Auto-generated constructor stub
		dbhelper = new DBHelper(context);
		database = dbhelper.getWritableDatabase();
		
	}
	
	//更新细节表
	private void add_Detail_Data(Goods good){
		database.beginTransaction();
		
		try{
			database.execSQL(INSERT_DETIAL,new Object[]{good.getId(),good.getSale_time(),1});
			database.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            database.endTransaction();			
		}
		
	}
	
	//更新总表
	private void Update_Whole_Data(){
		
	/*	database.beginTransaction();
		try{
			database.execSQL(INSERT_DETIAL,new Object[]{good.getId(),good.getSale_time(),1});
			database.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            database.endTransaction();			
		}
		*/
	}
	/*
	 * 用于在界面上展示当前的销售产品的信息：name price number
	 */
	private List<Goods> getWholeinfor(){
		Cursor c_w  = database.rawQuery("SELECT * Form whole_info", null);
		c_w.moveToNext();
		String name = c_w.getString(c_w.getColumnIndex("name"));
		String price = c_w.getString(c_w.getColumnIndex("price"));
		ArrayList<Goods> goods = new ArrayList<Goods>();
		Cursor c  = database.rawQuery("SELECT * Form whole_info", null);
		while(c.moveToNext()){
			Goods good = new Goods();
			good.setSale_time(c.getString(c.getColumnIndex("time")));
			good.setName(name);
			good.setPrice(price);
			good.setNum(c.getInt(c.getColumnIndex("number")));
			goods.add(good);
		}
		return goods;
	}
	/*
	 * 用于在界面上展示某一商品的具体销售情况 :name price time number 
	 * 这个time暂时使用时间戳来表示
	 */
	private List<Goods> getDetialinfor(String id){
		
		ArrayList<Goods> goods = new ArrayList<Goods>();
		Cursor c  = database.rawQuery("SELECT * Form detial_info", null);
		while(c.moveToNext()){
			Goods good = new Goods();
			good.setName(c.getString(c.getColumnIndex("name")));
			good.setPrice(c.getString(c.getColumnIndex("price")));
			good.setNum(c.getInt(c.getColumnIndex("number")));
			goods.add(good);
		}
		return goods;
	}
	//关闭数据库对向
	private void CloseDataBase(){
		database.close();
	}

}
