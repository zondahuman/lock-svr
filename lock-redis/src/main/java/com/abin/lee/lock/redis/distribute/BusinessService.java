package com.abin.lee.lock.redis.distribute;

import com.abin.lee.lock.redis.service.RedisService;
import com.abin.lee.lock.redis.service.impl.RedisServiceImpl;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by abin on 2017/4/21 2017/4/21.
 * lock-svr
 * com.abin.lee.lock.redis.distribute
 */
public class BusinessService {
    private static final String host = "business_";

    static RedisService redisService = new RedisServiceImpl();

    public static void main(String[] args) {
        for(int i=0;i<1000;i++){
            Thread[] threads = new Thread[10];
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
                    start(threadLocalRandom.nextInt(10)+"");
//                    start(1+"");
                }
            };
            runnable.run();

        }

    }


    public static void start(String applicationId){
        String lockKey = host+"_"+applicationId;
        System.out.println("applicationId="+applicationId+" , lockKey="+lockKey);
        try {

            boolean lockStatus =  redisService.acquireLock(lockKey, 1);
            System.out.println("applicationId="+applicationId+" , lockStatus="+lockStatus);
            if(!lockStatus) {
                return;
            }else{
                System.out.println("applicationId="+applicationId+" , lockKey="+lockKey + "------------locked--start");
                Thread.sleep(3000);
                System.out.println("applicationId="+applicationId+" , lockKey="+lockKey + "------------locked--end");

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("e="+ e);
        } finally {
            // 释放锁
            try {
                redisService.releaseLock(lockKey);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("start-release-redislock-failure="+ e);
            }
        }

    }
}
