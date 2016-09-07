package com.berbon.jfaccount.Service;

import com.berbon.jfaccount.commen.InitBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chj on 2016/9/7.
 */
@Service
public class SysService {

    @Autowired
    private InitBean initBean;

    public boolean useCreditCard(){

        return initBean.userCreditCard;
    }

}
