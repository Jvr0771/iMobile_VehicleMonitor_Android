package com.supermap.android.monitors;

import java.util.HashMap;

import com.supermap.android.communication.CarData;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;

/**
 * �켣���
 * @author Congle
 *
 */
public class PathMonitor {
	private DisplayManager mDisplayManager = null;
	private HashMap<String, Point2Ds> mPaths = new HashMap<String, Point2Ds>();
	private HashMap<String,Integer> mPathIDs = new HashMap<String,Integer>();

	/**
	 * ���캯��
	 * @param mgr
	 */
	public PathMonitor(DisplayManager mgr){
		mDisplayManager = mgr;
	}
	
	/**
	 * ��س���
	 * @param cardata
	 */
	public void monitor(CarData cardata){
		double x = cardata.getX();
		double y = cardata.getY();
		String carno = cardata.getCarNo();
		
		if(mPaths.containsKey(carno)){
			Point2D pt = new Point2D(x, y);
			mPaths.get(carno).add(pt);
			//������ʾ�켣
			if(mPathIDs.containsKey(carno)){
				int id = mPathIDs.get(carno);
//				mDisplayManager.updateLine(id, pt);
				showPath(true,carno);
			}
		}else{
			Point2Ds pts = new Point2Ds();
			pts.add(new Point2D(x, y));
			mPaths.put(carno, pts);
		}
		
		mDisplayManager.Translate(cardata);
	}
	
	/**
	 * ���ù켣�Ƿ���ʾ
	 * @param isShow	�Ƿ���ʾ
	 * @param carNo		���ƺ�
	 */
	public void showPath(boolean isShow,String carNo){
		if(isShow){
			if (mPaths.containsKey(carNo)) {
				
				//�Ѿ���ӹ���Ҫ���Ƴ�
				if (mPathIDs.containsKey(carNo)) {
					int id = mPathIDs.get(carNo);
					mDisplayManager.removeLine(id);

					mPathIDs.remove(carNo);
				}

				Point2Ds pts = mPaths.get(carNo);
				int id = mDisplayManager.addLine(pts);
				mPathIDs.put(carNo, id);
			}
		}else{
			if (mPathIDs.containsKey(carNo)) {
				int id = mPathIDs.get(carNo);		
				mDisplayManager.removeLine(id);

				mPathIDs.remove(carNo);
			}
		}
		mDisplayManager.refresh();
	}
	
	/**
	 * �ж�·���Ƿ��Ѿ���ʾ
	 * @param carNo
	 * @return
	 */
	public boolean isShowPath(String carNo){
		if(mPathIDs.containsKey(carNo)){
			return true;
		} else {
			return false;
		}
	}
	
}
