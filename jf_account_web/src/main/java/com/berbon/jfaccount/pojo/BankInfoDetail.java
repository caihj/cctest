package com.berbon.jfaccount.pojo;

import com.sztx.pay.center.rpc.api.domain.BankInfo;
import com.sztx.pay.center.rpc.api.domain.BankPayLimitInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chj on 2016/8/4.
 */
public class BankInfoDetail extends BankInfo {

    public BankInfoDetail(){

    }

    public BankInfoDetail(BankInfo info,BankPayLimitInfo limit){
        this.bankName=info.getBankName();
        this.bankType = info.getBankType();
        this.swiftCode =info.getSwiftCode();
        this.cardType= info.getCardType();
        this.setBankId(info.getBankId());
        this.setSmallLogoUrl(info.getSmallLogoUrl());
        this.setBigLogoUrl(info.getBigLogoUrl());
        this.setPassClipherPay(info.isPassClipherPay());

        if(limit!=null && limit.getLimitInfos()!=null) {
            for (BankPayLimitInfo.LimitInfo tmpLimit : limit.getLimitInfos()) {
                this.limitInfo.add(new LimitInfo(tmpLimit));
            }
        }
    }

    public BankInfoDetail(BankInfoDetail detail){

        this.bankName=detail.getBankName();
        this.bankType = detail.getBankType();
        this.swiftCode =detail.getSwiftCode();
        this.cardType= detail.getCardType();
        this.setBankId(detail.getBankId());
        this.setPassClipherPay(detail.isPassClipherPay());
        this.setSmallLogoUrl(detail.getSmallLogoUrl());
        this.setBigLogoUrl(detail.getBigLogoUrl());

        if(limitInfo!=null ) {
            for (LimitInfo limit : limitInfo) {
                this.limitInfo.add(new LimitInfo(limit));
            }
        }
    }

    public List<LimitInfo> limitInfo =new ArrayList<>();

    public static class LimitInfo {

        public LimitInfo(BankPayLimitInfo.LimitInfo  limitInfo){
            this.singleLimit = limitInfo.getSingleLimit();
            this.dayLimit = limitInfo.getDayLimit();
            this.monthLimit = limitInfo.getMonthLimit();
            this.desc = limitInfo.getDesc();
        }

        public LimitInfo(LimitInfo limitInfo){
            this.singleLimit = limitInfo.singleLimit;
            this.dayLimit = limitInfo.dayLimit;
            this.monthLimit = limitInfo.monthLimit;
            this.desc = limitInfo.desc;
        }

        /**
         * 单笔限额(单位分)
         */
        public Long singleLimit;
        /**
         * 日限额(单位分)
         */
        public Long dayLimit;

        /**
         * 月限额(单位分)
         */
        public Long monthLimit;
        /**
         * 描述
         */
        public String desc;
    }

}
