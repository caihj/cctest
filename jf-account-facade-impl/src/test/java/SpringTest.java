import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Dao.*;
import com.berbon.jfaccount.Service.MCAutoTranserService;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.pojo.AcquireTransferReq;
import com.berbon.jfaccount.facade.pojo.AcquireTransferRsp;
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
import java.util.Calendar;
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

    @Autowired
    private MasterChildRelateDao masterChildRelateDao;

    @Autowired
    private TransferOrderDao transferOrderDao;

    @Autowired
    private MCAutoTranserService service;

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
        baseInfoDao.makeUserVerify(101L, 1);

    }

    @Test
    public void MCTest(){
        Object obj = masterChildRelateDao.get("");
        System.out.println(JSONObject.toJSONString(obj));
    }

    @Test
    public void transferTest(){

        Calendar cal =  Calendar.getInstance();


        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        Date begin = cal.getTime();

        cal =  Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND, 59);
        Date end  = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(JSONObject.toJSONString(sdf.format(begin)));
        System.out.println(JSONObject.toJSONString(sdf.format(end)));

        Object obj = transferOrderDao.getTotalAmount(begin,end,"100102985","83986576","2020");

        System.out.println(JSONObject.toJSONString(obj));
    }


    @Test
    public void MCTransferTest(){
        AcquireTransferReq req = new AcquireTransferReq();
        req.userCode = "83986576";
        req.amount = 1L;
        req.bussOrderId = "testbuss"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        req.ip = "127.0.0.1";
        AcquireTransferRsp rsp = service.transfer(req);

        System.out.println(JSONObject.toJSONString(rsp));
    }



}
