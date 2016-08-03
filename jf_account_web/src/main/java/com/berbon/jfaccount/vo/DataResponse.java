package com.berbon.jfaccount.vo;

public class DataResponse {
	
	private String ack;
	private String ackDesc;
	private Object data;
	private Long totalNum;
	private String extendData;
	
	
	public DataResponse(String ack, String ackDesc, Object data) {
		super();
		this.ack = ack;
		this.ackDesc = ackDesc;
		this.data = data;
	}
	
	
	public DataResponse(String ack, String ackDesc, Object data,
			Long totalNum) {
		super();
		this.ack = ack;
		this.ackDesc = ackDesc;
		this.data = data;
		this.totalNum = totalNum;
	}

	

	public DataResponse(String ack, String ackDesc, Object data, Long totalNum, String extendData) {
		super();
		this.ack = ack;
		this.ackDesc = ackDesc;
		this.data = data;
		this.totalNum = totalNum;
		this.extendData = extendData;
	}


	public String getAck() {
		return ack;
	}
	public void setAck(String ack) {
		this.ack = ack;
	}
	public String getAckDesc() {
		return ackDesc;
	}
	public void setAckDesc(String ackDesc) {
		this.ackDesc = ackDesc;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Long getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}


	public String getExtendData() {
		return extendData;
	}


	public void setExtendData(String extendData) {
		this.extendData = extendData;
	}
	
	public static void main(String[] args) {
		
	
	}

}
