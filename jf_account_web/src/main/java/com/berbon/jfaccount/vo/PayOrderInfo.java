package com.berbon.jfaccount.vo;


public class PayOrderInfo {
	private String password;
	private String orderId;
    private Long priceAmount;
    private Integer encType;  // 1 md5 (passward+salt) ,2 yhbf,3 证书，4 MD5((passward+salt)+disturse)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Long getPriceAmount() {
		return priceAmount;
	}
	public void setPriceAmount(Long priceAmount) {
		this.priceAmount = priceAmount;
	}
	public Integer getEncType() {
		return encType;
	}
	public void setEncType(Integer encType) {
		this.encType = encType;
	}
    
	public static void main(String[] args) {
		System.err.println("http://jf.berbon.com/partner-web/Home/Index?__r=1445248381".split("\\?")[0]);
	}
}
