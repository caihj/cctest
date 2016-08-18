package com.berbon.jfaccount.facade.pojo;



import java.io.Serializable;

/**
 * Created by chj on 2016/8/10.
 */
public class TransferCheckReq implements Serializable {

    protected String fromUserCode;
    protected String toUserCode;
    protected long amount;
    protected String realName;
    protected String phone;

    public boolean validateSelf(){
        if(fromUserCode==null ||fromUserCode.isEmpty()){
            return false;
        }
        if(amount <=0){
            return false;
        }
        if(toUserCode==null ||toUserCode.isEmpty()){
            return false;
        }

        return true;
    }

    public String getFromUserCode() {
        return fromUserCode;
    }

    public void setFromUserCode(String fromUserCode) {
        this.fromUserCode = fromUserCode;
    }

    public String getToUserCode() {
        return toUserCode;
    }

    public void setToUserCode(String toUserCode) {
        this.toUserCode = toUserCode;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
