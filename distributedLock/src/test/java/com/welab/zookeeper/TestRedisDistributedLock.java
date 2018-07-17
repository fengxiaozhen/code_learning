package com.welab.zookeeper;

import com.welab.redis.RedisDistrubuteLock;
import java.util.UUID;

/**
 * @author markfeng
 * @version v1.0
 * @desc 业务控制类
 * @date 2018/6/14 下午3:45
 */
public class TestRedisDistributedLock {

    public static void main(String[] args) {

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                String lockKey="redisTest";
                String requestId= UUID.randomUUID().toString();
                long expireTime=500000L;
                RedisDistrubuteLock redisDistrubuteLock=new RedisDistrubuteLock(lockKey,requestId,expireTime);
                redisDistrubuteLock.lock();
                try {
                    System.out.println(Thread.currentThread().getName()+"已经获得了锁，正在处理业务逻辑"+requestId);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    System.out.println(Thread.currentThread().getName()+"释放锁");
                    redisDistrubuteLock.unlock();

                }
            }
        };

        for(int i=0;i<10;i++){
            Thread thread=new Thread(runnable);
            thread.start();
        }

    }

}
