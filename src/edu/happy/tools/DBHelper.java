package edu.happy.tools;

import java.nio.charset.Charset;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import edu.happy.supermarket.R;

public class DBHelper extends SQLiteOpenHelper {

	private Context context;
	private static String DATABASE_NAME="Goods";
    
	//detial���¼ÿ�����۵�ϸ����Ϣ:��Ʒid ��ʵʱ�۸�,����ʱ�䣬��������
	private static String CREATE_Detail_DATABASE = "CREATE TABLE IF NOT EXISTS Detail_info"
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id varchar(10),price varchar(20),time varchar(10),number INTEGER)";
	
	//whole���¼��Ʒ�ľ�����Ϣ:��Ʒ id , ���ƣ��۸�������
	private static String CREATE_Whole_DATABASE = "CREATE TABLE IF NOT EXISTS Whole_info"
			+ "(id varchar(10) PRIMARY KEY,name VARCHAR(100),number INTEGER)";
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null,1);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	//���ݿ��һ�α�������ʱ��ʹ��
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL(CREATE_Detail_DATABASE);
        db.execSQL(CREATE_Whole_DATABASE);
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000000','ţ��',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000001','ɳ����',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000002','ƻ��',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000003','����',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000004','�ɱȿ�',0)");
        //�洢ͼƬ
        downloadPic();
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
	
	private void downloadPic(){
		FileTools file = new FileTools();
		Resources res =context.getResources();  
//	   ţ��
	    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.milk);
	    file.SaveIcon(context, bmp, "1000000000");
//	   ɳ����
	    bmp = BitmapFactory.decodeResource(res, R.drawable.shaqima);
	    file.SaveIcon(context, bmp, "1000000001");
//	    ƻ��
	    bmp = BitmapFactory.decodeResource(res, R.drawable.apple);
	    file.SaveIcon(context, bmp, "1000000002");
//	    ����
	    bmp = BitmapFactory.decodeResource(res, R.drawable.eggs);
	    file.SaveIcon(context, bmp, "1000000003");
//	    �ɱȿ�
	    bmp = BitmapFactory.decodeResource(res, R.drawable.kebike);
	    file.SaveIcon(context, bmp, "1000000004");
//	    ���������������
	    bmp = BitmapFactory.decodeResource(res, R.drawable.danmaicooike);
	    file.SaveIcon(context, bmp, "7733057006");
	    
	}

}
