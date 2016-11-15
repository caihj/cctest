package com.berbon.jfaccount.facade.pojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/11/11.
 */
public class UserBaseInfo implements Serializable{

    private Long id;
    private Integer realNameVerified;
    private String realName;
    private String identityNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Integer getRealNameVerified() {
        return realNameVerified;
    }

    public void setRealNameVerified(Integer realNameVerified) {
        this.realNameVerified = realNameVerified;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(String identityNum) {
        this.identityNum = identityNum;
    }
}
