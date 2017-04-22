package com.abin.lee.lock.curator.util;

import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by abin on 2017/4/22 2017/4/22.
 * lock-svr
 * com.abin.lee.lock.curator.service
 */

public class CuratorListenerUtil {
    static CuratorFramework zkClient = null;
    static String nameSpace = "curator";
    static ExecutorService executorService = Executors.newCachedThreadPool();

    static {
        String zkhost = "172.16.2.145:2181";//zk的host
        RetryPolicy rp = new ExponentialBackoffRetry(1000, 3);//重试机制
        Builder builder = CuratorFrameworkFactory.builder().connectString(zkhost)
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

    public static void main(String[] args) throws Exception {
        CuratorListenerUtil ct = new CuratorListenerUtil();
        //ct.getListChildren("/zk/bb");
        //ct.upload("/jianli/123.txt", "D:\\123.txt");
        //ct.createrOrUpdate("/zk/cc334/zzz","c");
        //ct.delete("/qinb/bb");
        //ct.checkExist("/zk");
//        ct.read("/jianli/123.txt");
        zkClient.close();
    }

    public void addListener(CuratorFramework client)throws Exception{
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                switch (newState){
                    case CONNECTED:
                        reConnection(client);
                        break;
                    case RECONNECTED:
                        reConnection(client);
                        break;
                }
            }
        },executorService);
    }


    public void reConnection(CuratorFramework client)  {
        List<ACL> aclList = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).withACL(aclList).forPath(nameSpace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addWatcher(){
        String path = "application";
        String data = "PEOPLE";
        PathChildrenCache watcher = new PathChildrenCache(zkClient, path, true);
        try {
            watcher.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            watcher.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    PathChildrenCacheEvent.Type type = event.getType();
                    switch(type){
                        case CHILD_ADDED:

                            break;
                        case CONNECTION_RECONNECTED:

                            break;
                        case CONNECTION_LOST:

                            break;
                        default:
                            break;
                    }
                }
            },executorService);

            zkClient.setData().forPath(path, data.getBytes("UTF-8"));
            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建或更新一个节点
     *
     * @param path    路径
     * @param content 内容
     **/
    public void createrOrUpdate(String path, String content) throws Exception {
        zkClient.create().creatingParentsIfNeeded().forPath(path);
        zkClient.setData().forPath(path, content.getBytes());
        System.out.println("添加成功！！！");
    }

    /**
     * 删除zk节点
     *
     * @param path 删除节点的路径
     **/
    public void delete(String path) throws Exception {
        zkClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
        System.out.println("删除成功!");
    }

    /**
     * 判断路径是否存在
     *
     * @param path
     **/
    public void checkExist(String path) throws Exception {
        if (zkClient.checkExists().forPath(path) == null) {
            System.out.println("路径不存在!");
        } else {
            System.out.println("路径已经存在!");
        }
    }

    /**
     * 读取的路径
     *
     * @param path
     **/
    public void read(String path) throws Exception {
        String data = new String(zkClient.getData().forPath(path), "gbk");
        System.out.println("读取的数据:" + data);
    }

    /**
     * @param path 路径
     *             获取某个节点下的所有子文件
     */
    public void getListChildren(String path) throws Exception {
        List<String> paths = zkClient.getChildren().forPath(path);
        for (String p : paths) {
            System.out.println(p);
        }
    }

    /**
     * @param zkPath    zk上的路径
     * @param localpath 本地上的文件路径
     **/
    public void upload(String zkPath, String localpath) throws Exception {
        createrOrUpdate(zkPath, "");//创建路径
        byte[] bs = FileUtils.readFileToByteArray(new File(localpath));
        zkClient.setData().forPath(zkPath, bs);
        System.out.println("上传文件成功！");
    }
}