package edu.happy.tools;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.happy.supermarket.R;

public class MyListAdapter extends BaseAdapter {
	
	private ArrayList<Goods> ListItems;
	
	private Context context;
	
	private LayoutInflater listcontainer;
	
	public MyListAdapter(Context con,ArrayList<Goods> list) {
		// TODO Auto-generated constructor stub
		this.context = con;
		ListItems = list;
		listcontainer = LayoutInflater.from(context);
	}

	//计算一共有多少条目
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ListItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(convertView == null){
			convertView = listcontainer.inflate(R.layout.applist_layout, null);
			holder = new ViewHolder();
			holder.goodsIcon =(ImageView) convertView.findViewById(R.id.good_icon);
			holder.goodsName=(TextView) convertView.findViewById(R.id.good_name);
			holder.goodsNum=(TextView) convertView.findViewById(R.id.good_number);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		String filepath = context.getFilesDir()+"/GoodIcon/"+ListItems.get(position).getId().toString();
		System.out.println("file path is "+ filepath);
		File file = new File(filepath);
		Bitmap goodicon;
		if(file.exists()){
			goodicon = BitmapFactory.decodeFile(filepath);
		}else{
			goodicon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		}
		holder.goodsIcon.setImageBitmap(goodicon);
		holder.goodsName.setText(ListItems.get(position).getName().toString());
		holder.goodsNum.setText(String.valueOf(ListItems.get(position).getNum()));
		return convertView;
	}
	
	
	public class ViewHolder{
		public ImageView goodsIcon;
		public TextView goodsName;
		public TextView goodsNum;
	}

}
