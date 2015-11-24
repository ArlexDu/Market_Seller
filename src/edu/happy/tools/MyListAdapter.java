package edu.happy.tools;

import java.util.ArrayList;

import android.content.Context;
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

	//����һ���ж�����Ŀ
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
	//	holder.goodsIcon.setBackground((ListItems.get(position).getAppIcon()));
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
