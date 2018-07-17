package com.welab.redis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import sun.nio.ch.ThreadPool;

/**
 * @author markfeng
 * @version v1.0
 * @desc 业务控制类
 * @date 2018/6/14 下午3:05
 */
public class RedisDistrubuteLock implements Lock,Watcher {

    private static Jedis jedis=null;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private String lockKey;
    private String requestId;
    private long expireTime=5000L;
    private static final Long RELEASE_SUCCESS = 1L;
    private static CountDownLatch countDownLatch=null;

    public RedisDistrubuteLock(String lockKey,String requestId,long expireTime){
        this.lockKey=lockKey;
        this.requestId=requestId;
        this.expireTime=expireTime;
        countDownLatch=new CountDownLatch(1);
        if(null == jedis){
            jedis= new Jedis("127.0.0.1",6379);
        }

    }

    public static String  lock(String key,long timeout) {

        long expireTime=System.currentTimeMillis()+timeout;
        String value = UUID.randomUUID().toString();
        Jedis jedis = JedisPoolUtil.getJedis();
        try{

        while (System.currentTimeMillis()<expireTime){

            long re = jedis.setnx(key,value);
            if(1 == re){
                return value;
            }
            Thread.sleep(1000);

        }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return null;
       /* if(tryLock()){
            System.out.println(Thread.currentThread().getName()+"获取锁成功");
        }else {
            System.out.println(Thread.currentThread().getName()+"获取锁失败,请等待");

            try {
                this.countDownLatch.await();
                System.out.println(Thread.currentThread().getName()+"开始重新获取锁");
                lock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

    }

    public static boolean releaseLock(String key,String value){
        Jedis jedis = JedisPoolUtil.getJedis();
        try {
            while (true){
                jedis.watch(key);
                if(value.equals(jedis.get(key))){
                    Transaction transaction = jedis.multi();
                    transaction.del(key);
                    List<Object> list = transaction.exec();
                    if(null == list){
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName()+"释放锁锁成功"+value);
                    return true;
                }

                jedis.unwatch();
            }

        }catch (Exception e){

        }finally {
            jedis.close();
        }
        return false;
    }

    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    @Override
    public void unlock() {
        /*String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));*/
        Long result= jedis.del(lockKey);

        if (RELEASE_SUCCESS.equals(result)) {
            this.countDownLatch.countDown();
            System.out.print("解锁成功");
        }
       // return false;

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }


    public static void main(String[] args) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String value = RedisDistrubuteLock.lock("redisLock",50000);
                System.out.println(Thread.currentThread().getName()+"获得锁成功"+value);
                RedisDistrubuteLock.releaseLock("redisLock",value);

            }
        };

    for(int i=0;i<100;i++){
        Thread thread=new Thread(runnable);
        thread.start();
    }


        /*

        RedisDistrubuteLock redisDistrubuteLock = new RedisDistrubuteLock("fxzcollege1111", UUID.randomUUID().toString(),500000);
        if(redisDistrubuteLock.tryLock()){
            System.out.println("获取锁成功");
        }else {
            System.out.println("获取锁失败,进入等待");
        }*/
    }
}
