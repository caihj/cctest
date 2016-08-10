package com.berbon.jfaccount.impl;

import com.berbon.jfaccount.facade.AccountMobileFacade;
import org.springframework.stereotype.Service;

/**
 * Created by chj on 2016/8/10.
 */
@Service
public class AccountMobileFacadeIMpl implements AccountMobileFacade {
    @Override
    public String echo(String in) {
        return in;
    }
}
