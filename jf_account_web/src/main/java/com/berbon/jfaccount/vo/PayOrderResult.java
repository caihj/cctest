package com.berbon.jfaccount.vo;

public class PayOrderResult {
	
	private String orderId;
	private String cardNo;
	private Long priceAmount;
	private Integer orderState;
	
	
	public PayOrderResult() {
		super();
	}
	public PayOrderResult(String orderId, String cardNo, Long priceAmount,
			Integer orderState) {
		super();
		this.orderId = orderId;
		this.cardNo = cardNo;
		this.priceAmount = priceAmount;
		this.orderState = orderState;
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
	public Long getPriceAmount() {
		return priceAmount;
	}
	public void setPriceAmount(Long priceAmount) {
		this.priceAmount = priceAmount;
	}
	public Integer getOrderState() {
		return orderState;
	}
	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}

	
	
	

}
