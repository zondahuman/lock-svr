package com.abin.lee.lock.redis.distribute.test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by abin on 2017/4/22 2017/4/22.
 * lock-svr
 * com.abin.lee.lock.redis.distribute.test
 */
public class RandomTest {

    public static void main(String[] args) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        System.out.println(threadLocalRandom.nextInt(100)+"");
    }

}
