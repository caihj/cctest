package com.berbon.jfaccount.vo;

import java.io.Serializable;

public class ResponseInitOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String  cardName;
	private String productId;  //产品Id
	private String cardNo;     //卡号
	private Long denomination;  //面值
	private Long priceAmount;//售价金额
	private Long payAmount; //折扣金额
	private Long chargeNum;  //订单数量
	private String orderId;   //订单号
	
	
	public ResponseInitOrder() {
		super();
	}
	
	
	public ResponseInitOrder( String cardName,
			String productId, String cardNo, Long denomination,
			Long priceAmount, Long payAmount, Long chargeNum, String orderId) {
		super();
		this.cardName = cardName;
		this.productId = productId;
		this.cardNo = cardNo;
		this.denomination = denomination;
		this.priceAmount = priceAmount; //售价金额
		this.payAmount = payAmount;   
		this.chargeNum = chargeNum;
		this.orderId = orderId;
	}
	
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public Long getDenomination() {
		return denomination;
	}
	public void setDenomination(Long denomination) {
		this.denomination = denomination;
	}
	public Long getPriceAmount() {
		return priceAmount;
	}
	public void setPriceAmount(Long priceAmount) {
		this.priceAmount = priceAmount;
	}
	public Long getPayAmount() {
		return payAmount;
	}
	public void setPayAmount(Long payAmount) {
		this.payAmount = payAmount;
	}
	public Long getChargeNum() {
		return chargeNum;
	}
	public void setChargeNum(Long chargeNum) {
		this.chargeNum = chargeNum;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	
	
}
