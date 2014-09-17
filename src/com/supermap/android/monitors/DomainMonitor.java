package com.supermap.android.monitors;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.supermap.android.communication.CarData;
import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.GeoPoint;
import com.supermap.data.GeoRectangle;
import com.supermap.data.GeoText;
import com.supermap.data.Geometrist;
import com.supermap.data.Geometry;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.TextAlignment;
import com.supermap.data.TextPart;
import com.supermap.data.TextStyle;
import com.supermap.mapping.GeometryEvent;
import com.supermap.mapping.TrackingLayer;
import com.supermap.mapping.dyn.DynamicPoint;


/**
 * ��Χ��أ��������Χ��Խ���ж�
 * @author Congle
 *
 */
public class DomainMonitor {
	private DisplayManager mDisplayManager = null;
	
	private Handler        mUIHandler      = null;
	private TextView       mWarningInfo    = null;
	private TrackingLayer  mTrackingLayer  = null;
	
	private ArrayList<MonitorDomain> m_MonitorDomainList = null;
	
	/**
	 * ���캯��
	 * @param mgr
	 */
	public DomainMonitor(DisplayManager mgr){
		mDisplayManager = mgr;
		mTrackingLayer  = mgr.getTrackingLayer();
		mUIHandler      = new Handler();
		m_MonitorDomainList = new ArrayList<MonitorDomain>();
		
	}
	
	/**
	 * ���Ӽ�����б����
	 * @param event
	 */
	public void monitorDomainList(GeometryEvent event){
		
		if(event == null){
			return;
		}
		
		Recordset recordset = null;
		DatasetVector datasetVector = null;

		datasetVector = (DatasetVector) event.getLayer().getDataset();
		if(datasetVector == null){			
			return;
		}
		recordset = datasetVector.query(new int[]{event.getID()}, CursorType.DYNAMIC);
		
		Geometry geometry = recordset.getGeometry();                     // ��ȡ�������
		// �����������ӵ���������б�
		if(geometry != null){
			 MonitorDomain monitorDomain = new MonitorDomain();
		     monitorDomain.setMonitorGeometry(geometry);
		     monitorDomain.setMointorName("����� " + m_MonitorDomainList.size());
		     GeoText geoText = createGeoText(geometry, monitorDomain.getMonitorName());
		     if(geoText != null && mTrackingLayer != null){
		    	 mTrackingLayer.add(geoText, "");
		     }
		     m_MonitorDomainList.add(monitorDomain);
		}else {
			
		}
		recordset.dispose();
	}
	
	/**
	 * ��س���
	 * @param cardata
	 */
	public void monitor(CarData cardata){
		if(cardata == null){
			return ;
		}
		
		DynamicPoint car = mDisplayManager.queryCar(cardata);
		
		if(car == null){
			return;
		}
		Rectangle2D rectangle2d = car.getBounds();
		Rectangle2D rectangle2d2 = new Rectangle2D(rectangle2d.getLeft(), rectangle2d.getBottom(), rectangle2d.getRight()*1.1, rectangle2d.getTop()*1.1);
		GeoRectangle carRectangle = new GeoRectangle(rectangle2d2, 0);
		Point2Ds point2Ds = car.getGeoPoints();
		
		Point2D point2d = point2Ds.getItem(0);
		
		GeoPoint geoPoint = new GeoPoint(point2d);
		
		int id = car.getID();
		
		if(carRectangle != null && mDisplayManager != null && m_MonitorDomainList.size()>0){
			
			for (MonitorDomain monitorDomain : m_MonitorDomainList) {
				Geometry monitorDomainGeo = monitorDomain.getMonitorGeometry();
				if(monitorDomainGeo == null ){
					break;
				}
				boolean isContainCar = Geometrist.canContain(monitorDomainGeo, geoPoint);
				boolean isContainCarID = monitorDomain.getMointorIDs().contains(id);
				
				if(isContainCar){
					//���ڼ�������ڣ���������δ��¼�ó���ID�����ǽ���
					if(!isContainCarID){
						
						entryWarning(cardata, monitorDomain);
						//����ü������
						monitorDomain.getMointorIDs().add(id);
					}
				} else{
					// �����ڼ�������ڣ����������иó���ID��¼�������뿪
					if(isContainCarID){
					
						leaveWarning(cardata, monitorDomain);
						monitorDomain.getMointorIDs().remove(id);
					}
				}
			}
			
		}
	}
	

	/**
	 * ����������
	 */
	public void clearMonitorDomain() {
		// ��������
		if(m_MonitorDomainList != null){
			m_MonitorDomainList.clear();
		}
		
		mDisplayManager.clearEditLayer();
		mDisplayManager.refresh();
	}
	
