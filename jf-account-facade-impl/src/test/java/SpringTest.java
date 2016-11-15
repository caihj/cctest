import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Dao.UserActFlowDao;
import com.berbon.jfaccount.Dao.UserBaseInfoDao;
import com.berbon.jfaccount.Dao.WithdrawOrderDao;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.pojo.PayFlowData;
import com.berbon.jfaccount.facade.pojo.WithdrawOrderInfo;
import com.berbon.jfaccount.pojo.UserActFlow;
import com.sztx.se.rpc.dubbo.client.DubboClient;
import com.sztx.se.rpc.dubbo.client.DubboClientFactory;
import com.sztx.usercenter.rpc.api.domain.out.UserVO;
import com.sztx.usercenter.rpc.api.service.QueryUserInfoService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by chj on 2016/8/10.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Component
@Transactional
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*.xml"})
public class SpringTest extends TestCase {

    @Autowired
    private ChargeOrderDao dao;


    @Autowired
    private WithdrawOrderDao withdrawOrderDao;

    @Autowired
    private UserActFlowDao flowDao;

    @Autowired
    private DubboClientFactory dubboClient;


    @Autowired
    private UserBaseInfoDao baseInfoDao;


    @Test
    public void testDao(){
        System.out.println(dao.hello());
    }


    @Test
    public void testUserInfo(){

        QueryUserInfoService queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        UserVO userVo = queryUserInfoService.getUserInfo("83986576",90);

        System.out.println(JSONObject.toJSONString(userVo));
        System.out.println(userVo.getUserCode());

    }

    @Test
    public void testInsert(){
        WithdrawOrderInfo orderInfo = new WithdrawOrderInfo();

        orderInfo.setUsercode("83986576");
        orderInfo.setAmount(100);
        orderInfo.setBindNo("BC154");
        orderInfo.setCardNo("6226014546464");
        orderInfo.setCreatetime(new Date());
        orderInfo.setState(WithdrawOrderDao.OrderState.init.state);
        orderInfo.setStateDesc(WithdrawOrderDao.OrderState.init.desc);
        orderInfo.setOrderId("201608291231");
        orderInfo.setTradOrderId("16243213");
        orderInfo.setIp("192.168.1.1");


        withdrawOrderDao.newOrder(orderInfo);
    }


    @Test
    public void t1() throws ParseException {

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        flowDao.getCount(sd.parse("2016-08-01 00:00:00"), sd.parse("2016-09-18 23:59:59"), "38828417", "1021201608040000052767");

    }

    @Test
    public void t2() throws ParseException {

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<UserActFlow> flows = flowDao.query(0, 10, sd.parse("2016-08-01 00:00:00"), sd.parse("2016-08-08 23:59:59"), "83986576", "");
        System.out.println(JSONObject.toJSONString(flows));

    }

    @Autowired
    private AccountFacade accountFacade;

    @Test
    public void t3() throws ParseException {

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<PayFlowData> flows = accountFacade.queryHisPayFlow(20, 30, sd.parse("2016-08-01 00:00:00"), sd.parse("2016-08-08 23:59:59"), "83986576", "");
        System.out.println(JSONObject.toJSONString(flows));

    }

    @Test
    public void testBaseInfoDao(){

        System.out.println(baseInfoDao.checkisAuth("83986576"));
        System.out.println(JSONObject.toJSONString(baseInfoDao.getPartInfo("83986576")));
        baseInfoDao.makeUserVerify(101L,1);

    }




}
