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
    
	//detial表记录每次销售的细节信息:商品id ，实时价格,交易时间，交易数量
	private static String CREATE_Detail_DATABASE = "CREATE TABLE IF NOT EXISTS Detail_info"
			+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id varchar(10),price varchar(20),time varchar(10),number INTEGER)";
	
	//whole表记录商品的具体信息:商品 id , 名称，价格，总数量
	private static String CREATE_Whole_DATABASE = "CREATE TABLE IF NOT EXISTS Whole_info"
			+ "(id varchar(10) PRIMARY KEY,name VARCHAR(100),number INTEGER)";
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null,1);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	//数据库第一次被创建的时候使用
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
        db.execSQL(CREATE_Detail_DATABASE);
        db.execSQL(CREATE_Whole_DATABASE);
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000000','牛奶',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000001','沙琪玛',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000002','苹果',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000003','鸡蛋',0)");
        db.execSQL("INSERT INTO Whole_info(id,name,number) values('1000000004','可比克',0)");
        //存储图片
        downloadPic();
	}
	
    //数据库更新的时候使用
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
//	   牛奶
	    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.milk);
	    file.SaveIcon(context, bmp, "1000000000");
//	   沙琪玛
	    bmp = BitmapFactory.decodeResource(res, R.drawable.shaqima);
	    file.SaveIcon(context, bmp, "1000000001");
//	    苹果
	    bmp = BitmapFactory.decodeResource(res, R.drawable.apple);
	    file.SaveIcon(context, bmp, "1000000002");
//	    鸡蛋
	    bmp = BitmapFactory.decodeResource(res, R.drawable.eggs);
	    file.SaveIcon(context, bmp, "1000000003");
//	    可比克
	    bmp = BitmapFactory.decodeResource(res, R.drawable.kebike);
	    file.SaveIcon(context, bmp, "1000000004");
//	    丹麦香草威化饼干
	    bmp = BitmapFactory.decodeResource(res, R.drawable.danmaicooike);
	    file.SaveIcon(context, bmp, "7733057006");
	    
	}

}
