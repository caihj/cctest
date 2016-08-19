package com.berbon.jfaccount.comm;

/**
 * Created by chj on 2016/8/17.
 */

/**
 * 2000	缴费话费充值
 2001	话费充值web
 2002	话费充值手机端
 2003	话费优势充值
 2004	游戏币web
 2005	游戏币手机端
 2006	交通罚款
 2007	话费流量
 2008	宽带缴费
 2009	电影票
 2010	宽带新装
 2011	加油卡
 2012	水电煤
 2013	缴费商充值
 2014	缴费商转账
 2015	缴费商佣金转入
 2016	缴费商佣金转出
 */
public enum BusinessType {
  type_2000(2000,"缴费话费充值"),
  type_2001(2001,"话费充值web"),
  type_2002(2002,"话费充值手机端"),
  type_2003(2003,"话费优势充值"),
  type_2004(2004,"游戏币web"),
  type_2005(2005,"游戏币手机端"),
  type_2006(2006,"交通罚款"),
  type_2007(2007,"话费流量"),
  type_2008(2008,"宽带缴费"),
  type_2009(2009,"电影票"),
  type_2010(2010,"宽带新装"),
  type_2011(2011,"加油卡"),
  type_2012(2012,"水电煤"),
  type_2013(2013,"缴费商充值"),
  type_2014(2014,"缴费商转账"),
  type_2015(2015,"缴费商佣金转入"),
  type_2016(2016,"缴费商佣金转出");

  BusinessType(int type,String desc){
   this.type = type;
   this.desc = desc;
  }

  public int  type;
  public String desc;
}
