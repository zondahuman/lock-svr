package com.abin.lee.lock.curator.service;

import com.abin.lee.lock.common.DateUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * Created by abin on 2017/4/23 2017/4/23.
 * lock-svr
 * com.abin.lee.lock.curator.service
 */
public class CuratorDistributeLock {
    public static final String path = "abin";
    static CuratorFramework zkClient = null;
    static String nameSpace = "distribute";
    static InterProcessMutex lock = null;

    static {
        String zkhost = "172.16.2.145:2181";//zk的host
        RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);//重试机制
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(zkhost)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(5000)
                .retryPolicy(rp)
                .namespace(nameSpace);
        zkClient = builder.build();
        zkClient.start();// 放在这前面执行
        try {
            zkClient.blockUntilConnected();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        zkClient.usingNamespace(nameSpace);
    }

    public static Boolean acquireLock(CuratorFramework client, Integer lockTime, String lockKey)  {
        InterProcessMutex lock = new InterProcessMutex(client, "/"+lockKey);
        Boolean flag = Boolean.FALSE;
        try {
            System.out.println("flag.start=" + flag +"---"+ DateUtil.getYMDHMSTime());
            flag = lock.acquire(lockTime, TimeUnit.SECONDS);
            System.out.println("flag.end=" + flag +"---"+ DateUtil.getYMDHMSTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void releaseLock(CuratorFramework client, String lockKey){
        InterProcessMutex lock = new InterProcessMutex(client, "/"+lockKey);
        boolean flag = lock.isAcquiredInThisProcess();
        System.out.println("lock.isAcquiredInThisProcess()="+ flag +"------"+ DateUtil.getYMDHMSTime());
        try {
            if(flag){
                System.out.println("lock.release().start"+ DateUtil.getYMDHMSTime());
                lock.release();
                System.out.println("lock.release().end"+ DateUtil.getYMDHMSTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        String lockKey = "tcp_1";
        boolean flag = acquireLock(zkClient, 10, lockKey);
        Thread.sleep(35*1000);
        System.out.println("main-flag="+flag);
        releaseLock(zkClient, lockKey);
    }



}
