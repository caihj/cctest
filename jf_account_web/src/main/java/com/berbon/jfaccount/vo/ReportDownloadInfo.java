package com.berbon.jfaccount.vo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportDownloadInfo {
	private String usercode;
	private String dateTime;
	private String days="1";
	private String fileName;
	private String serviceCode;
	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}


	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getFileName() {
		if (null == this.fileName) {
			this.fileName = usercode + "_" + dateTime + ".zip";
		}
		return fileName;
	}

	public ReportDownloadInfo(String usercode, String dateTime, String days) {
		this.usercode = usercode;
		this.dateTime = dateTime;
		this.days = days;
	}
		
	
	
	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public List<String> getFileNames() {
		List<String> dates = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(dateTime));
			for (int i = 0; i < Integer.parseInt(days); i++) {
				dates.add(usercode + "_" + sdf.format(calendar.getTime())
						+ ".xls");
				calendar.add(Calendar.DAY_OF_YEAR, -1);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dates;
	}

	public ReportDownloadInfo() {
		// TODO Auto-generated constructor stub
	}

}
