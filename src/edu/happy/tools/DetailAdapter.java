package edu.happy.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.happy.supermarket.R;

public class DetailAdapter extends BaseAdapter {

	private ArrayList<Goods> goods;
	private Context context;
	private LayoutInflater inflater;
	
	public DetailAdapter(Context c,ArrayList<Goods> list) {
		// TODO Auto-generated constructor stub
		context = c;
		goods = list;
		inflater = LayoutInflater.from(c);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return goods.size();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.detail_layout, null);
			holder = new Holder();
			holder.price = (TextView)convertView.findViewById(R.id.price);
			holder.number = (TextView)convertView.findViewById(R.id.number);
			holder.time = (TextView)convertView.findViewById(R.id.Time);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		holder.price.setText(goods.get(position).getPrice().toString());
		holder.number.setText(String.valueOf(goods.get(position).getNum()));
		SimpleDateFormat mformat = new SimpleDateFormat("yyyy:MM:dd HH:mm"); 
		long ltime = Long.parseLong(goods.get(position).getSale_time());
		String time = mformat.format(new Date(ltime));
		holder.time.setText(time);
		return convertView;
	}
	
	private class Holder{
		public TextView price;
		public TextView number;
		public TextView time;
	}

}
