package com.supermap.android.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import com.supermap.android.carmonitor.MonitorActivity;
import com.supermap.android.configuration.DefaultDataConfiguration;
import com.supermap.android.filemanager.MyAssetManager;
import com.supermap.android.filemanager.MySharedPreferences;
import com.supermap.data.Environment;
import com.supermap.gisdemo.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

/**
 * �������棬��������ʱ������ݼ��غã���Ȼ̫���ˣ�������ʱ�򻹿��Լӵ�ÿ��Ķ���
 * @author Congle
 *
 */
public class StartupActivity extends Activity {
	private final String WorksapceName = "carsmonitor.sxwu";           // ������صĵ�ͼ�����ռ��ļ���
	private final String LicName       = "Trial License.slm";          // ����ļ���
	// �����켣�ļ����Ŀ¼
	private final String TrackPath     = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/SuperMap/Demos/Data/Track/";
	// ��ͼ����Ŀ¼
	private final String WorkspacePath = DefaultDataConfiguration.MapDataPath;
	// ����ļ�Ŀ¼
	private final String LicPath       = DefaultDataConfiguration.LicensePath;
	
	private MyApplication mApp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.startup);                               //  ��ʾ��������
		
		// Initialization
		MySharedPreferences.init(this);
		MyAssetManager.init(this);
		
		// ��ʼ������
	    
		
		
		Environment.setLicensePath(LicPath);
		Environment.setWebCacheDirectory(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/SuperMap/WebCahe/");
		Environment.initialization(this);
		
		mApp = (MyApplication) getApplication();
		mApp.registerActivity(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		final ProgressDialog dialog = new ProgressDialog(this);
    	dialog.setCancelable(false);
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.setMessage("���ڳ�ʼ�����ݡ�����");
    	dialog.show();
		
		new Thread(){
			public void run() {
			    new DefaultDataConfiguration().autoConfig();
				if(!uploadMapData()){
					Log.e(this.getClass().getName(), "������ʼ��ʧ��");
				}
				copyTrackData(); //�����켣�ļ�
				if(!mApp.openWorkspace(WorkspacePath+WorksapceName)){
					Log.e(this.getClass().getName(), "���ݴ�ʧ��");
				}
				
				//������̨����
				Intent intentService = new Intent();
				intentService.setAction("com.supermap.backstageservice.START");
				startService(intentService);
				dialog.dismiss();
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						Intent intent = new Intent(StartupActivity.this, MonitorActivity.class);
						StartupActivity.this.startActivity(intent);
						
					}
				});
			};
		}.start();
		
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
	}
	
	/**
	 * ��������
	 * @return  �ɹ���ʧ��
	 */
	private boolean uploadMapData(){
		File wksFile = new File(WorkspacePath+WorksapceName);
		File licFile = new File(LicPath+LicName);
		if(!wksFile.exists() || !licFile.exists()){
			try {
				AssetManager mgr = this.getAssets();
				InputStream is = mgr.open(WorksapceName);
				boolean reslut = copyFile(is, wksFile, true);            // ���ƹ����ռ�
				is.close();
				
				is = mgr.open(LicName);
				reslut &= copyFile(is, licFile, true);                   // ����License
				is.close();
				
				return reslut;
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ���Ƴ����Ĺ켣�ļ���TrackPath
	 */
	private void copyTrackData(){
		AssetManager mgr = this.getAssets();
		try {
			String[] list = mgr.list("Track");
			int listLength = list.length;
			for (int i = 0; i < listLength; i++) {
				
				File trackFile = new File(TrackPath+i+".txt");
				InputStream is = mgr.open("Track/"+list[i]);
				copyFile(is, trackFile, true);
				is.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 private boolean copyFile(InputStream src,File des,boolean rewrite){
	    	//Ŀ��·�������ڵĻ��ʹ���һ��
	    	if(!des.getParentFile().exists()){
	    		des.getParentFile().mkdirs();
	    	}
	    	if(des.exists()){
	    		if(rewrite){
	    			des.delete();
	    		}else{
	    			return false;
	    		}
	    	}
	    	
	    	try{
	    		InputStream fis = src;
	    		FileOutputStream fos = new FileOutputStream(des);
	    		//1kb
	    		byte[] bytes = new byte[1024];
	    		int readlength = -1;
	    		while((readlength = fis.read(bytes))>0){
	    			fos.write(bytes, 0, readlength);
	    		}
	    		fos.flush();
	    		fos.close();
	    		fis.close();
	    	}catch(Exception e){
	    		return false;
	    	}
	    	return true;
	    }
}
