import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Dao.WithdrawOrderDao;
import com.berbon.jfaccount.facade.pojo.WithdrawOrderInfo;
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

import java.util.Date;

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
    private DubboClientFactory dubboClient;

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
}
