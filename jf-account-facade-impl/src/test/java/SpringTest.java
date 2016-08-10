import com.berbon.jfaccount.Dao.ChargeOrderDao;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Test
    public void testDao(){
        System.out.println(dao.hello());
    }
}
