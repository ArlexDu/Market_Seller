package edu.happy.tools;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ParseNdefMessage {
	
	//返回一个信息都已经包装好的View
	public View getView(Activity activity ,LayoutInflater inflater,ViewGroup parent,int offset,DataBaseControl database);
	
	
}
