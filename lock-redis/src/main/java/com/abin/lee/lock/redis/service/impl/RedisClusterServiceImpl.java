package com.abin.lee.lock.redis.service.impl;

import com.abin.lee.lock.redis.service.RedisService;
import com.abin.lee.lock.redis.service.util.SerializeUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by bml on 2016/1/14.
 */
//@Service
public class RedisClusterServiceImpl extends JedisCluster implements RedisService {

    public RedisClusterServiceImpl(Set<HostAndPort> nodes, GenericObjectPoolConfig poolConfig) {
        super(nodes, poolConfig);
    }

    /**
     * 设置对象，有超时时间
     *
     * @param key
     * @param seconds
     * @param object
     */
    public void setObject(final String key, final int seconds, final Object object) {
        setex(key.getBytes(), seconds, SerializeUtil.serialize(object));
    }

    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    public Object getObject(final String key) {
        return SerializeUtil.unserialize(get(key.getBytes()));
    }

    @Override
    public void setObject(String key, Object object) {
        set(key.getBytes(), SerializeUtil.serialize(object));
    }

    @Override
    public void hdel(String key, String field) {
        hdel(key, field);
    }

    @Override
    public void hsetObject(String key, String field, Object value) {
        hset(key.getBytes(), field.getBytes(), SerializeUtil.serialize(value));
    }

    @Override
    public <T> T hgetObject(String key, String field) {
        return (T) SerializeUtil.unserialize(hget(key.getBytes(), field.getBytes()));
    }

    @Override
    public Set<String> keys(String keyPattern) {
        throw new RuntimeException("不支持的Redis操作");
    }

    @Override
    public void hdelObject(String key, String field) {
        hdel(key.getBytes(), field.getBytes());
    }

    @Override
    public Object runScript(String script, List<String> keys, List<String> args) {
        throw new RuntimeException("不支持的Redis操作");
    }

    @Override
    public Boolean acquireLock(String lock, long expired) {
        throw new RuntimeException("不支持的Redis操作");
    }

    @Override
    public void releaseLock(String lock) {
        throw new RuntimeException("不支持的Redis操作");
    }

    @Override
    public long rpushx(String key, String value, int seconds) {
        long length = rpushx(key, value);
        expire(key, seconds);
        return length;
    }

    @Override
    public long rpush(String key, int seconds, String... value) {
        long length = rpush(key, value);
        expire(key, seconds);
        return length;
    }

    @Override
    public void ltrim(String key, int start, int end) {
        ltrim(key, start, end);
    }

    @Override
    public List<String> lrange(String key, int start, int end) {
        return lrange(key, start, end);
    }

    public Long decr(final String key) {
        return decr(key);
    }

    public Map<String, String> hgetAll(final String key) {
    return hgetAll(key);
    }

    public Long hincrBy(final String key, final String field, final long value) {
        return hincrBy(key, field, value);
    }


    public Long decrBy(final String key, final long value) {
        return decrBy(key, value);
    }

    public String set(final String key, final String value, final String nxxx, final String expx, final int time) {
        return set(key, value, nxxx, expx, time);
    }

    public String hmset(final String key, final Map<String, String> hash) {
        return hmset(key, hash);
    }

    public String rpop(final String key) {
        return rpop(key);
    }

}
