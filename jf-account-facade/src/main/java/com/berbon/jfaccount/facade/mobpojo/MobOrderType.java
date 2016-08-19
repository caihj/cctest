package com.berbon.jfaccount.facade.mobpojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/17.
 */
public enum MobOrderType implements Serializable {

    mobile_charge("话费充值"),
    game_charge("游戏充值");
    MobOrderType(String name){
        this.name = name;
    }
    public String name;
}
