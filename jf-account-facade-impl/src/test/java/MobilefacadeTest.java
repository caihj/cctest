import com.alibaba.fastjson.JSON;
import com.berbon.jfaccount.facade.AccountFacade;
import com.berbon.jfaccount.facade.AccountMobileFacade;
import com.berbon.jfaccount.facade.mobpojo.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chj on 2016/8/25.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Component
@Transactional
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*.xml"})
public class MobilefacadeTest  extends TestCase {

    @Autowired
    AccountMobileFacade mobileFacade;


    @Test
    public void t1(){

        CreateChargeReq req = new CreateChargeReq();
        req.setUserCode("83986576");
        req.setPayAmount(100L);
        req.setFromMob("15882144369");
        req.setFromIp("192.168.1.1");

        Object obj = mobileFacade.createChargeOrder(req);
        System.out.println(JSON.toJSONString(obj));
    }

    @Test
    public void t2(){
        GetValMsgReq req = new GetValMsgReq();
        req.setUserId("83986576");
        req.setIp("192.168.1.1");
        req.setOrderId("CZ16082814012658401");
        req.setPayAmount("100");
        req.setBindNo("");


        mobileFacade.getChargeValMsg(req);
    }


    /**
     * 创建转账订单
     */
    @Test
    public void t3(){

        CreateTransferOrderReq req = new CreateTransferOrderReq();

        req.setPayerUserId("83986576");
        req.setPayeeUserId("100102991");
        req.setPayAmount(100);
        req.setMob("15945651287");
        req.setIp("192.168.1.1");

        mobileFacade.creatTransferOrder(req);

    }

    @Test
    public void t4(){

        mobileFacade.doTransfer("ZZ16082514110667101", "192.168.1.1");
    }

    @Test
    public void t5(){
        boolean ret  = mobileFacade.queryCanWithdrawals("83986576");
        System.out.println("是否可以提现" + ret);
    }

    /**
     * 查询体现银行卡
     */
    @Test
    public void t6(){

        List<BankCardInfo>  cards =  mobileFacade.queryWithdrawalBankCard("83986576");
        System.out.println("体现银行卡+"+JSON.toJSONString(cards));

    }

    @Test
    public  void  t8(){
        long balance = mobileFacade.queryBalance("83986576");
        System.out.println("余额是："+balance);
    }


    @Test
    public void t9(){

        mobileFacade.BalancePay("100102991", "03020160828000006", MobOrderType.game_charge, "192.168.1.1");
    }

    @Test
    public void t10(){
        mobileFacade.quickPay("83986576", "03020160825000009", MobOrderType.game_charge, "192.168.1.1","");
    }
}
