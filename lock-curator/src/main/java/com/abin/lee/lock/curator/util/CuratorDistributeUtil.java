package com.abin.lee.lock.curator.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Paths;

/**
 * Created by abin on 2017/4/22 2017/4/22.
 * lock-svr
 * com.abin.lee.lock.curator.service
 */

public class CuratorDistributeUtil {
    private final static Logger logger = LoggerFactory.getLogger(CuratorDistributeUtil.class);
    private CuratorFramework zk;

    public static class LockException extends RuntimeException {
        LockException(String message) {
            super(message);
        }
    }

    public static class Lock implements AutoCloseable {
        private CuratorFramework zk;
        private String node;

        Lock(CuratorFramework zk, String node) throws Exception {
            this.zk = zk;
            this.node = node;
            createEphemeralNode(zk, node);

        }

        private void createEphemeralNode(CuratorFramework zk, String path) throws Exception{
            try {
                zk.create().withMode(CreateMode.EPHEMERAL).forPath(path, path.getBytes());

            } catch ( KeeperException.NodeExistsException ignore ) {
                // ignore
                logger.info("Path [{}] exists", path);
                throw new LockException(String.format("acquire lock of %s failed", node));
            }
        }

        public void deleteEphemeralNode() throws Exception{
            try {
                zk.delete().forPath(this.node);
            } catch (KeeperException.NoNodeException ignore) {
                logger.info("delete path {} not exists", node);
            }
        }

        @Override
        public void close() throws Exception {
            //if (mutex != null && mutex.isAcquiredInThisProcess()) {
            //    mutex.release();
            //    logger.info("lock {} release", node);
            //}
            deleteEphemeralNode();
        }

    }

    //@Value("${zookeeper.connect}")
    private String hosts = "172.16.2.145:2181";
    //@Value("${zookeeper.retry}")
    private Integer retry = 3;
    //@Value("${zookeeper.timeout.connect}")
    private Integer connectTimeout = 30;
    //@Value(("${zookeeper.timeout.session}"))
    private Integer sessionTimeout = 300;
    //@Value("${zookeeper.root}")
    private String root = "/lock/distribute";

    @PostConstruct
    public void start() throws Exception {
//        RetryForever retryPolicy = new RetryForever(1000);
//        zk = CuratorFrameworkFactory.newClient(hosts, retryPolicy);
//        zk.start();
//        createPersistentNode(this.root);
    }

    @PreDestroy
    private void stop() throws Exception {
        if(zk != null) {
            zk.close();
        }
    }

    public Lock tryLock(Long id) throws Exception {
        String node = Paths.get(root, id.toString()).toAbsolutePath().toString();
        return new Lock(zk, node);
    }


    public void createPersistentNode(String path) throws Exception{
        try{
            zk.create().creatingParentsIfNeeded().forPath(path, path.getBytes());

        }catch ( KeeperException.NodeExistsException ignore ) {
            // ignore
            logger.info("Path [{}] exists", path);
        }
    }
}