	/**
	 * �󶨾�����Ϣ��ʾ��
	 */
	public void attacthWarningDisplay(TextView textView){
		mWarningInfo = textView;
	}
	
	/**
	 * ��������������ʾ
	 * @param cardata
	 * @param domain
	 */
	private void entryWarning(CarData cardata, MonitorDomain domain){
		if(cardata == null || domain == null){
			return;
		}
		
		DynamicPoint car = mDisplayManager.queryCar(cardata);
		
		if(car != null && mWarningInfo!= null){
			mDisplayManager.flashing(cardata,5);
			String carNo = cardata.getCarNo();
			mWarningInfo.setTextColor(Color.RED);
			mWarningInfo.setBackgroundColor(Color.argb(180, 155, 155, 155));
			mWarningInfo.setTextSize(35.0f);
			mWarningInfo.setVisibility(View.VISIBLE);
			String time = new Date(System.currentTimeMillis()).toLocaleString();
			mWarningInfo.setText(domain.getMonitorName() + ": \n" + time+"\n"+carNo+"���� �����");
			mWarningInfo.invalidate();
			mUIHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mWarningInfo.setVisibility(View.GONE);
				}
			}, 3000);
			
			//������Ч
			SoundMonitor.getSoundMonitor().warn();
			SoundMonitor.getSoundMonitor().shake();
		}
	}
	
	/**
	 * �����뿪�������ʾ
	 * @param cardata
	 * @param domain
	 */
	private void leaveWarning(CarData cardata, MonitorDomain domain){
		if(cardata == null || domain == null){
			return;
		}
		DynamicPoint car = mDisplayManager.queryCar(cardata);
	
		if(car != null && mWarningInfo!= null){
			String carNo = cardata.getCarNo();
			mWarningInfo.setTextColor(Color.RED);
			mWarningInfo.setBackgroundColor(Color.argb(180, 155, 155, 155));
			mWarningInfo.setTextSize(35.0f);
			mWarningInfo.setVisibility(View.VISIBLE);
			String time = new Date(System.currentTimeMillis()).toLocaleString();
			mWarningInfo.setText(domain.getMonitorName() + ": \n" + time+"\n"+carNo+"�뿪�����");
			mWarningInfo.invalidate();
			mUIHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mWarningInfo.setVisibility(View.GONE);
				}
			}, 5000);
			
			//������Ч
			SoundMonitor.getSoundMonitor().whistle();
			SoundMonitor.getSoundMonitor().shake();
		}
	}
	
	
	/**
	 *  ���������
	 *
	 */
	private class MonitorDomain {
		private Geometry     monitorGeometry    = null;
		private Set<Integer> monitorIDs         = null;
		private String       monitorName        = null;
		public MonitorDomain(){
			monitorIDs = new HashSet<Integer> ();
		}
		public boolean setMonitorGeometry (Geometry geometry ) {
			if(geometry != null) {
				monitorGeometry = geometry;
			}else{
				return false;
			}
			
			return true;
		}
		
		public void addMonitorCarID (Integer id) {
			monitorIDs.add(id);
		}
		
		public Geometry getMonitorGeometry() {
			return monitorGeometry;
		}
		public Set<Integer> getMointorIDs () {
			return monitorIDs;
		}
		
		public void setMointorName(String monitorname){
			monitorName = monitorname;
		}
		
		public String getMonitorName(){
			return monitorName;
		}
	}
	
	/**
	 * ��ȡ���ζ����ı�
	 * @param geometry
	 * @param content
	 * @return
	 */
	public GeoText createGeoText(Geometry geometry, String content) {
		GeoText geoText = null;
		if(geometry != null){
			 TextStyle textStyle = new TextStyle();
		        //textStyle.setRotation(30.0);
		        //textStyle.setShadow(true);
		        textStyle.setAlignment(TextAlignment.TOPCENTER);
		        textStyle.setBackColor(new com.supermap.data.Color(0x53ccc3));
		        textStyle.setForeColor(new com.supermap.data.Color(0x000000));
		        textStyle.setBackOpaque(true);
		        textStyle.setBold(true);
		        textStyle.setFontName("����");
		        textStyle.setFontHeight(10.0);
		        textStyle.setFontWidth(10.0);
		        textStyle.setSizeFixed(true);
		        //textStyle.setItalic(true);
		        //textStyle.setOutline(true);
		        //textStyle.setStrikeout(true);
		        //textStyle.setUnderline(true);
		        textStyle.setWeight(50);

		    Point2D point2d = geometry.getInnerPoint();
		    TextPart textPart = new TextPart(content, point2d);
		    geoText = new GeoText(textPart, textStyle);
		}
		return geoText;
	}
}
