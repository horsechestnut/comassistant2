package com.example.administrator.comassistant2.simulation.bean;

/**
 * @author benjaminwan
 */
public class ComBean {
		public byte[] bRec=null;
		public String sComPort="";
		public ComBean(String sPort,byte[] buffer,int size){
			sComPort=sPort;
			bRec=new byte[size];
			for (int i = 0; i < size; i++)
			{
				bRec[i]=buffer[i];
			}
			//SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
			//sRecTime = sDateFormat.format(new java.util.Date());
		}
}