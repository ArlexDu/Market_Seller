package edu.happy.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME="Goods";
    
	//detial���¼ÿ�����۵�ϸ����Ϣ:��Ʒid ������ʱ�䣬��������
	private static String CREATE_Detail_DATABASE = "CREATE TABLE IF NOT EXIST Detial_info"
			+ "(_id varchar(10) PRIMARY KEY,time varchar(10),number INTEGER)";
	
	//whole���¼��Ʒ�ľ�����Ϣ:��Ʒ id , ���ƣ��۸�������
	private static String CREATE_whole_DATABASE = "CREATE TABLE IF NOT EXIST whole_info"
			+ "(_id varchar(10) PRIMARY KEY,name VARCHAR(100),price varchar(20),number INTEGER";
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null,1);
		// TODO Auto-generated constructor stub
	}

	//���ݿ��һ�α�������ʱ��ʹ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL(CREATE_Detail_DATABASE);
        db.execSQL(CREATE_whole_DATABASE);
	}
    //���ݿ���µ�ʱ��ʹ��
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
	}

}
