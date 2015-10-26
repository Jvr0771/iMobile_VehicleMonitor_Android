package com.supermap.android.monitors;

import java.util.HashMap;
import com.supermap.CarsMonitorDemo.R;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

public class SoundMonitor {

	private Vibrator mVibrator;
	private SoundPool mSoundPool;
	private HashMap<String, Integer> mSoundMap;
	private SharedPreferences m_preferences;
	
	static SoundMonitor mSoundMonitor;
	
	/**
	 * ��ʼ��
	 * @param context
	 */
	public static void init(Context context) {
		mSoundMonitor = new SoundMonitor(context);
	}
	
	/**
	 * ��ȡ������ض���
	 * @return
	 */
	public static SoundMonitor getSoundMonitor(){
		return mSoundMonitor; 
	}
	
	/**
	 * ���캯��
	 * @param context
	 */
	private SoundMonitor(Context context) {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		mSoundMap = new HashMap<String, Integer>();
		
		mSoundMap.put("alert", mSoundPool.load(context, R.raw.alert, 1));
		mSoundMap.put("whistle", mSoundPool.load(context, R.raw.whistle, 1));
		
		mVibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
		
		m_preferences = context.getSharedPreferences("SystemSetting", Context.MODE_PRIVATE);
	}
	
	/**
	 * ������
	 */
	public void warn() {
		Boolean isSwitchVoice = m_preferences.getBoolean("switchVoice", false);

		if (isSwitchVoice) {
			mSoundPool.play(mSoundMap.get("alert"), 1, 1, 0, 0, 1);
		}
	}
	
	/**
	 * ����
	 */
	public void whistle() {
		Boolean isSwitchVoice = m_preferences.getBoolean("switchVoice", false);

		if (isSwitchVoice) {
			mSoundPool.play(mSoundMap.get("whistle"), 1, 1, 0, 0, 1);
		}
	}
	
	/**
	 * ��
	 */
	public void shake(){
		Boolean isSwitchShake = m_preferences.getBoolean("switchShake", false);
		if (isSwitchShake) {
			mVibrator.vibrate(1000);
		}
	}
}
