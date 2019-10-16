package com.atguigu.gmall.manag;

import com.atguigu.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import sun.awt.geom.AreaOp;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {
@Autowired
    RedisUtil redisUtil;
    @Test
    public void contextLoads() {
    }
    @Test
    public void redisTest(){
        /*Jedis jedis=redisUtil.getJedis();

        System.out.println(jedis.ping());*/
        Jedis jedis = new Jedis("120.78.175.32", 6379);
        String ping=jedis.ping();
        System.out.println(ping);
        //关闭连接
        jedis.close();

    }
}
