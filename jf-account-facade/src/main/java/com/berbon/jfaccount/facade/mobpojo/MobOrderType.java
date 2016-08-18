package com.berbon.jfaccount.facade.mobpojo;

/**
 * Created by chj on 2016/8/17.
 */
public enum MobOrderType {

    mobile_charge("话费充值"),
    game_charge("游戏充值");
    MobOrderType(String name){
        this.name = name;
    }
    public String name;
}
