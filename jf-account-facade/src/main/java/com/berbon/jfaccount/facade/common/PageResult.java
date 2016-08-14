package com.berbon.jfaccount.facade.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chj on 2016/8/14.
 */
public class PageResult<T> implements Serializable {
    public int pageNo;
    public int pageSize;
    public int total;
    public List<T> listData;

}
