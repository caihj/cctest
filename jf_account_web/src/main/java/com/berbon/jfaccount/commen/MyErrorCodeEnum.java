package com.berbon.jfaccount.commen;

public enum MyErrorCodeEnum {
	system_error("0000", "服务忙，请稍后再试或联系客服"), 
	param_eror("0001",  "参数缺少或错误"),
	not_login("0002", "请重新登录！"), 
	amount_eror("0003",  "输入金额有误"),
	init_eror("0004",  "下单失败,请重试"),
	pay_amount_error("0005",  "该笔订单已支付"),
	pay_sucess("0006",  "支付金额不一致"),
	paypas_error("0007",  "支付密码错误"),
	sucess("1001", "处理成功");
	private String errCode;
	private String outDesc;

	MyErrorCodeEnum(String errCode, String outDesc) {
		this.errCode = errCode;
		this.outDesc = outDesc;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getOutDesc() {
		return outDesc;
	}

	public void setOutDesc(String outDesc) {
		this.outDesc = outDesc;
	}
}
