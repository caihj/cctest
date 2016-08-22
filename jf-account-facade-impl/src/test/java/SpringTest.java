import com.alibaba.fastjson.JSONObject;
import com.berbon.jfaccount.Dao.ChargeOrderDao;
import com.berbon.jfaccount.Service.OrderCacheService;
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
    private OrderCacheService orderCacheService;

    @Autowired
    private DubboClientFactory dubboClient;

    @Test
    public void testDao(){
        System.out.println(dao.hello());
    }

    @Test
    public void testRedis(){
        orderCacheService.hello();
    }


    @Test
    public void testUserInfo(){

        QueryUserInfoService queryUserInfoService = dubboClient.getDubboClient("queryUserInfoService");
        UserVO userVo = queryUserInfoService.getUserInfo("83986576",90);

        System.out.println(JSONObject.toJSONString(userVo));
        System.out.println(userVo.getUserCode());

    }
}
