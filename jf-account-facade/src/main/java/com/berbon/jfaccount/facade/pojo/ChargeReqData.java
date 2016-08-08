package com.berbon.jfaccount.facade.pojo;

/**
 * Created by chj on 2016/8/8.
 */

/**
 * 充值请求，需要的数据
 */
public class ChargeReqData {

    /**用户请求协议
     */
    private String userCode;

    private String attach;

    private int srcChannel;

    /**
     * 一下参数见
     * http://showdoc.berbon.com/index.php/Home/item/show?page_id=606&item_id=39
     */
    private int type;
    private String bindNo;;
    private int cardType;
    private String cardNo;
    private String cvv;
    private String expireDate;
    private String realName;
    private String identityNo;
    private String mobileNo;
    private int amount;
    private String bankId;


    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBindNo() {
        return bindNo;
    }

    public void setBindNo(String bindNo) {
        this.bindNo = bindNo;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getSrcChannel() {
        return srcChannel;
    }

    public void setSrcChannel(int srcChannel) {
        this.srcChannel = srcChannel;
    }
}
