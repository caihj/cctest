package com.berbon.jfaccount.pojo;

import com.sztx.pay.center.rpc.api.domain.BankInfo;
import com.sztx.pay.center.rpc.api.domain.BankPayLimitInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chj on 2016/8/4.
 */
public class BankInfoDetail extends BankInfo {

    public BankInfoDetail(BankInfo info,BankPayLimitInfo limit){
        this.bankName=info.getBankName();
        this.bankType = info.getBankType();
        this.swiftCode =info.getSwiftCode();
        this.cardType= info.getCardType();
        this.setBankId(info.getBankId());
        this.setPassClipherPay(info.isPassClipherPay());

        if(limit!=null && limit.getLimitInfos()!=null) {
            for (BankPayLimitInfo.LimitInfo tmpLimit : limit.getLimitInfos()) {
                this.limitInfo.add(new LimitInfo(tmpLimit));
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
