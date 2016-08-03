package com.berbon.jfaccount.vo;

public class FuelCardInfo {
	
	private String productId;
	private String productName;
	private Integer productType;
	private Integer cardType;
	private Long denomination;
	public String getProductId() {
		return productId;
	}
	public FuelCardInfo() {
		super();
	}
	public FuelCardInfo(String productId, String productName,
			Integer productType, Integer cardType, Long denomination) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productType = productType;
		this.cardType = cardType;
		this.denomination = denomination;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType) {
		this.productType = productType;
	}
	public Integer getCardType() {
		return cardType;
	}
	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}
	public Long getDenomination() {
		return denomination;
	}
	public void setDenomination(Long denomination) {
		this.denomination = denomination;
	}
	
}
