package edu.happy.supermarket;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.happy.tools.DataBaseControl;
import edu.happy.tools.DetailAdapter;
import edu.happy.tools.Goods;
import edu.happy.tools.MyListAdapter;

public class DetialInfoList extends Activity  {

	private ArrayList<Goods> list = new ArrayList<Goods>();
	private ListView listview ;
	private TextView head;
	private ProgressBar bar;
	private TextView no_info;
	Handler myHander = new Handler(){
		
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				init();
			}
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_app_list);
		listview = (ListView)findViewById(R.id.applist);
		bar = (ProgressBar)findViewById(R.id.list_bar);
		head = (TextView)findViewById(R.id.text);
		new Thread(new Runnable() {
			public void run() {
				getdata();
				Message msg = Message.obtain();
				msg.what = 0;
				// ���������Ϣ����Ϣ������
				myHander.sendMessage(msg);
			}
		}).start();
	}
	private void init(){
		if(list.size() ==0){//��ǰû�иĲ�Ʒ����������
			listview.setVisibility(View.INVISIBLE);
			no_info = (TextView)findViewById(R.id.no_information);
			no_info.setVisibility(View.VISIBLE);
		}else{
			DetailAdapter adapter = new DetailAdapter(this, list);
			listview.setAdapter(adapter);	
		}
		bar.setVisibility(View.INVISIBLE);
	}
	
	//�������ݿ�����Ϣ
	private void getdata(){
		Intent intent = getIntent();
		String id = intent.getStringExtra("id");
		String name = intent.getStringExtra("name");
		head.setText(name+"�ľ���������Ϣ");
		DataBaseControl db = new DataBaseControl(this);
		list = db.GetDetialinfo(id);
		db.CloseDataBase();
	}

	
	//��д���ؼ������ⷵ�ؿ�ָ�����
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode == KeyEvent.KEYCODE_BACK){
				Intent intent = new Intent();
				intent.putExtra("package_name", "");
				setResult(0,intent);
				finish();
				return true;
			}else{
				return super.onKeyDown(keyCode, event);	
			}
		}


}
