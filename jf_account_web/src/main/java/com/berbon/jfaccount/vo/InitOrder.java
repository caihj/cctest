package com.berbon.jfaccount.vo;

public class InitOrder {
	private String productId;
	private String cardNo;
	private String mob;
	private Long denomination;
	private Long chargeNum;
	private String other;
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
	public String getMob() {
		return mob;
	}
	public void setMob(String mob) {
		this.mob = mob;
	}

	public Long getDenomination() {
		return denomination;
	}
	public void setDenomination(Long denomination) {
		this.denomination = denomination;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public Long getChargeNum() {
		return chargeNum;
	}
	public void setChargeNum(Long chargeNum) {
		this.chargeNum = chargeNum;
	}

}
