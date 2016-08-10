import java.util.HashMap;

/**
 * Created by chj on 2016/8/8.
 */

public class Test3 {

    public HashMap<String, Boolean> map = new HashMap<String, Boolean>();

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        Test3 test3 = new Test3();
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("key", "测试,反正Boolean肯定存不下String");
        Test3.class.getField("map").set(test3, map2);
        System.out.println((String)(Object)test3.map.get("key"));

    }
}

