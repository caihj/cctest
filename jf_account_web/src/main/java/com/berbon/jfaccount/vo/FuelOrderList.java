package com.berbon.jfaccount.vo;

public class FuelOrderList {
	
	private String orderId;
	private String cardNo;
	private String orderTime;
	private String cardName;
	private Long denomination;
	private Long payAmount;
	private Integer orderState;
	private Integer cardType;
	
	
	public FuelOrderList() {
		super();
	}
	public FuelOrderList(String orderId, String cardNo, String orderTime,
			String cardName, Long denomination, Long payAmount,
			Integer orderState,Integer cardType) {
		super();
		this.orderId = orderId;
		this.cardNo = cardNo;
		this.orderTime = orderTime;
		this.cardName = cardName;
		this.denomination = denomination;
		this.payAmount = payAmount;
		this.orderState = orderState;
		this.cardType = cardType;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public Long getDenomination() {
		return denomination;
	}
	public void setDenomination(Long denomination) {
		this.denomination = denomination;
	}
	public Long getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(Long payAmount) {
		this.payAmount = payAmount;
	}
	public Integer getOrderState() {
		return orderState;
	}
	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}
	public Integer getCardType() {
		return cardType;
	}
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	
	
	
	

}
