package com.abin.lee.lock.redis.service;

import com.abin.lee.lock.context.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * jedis 命令工具
 */
public class RedisUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    private static Map<String, RedisUtil> instances = new HashMap<String, RedisUtil>();

    private ShardedJedisPool shardedJedisPool;
    public ShardedJedisPool getShardedJedisPool() {
        return shardedJedisPool;
    }
    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    private RedisUtil(String beanId) {
        this.shardedJedisPool = SpringContextUtils.getBean(beanId, ShardedJedisPool.class);
    }

    public static RedisUtil getInstance() {
        if(!instances.containsKey("shardedJedisPool")){
            synchronized (RedisUtil.class) {
                if(!instances.containsKey("shardedJedisPool")){
                    instances.put("shardedJedisPool", new RedisUtil("shardedJedisPool"));
                }
            }

        }
        return instances.get("shardedJedisPool");
    }

    /**
     * 扩展接口，直接调用jedis的字符串和对象的封装命令
     */
    public static interface JedisProxy extends JedisCommands, BinaryJedisCommands {

    }

    private static Map<Method, Method> cache = new HashMap<Method, Method>();

    private static Method getMethod(Method m) throws SecurityException, NoSuchMethodException {
        Method ret = cache.get(m);
        if (ret != null)
            return ret;

        Class<ShardedJedis> c = ShardedJedis.class;
        ret = c.getMethod(m.getName(), m.getParameterTypes());
        cache.put(m, ret);
        return ret;
    }

    /**
     * JedisProxy，每次执行方法后都会自动释放连接
     * @return
     */
    public JedisProxy getProxy() {
        Object proxy = Proxy.newProxyInstance(ShardedJedisPool.class.getClassLoader(), new Class[]{JedisProxy.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        ShardedJedisPool pool = getShardedJedisPool();
                        ShardedJedis jedis = null;
                        try {
                            jedis = pool.getResource();
                            if (JedisProxy.class != method.getDeclaringClass()) {
                                return method.invoke(jedis, args);
                            } else {
                                return getMethod(method).invoke(jedis, args);
                            }
                        } finally {
                            jedis.close();
                        }
                    }
                });
        return (JedisProxy) proxy;
    }



}
