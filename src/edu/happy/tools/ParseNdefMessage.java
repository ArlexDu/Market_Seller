package edu.happy.tools;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ParseNdefMessage {
	
	//����һ����Ϣ���Ѿ���װ�õ�View
	public View getView(Activity activity ,LayoutInflater inflater,ViewGroup parent,int offset,DataBaseControl database);
	
	
}
