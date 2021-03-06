package com.abin.lee.lock.curator.service.test;

import com.abin.lee.lock.common.DateUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by abin on 2017/4/23 2017/4/23.
 * lock-svr
 * com.abin.lee.lock.curator.service.test
 */


public class TestCuratorLock2 {
    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub
        CountDownLatch latch = new CountDownLatch(5);
        String zookeeperConnectionString = "172.16.2.145:2181";
//        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(Integer.MAX_VALUE, 300000);
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                zookeeperConnectionString, retryPolicy);
        client.start();
        System.out.println("客户端启动。。。。");
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.submit(new MyLock("client" + i, client, latch));
        }
        exec.shutdown();
        latch.await();
        System.out.println("所有任务执行完毕");
        client.close();
        System.out.println("客户端关闭。。。。");
    }

    static class MyLock implements Runnable {
        private String name;
        private CuratorFramework client;
        private CountDownLatch latch;

        public MyLock(String name, CuratorFramework client, CountDownLatch latch) {
            this.name = name;
            this.client = client;
            this.latch = latch;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            InterProcessMutex lock = new InterProcessMutex(client,
                    "/test_group");
            try {
                System.out.println("----------" + this.name + "开始获取到锁----------" + DateUtil.getYMDHMSTime());
                if (lock.acquire(1200, TimeUnit.SECONDS)) {
                    System.out.println("----------" + this.name + "获取到锁----------" + DateUtil.getYMDHMSTime());
                    try {
                        // do some work inside of the critical section here
                        System.out.println("----------" + this.name
                                + "获得资源----------");
                        System.out.println("----------" + this.name
                                + "正在处理资源----------");
                        Thread.sleep(1000 * 1000);
                        System.out.println("----------" + this.name
                                + "资源使用完毕----------");
                        latch.countDown();
                    } finally {
                        lock.release();
                        System.out.println("----------" + this.name
                                + "释放----------" + DateUtil.getYMDHMSTime());
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}