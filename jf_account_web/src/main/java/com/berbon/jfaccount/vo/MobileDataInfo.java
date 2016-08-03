package com.berbon.jfaccount.vo;

import java.util.List;

import com.berbon.mobiledata.pojo.MobAttribution;
import com.berbon.mobiledata.pojo.MobileDataProduct;
/**
 * 手机流量充值获取
 * @author baijt
 *
 * 2015年10月27日
 */
public class MobileDataInfo {
	private MobAttribution mobAttribution;
	private List<MobileDataProduct> mobileDataProducts;
	
	
	
	public MobileDataInfo() {
		super();
	}
	public MobileDataInfo(MobAttribution mobAttribution, List<MobileDataProduct> mobileDataProducts) {
		super();
		this.mobAttribution = mobAttribution;
		this.mobileDataProducts = mobileDataProducts;
	}
	public MobAttribution getMobAttribution() {
		return mobAttribution;
	}
	public void setMobAttribution(MobAttribution mobAttribution) {
		this.mobAttribution = mobAttribution;
	}
	public List<MobileDataProduct> getMobileDataProducts() {
		return mobileDataProducts;
	}
	public void setMobileDataProducts(List<MobileDataProduct> mobileDataProducts) {
		this.mobileDataProducts = mobileDataProducts;
	}
	
	
	
	
	

}
