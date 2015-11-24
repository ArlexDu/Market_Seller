package edu.happy.tools;

import java.nio.charset.Charset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME="Goods";
    
	//detial���¼ÿ�����۵�ϸ����Ϣ:��Ʒid ��ʵʱ�۸�,����ʱ�䣬��������
	private static String CREATE_Detail_DATABASE = "CREATE TABLE IF NOT EXISTS Detail_info"
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id varchar(10),price varchar(20),time varchar(10),number INTEGER)";
	
	//whole���¼��Ʒ�ľ�����Ϣ:��Ʒ id , ���ƣ��۸�������
	private static String CREATE_Whole_DATABASE = "CREATE TABLE IF NOT EXISTS Whole_info"
			+ "(id varchar(10) PRIMARY KEY,name VARCHAR(100),number INTEGER)";
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null,1);
		// TODO Auto-generated constructor stub
	}

	//���ݿ��һ�α�������ʱ��ʹ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL(CREATE_Detail_DATABASE);
        db.execSQL(CREATE_Whole_DATABASE);
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000000','milk',30)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000001','cooike',27)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000002','apple',15)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000003','eggs',50)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000004','chicken',2)");
        String rawp = "35Ԫ";
        String price = new String(rawp.getBytes(), Charset.forName("UTF-8"));
        db.execSQL("INSERT INTO Detail_info(id,price,time,number) values('1000000004','"+price+"','1448368724829',2)");
        db.execSQL("INSERT INTO Detail_info(id,price,time,number) values('1000000004','"+price+"','1448368824829',1)");
        db.execSQL("INSERT INTO Detail_info(id,price,time,number) values('1000000004','"+price+"','1448368924829',3)");
        db.execSQL("INSERT INTO Detail_info(id,price,time,number) values('1000000004','"+price+"','1448369724829',5)");
        db.execSQL("INSERT INTO Detail_info(id,price,time,number) values('1000000004','"+price+"','1448366724829',1)");
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
