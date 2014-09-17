package com.supermap.android.communication;

import java.util.ArrayList;

public class NaviDataManager {
	
	private int m_Cursor = 0; //�α�
	
	private boolean isAdvance = true; //�Ƿ���ǰ��ȡ
	
	private ArrayList<CarData> m_arrryCarData;	//��¼��������		
	
	/**
	 * ���캯��
	 */
	public NaviDataManager() {
		m_arrryCarData = new ArrayList<CarData>();
	}
	
	/**
	 * ��ʼ����������
	 * @param carNo     ��������
	 * @param dataPath 	�켣�ļ�
	 */
	public void init(String carNo, String dataPath) {
		ArrayList<String> arrryString = new ArrayList<String>();	//��¼ÿ������
		OperateFile.readFile(dataPath, arrryString);
		
		String strPhoneNo = createPhoneNo();
		String strCarName = createCarName();
		for (int i = 0; i < arrryString.size(); i++) {
			ArrayList<Double> arrryDouble = new ArrayList<Double>();	//��¼ÿ������
			
			OperateFile.resolveData(arrryString.get(i), arrryDouble);
			// ���ó�����Ϣ
			CarData data = new CarData();
			data.setCarName(strCarName);
			data.setCarNo(carNo);
			data.setPhoneNo(strPhoneNo);
			data.setX(arrryDouble.get(0));
			data.setY(arrryDouble.get(1));
			
			m_arrryCarData.add(data);
		}
		int random = (int) (Math.random()*m_arrryCarData.size());
		m_Cursor = random;
	}
	
	/**
	 * �����绰����
	 * @return
	 */
	private String createPhoneNo(){
		StringBuilder sb = new StringBuilder();
		sb.append("186");
		
		//8������
		for(int i=0;i<8;i++){
			int value = (int) (Math.random()*9);
			sb.append(value);
		}
		
		return sb.toString();
	}
	
	/**
	 * ������������
	 * @return   ���س��������ַ���
	 */
	private String createCarName(){
		String carName = "δ֪����";
		int value = (int) (Math.random()*10);
		//8������
		switch (value){
		case 0:
			carName = "��������";
			break;
		case 1:
			carName = "�µ�";
			break;
		case 2:
			carName = "����";
			break;
		case 3:
			carName = "��������";
			break;
		case 4:
			carName = "ѩ����";
			break;
		case 5:
			carName = "����";
			break;
		case 6:
			carName = "������";
			break;
		default:
			carName = "δ֪����";
			break;
		}
		
		return carName;
	}
	
	public int getSize() {
		return m_arrryCarData.size();
	}
	
	/**
	 * ��ȡһ�����ݣ��������ת�����ȡ
	 * @return
	 */
	public CarData getData() {
			
		if (m_arrryCarData.size() < 1) {
			return null;
		}
		
		if (m_arrryCarData.size() == 1) {
			return m_arrryCarData.get(0);
		}
		
		// �ı��ȡ����
		if (m_Cursor == m_arrryCarData.size()-1) {
			isAdvance = false;
		} else if (m_Cursor == 0) {
			isAdvance = true;
		}
		
		if (isAdvance) {
			return m_arrryCarData.get(m_Cursor++);
		} else {
			return m_arrryCarData.get(m_Cursor--);
		}
	}
	
}