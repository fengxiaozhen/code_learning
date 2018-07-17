package com.welab.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author markfeng
 * @version v1.0
 * @desc 业务控制类
 * @date 2018/7/16 上午10:12
 */
public class JedisPoolUtil {

    public synchronized static Jedis getJedis(){

        return getJedisPool().getResource();
    }

    private static JedisPool getJedisPool(){
        return JedisPoolInnerClass.jedisPool;
    }


    //redis.close已经重写，会自动释放jedis
    public static void releaseJedis(){

    }


    private static class JedisPoolInnerClass{
        private static JedisPool jedisPool=init();
        private static JedisPool init(){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(500);
            jedisPoolConfig.setMaxIdle(5);
            jedisPoolConfig.setMaxWaitMillis(1000*5);
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPool=new JedisPool(jedisPoolConfig,"127.0.0.1",6379,1000*5);
            return jedisPool;
        }
    }


    public static void main(String[] args) {
        Jedis jedis = null;

            int loop = 1;
            int count =0;
            while (loop < 20){
                try{
                long start = System.currentTimeMillis();
                jedis=JedisPoolUtil.getJedis();
                jedis.set("test","test11");
                String value=jedis.get("test");
                long end=System.currentTimeMillis();
                long time=end-start;
                count++;
                //loop++;
                System.out.println("count=" +count+"   get the value="+value +"  time="+time);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(null != jedis){
                        jedis.close();
                    }

                }
            }

    }
}
