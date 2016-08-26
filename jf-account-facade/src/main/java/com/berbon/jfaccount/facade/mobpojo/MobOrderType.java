package com.berbon.jfaccount.facade.mobpojo;

import java.io.Serializable;

/**
 * Created by chj on 2016/8/17.
 */
public enum MobOrderType implements Serializable {

    game_charge(1,"游戏充值"),
    mobile_charge(2,"话费充值");
    MobOrderType(int type,String name){
        this.type = type;
        this.name = name;
    }
    public int type;
    public String name;

    public static MobOrderType parseInt(int type){
        for(MobOrderType t :values()){
            if(t.type==type)
                return t ;
        }
        return null;
    }
}
