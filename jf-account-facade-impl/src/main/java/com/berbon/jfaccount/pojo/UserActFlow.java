package com.berbon.jfaccount.pojo;


import java.sql.Timestamp;

/**
 * Created by chj on 2016/9/14.
 *
 * 通付 资金流水表，和数据库定义一模一样
 */
public class UserActFlow {

    public String	history_no;
    public String	pay_no;
    public String	pay_flow_no;
    public String	pay_type;
    public String	act_no;
    public Integer	user_id;
    public Integer	act_type;
    public String	cur_type;
    public Integer	dc_type;
    public Long	act_balance;
    public Long	freeze_balance;
    public Long	tran_amount;
    public Long	freeze_amount;
    public String	inner_remark;
    public String	user_remark;
    public Timestamp trade_time;
    public String	trade_msg;
    public String	acting_no;
    public Timestamp create_time;
    public Timestamp	modify_time;
    public Integer	real_bank_type;
    public String	bank_name;
    public String	bank_act;
    public String	bank_act_name;
    public String	bank_card_tail;
    public Integer	order_type;
    public String	payer_login_no;
    public Integer	payer_user_id;
    public Integer	payer_user_type;
    public String	payer_login_name;
    public String	payee_login_no;
    public Integer	payee_user_id;
    public Integer	payee_user_type;
    public String	payee_login_name;
    public String	trade_no;
    public String	out_trade_no;
    public String	good_name;
    public String	body;
    public String	pos_no;
    public String	send_tran_no;
    public String	bank_tran_no;
    public Integer	payee_real_bank_type;
    public String	payee_bank_name;
    public String	payee_bank_act;
    public String	payee_bank_act_name;
    public String	payee_bank_card_tail;
    public String	opposite_side;
    public Long	modify_count;
    public Integer	goods_type;
    public Integer	pay_flow_type;
    public String	plat_partner;
    public Integer	isvisiable;
}
