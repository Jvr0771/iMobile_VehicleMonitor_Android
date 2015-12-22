package com.supermap.android.app;


import java.util.ArrayList;

import com.supermap.android.filemanager.MyAssetManager;
import com.supermap.android.filemanager.MySharedPreferences;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;

import android.app.Activity;
import android.app.Application;
import android.os.Process;

public class MyApplication extends Application {
	
	public static String BroadcastAction = "com.supermap.CAR_DATA";
	private Workspace mWorkspace = null;
  	private WorkspaceConnectionInfo mInfo = null;
	private boolean isOpen = false;
	
	public static String DATAPATH = "";
	// ��ȡAndroid�豸�ڲ��洢����Ŀ¼��SDCARD
	public static String SDCARD = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	
	private static MyApplication sInstance = null;
	
	// Activity �б�
	private ArrayList<Activity> mActivities = new ArrayList<Activity>();
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		
		// ��ȡӦ�ó�����ļ�Ŀ¼��DATAPATH
		DATAPATH = this.getFilesDir().getAbsolutePath() + "/";
		sInstance = this;
	}
	
	/**
	 *  �򿪹����ռ�
	 * @param server    �����ռ�·��
	 * @return          ���سɹ���ʧ��
	 */
	public boolean openWorkspace(String server){
		if(isOpen){
			return isOpen; 
		}
		mWorkspace = new Workspace();
		mInfo = new WorkspaceConnectionInfo();  
		mInfo.setServer(server);
		WorkspaceType type = (server.endsWith("SMWU")||server.endsWith("smwu"))?WorkspaceType.SMWU:WorkspaceType.SXWU;
		mInfo.setType(type);
		isOpen =  mWorkspace.open(mInfo);  
		return isOpen;
	}
	
	/**
	 * ��Activity���뵽 Activity�б���
	 * @param act   ��Ҫ��ӵ�activity
	 */
	public void registerActivity(Activity act){
		mActivities.add(act);
	}
	
	/**
	 * ��ȡ�����ռ�
	 * @return    �����ռ����
	 */
	public Workspace getWorkspace(){
		return mWorkspace;
	}
	
	public static MyApplication getInstance()
	{
		return sInstance;
	}
	
	
	 /**
	  * �˳�Ӧ��                  
	  */
	public void exit(){
		try {
			mWorkspace.save();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mWorkspace.close();
		mInfo.dispose();
		mWorkspace.dispose();
		mInfo = null;
		mWorkspace = null;
		
		for(Activity act:mActivities){
			act.finish();
		}
		Process.killProcess(Process.myPid());
	}
}
