package com.berbon.jfaccount.pojo;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class NotifyData implements Serializable {
	private String service = "paynotify";
	private String version = "1.0.0";
	private String resultCode;
	private String resultMsg;
	private String merId;
	private String merOrderId;
	private String payOrderId;
	private String payValue;
	private String currency = "1";
	private String payTime;
	private String signType = "MD5";
	private String sign;

	public NotifyData() {
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getMerOrderId() {
		return merOrderId;
	}

	public void setMerOrderId(String merOrderId) {
		this.merOrderId = merOrderId;
	}

	public String getPayOrderId() {
		return payOrderId;
	}

	public void setPayOrderId(String payOrderId) {
		this.payOrderId = payOrderId;
	}

	public String getPayValue() {
		return payValue;
	}

	public void setPayValue(String payValue) {
		this.payValue = payValue;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String createSign(String key) {
		Map<String, Object> treeMap = new TreeMap<String, Object>();
		treeMap.put("service", service);
		treeMap.put("version", version);
		treeMap.put("resultCode", resultCode);
		treeMap.put("resultMsg", resultMsg);
		treeMap.put("merId", merId);
		treeMap.put("merOrderId", merOrderId);
		treeMap.put("payOrderId", payOrderId);
		treeMap.put("payValue", payValue);
		treeMap.put("currency", currency);
		treeMap.put("payTime", payTime);
		treeMap.put("signType", signType);
		Iterator<Entry<String, Object>> it = treeMap.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		Entry<String, Object> entry;
		boolean first = true;
		while (it.hasNext()) {
			entry = it.next();
			if (!first) {
				sb.append("&");
			} else {
				first = false;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue());
		}
		String preSign = sb.toString() + "&key=";
		return DigestUtils.md5Hex(preSign + key);
	}
}
