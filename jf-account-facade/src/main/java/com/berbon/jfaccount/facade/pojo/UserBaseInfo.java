package com.berbon.jfaccount.facade.pojo;

/**
 * Created by chj on 2016/11/11.
 */
public class UserBaseInfo {

    private Integer real_name_verified;
    private String real_name;
    private String identity_num;

    public Integer getReal_name_verified() {
        return real_name_verified;
    }

    public void setReal_name_verified(Integer real_name_verified) {
        this.real_name_verified = real_name_verified;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getIdentity_num() {
        return identity_num;
    }

    public void setIdentity_num(String identity_num) {
        this.identity_num = identity_num;
    }
}
