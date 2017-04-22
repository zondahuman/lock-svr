package com.abin.lee.lock.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by abin
 * Be Created in 2016/5/18.
 */
public interface RedisService {
    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    String set(String key, String value);

    /**
     * 设置值，有超时时间
     *
     * @param key
     * @param seconds
     * @param value
     */
    String setex(String key, int seconds, String value);

    /**
     * 当且仅当 key 不存在时设置值
     *
     * @param key
     * @param value
     */
    Long setnx(String key, String value);

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 设置对象，有超时时间
     *
     * @param key
     * @param seconds
     * @param value
     */
    void setObject(String key, int seconds, Object object);

    /**
     * 设置对象，无超时时间 慎用
     *
     * @param key
     * @param seconds
     * @param value
     */
    void setObject(String key, Object object);

    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    Object getObject(String key);

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    Boolean exists(String key);

    /**
     * 为key设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    Long expire(String key, int seconds);

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    Boolean exists(byte[] key);

    /**
     * 设置哈希值
     *
     * @param key
     * @param field
     * @param value
     */
    Long hset(String key, String field, String value);

    /**
     * 获取哈希值
     *
     * @param key
     * @param field
     * @return
     */
    String hget(String key, String field);

    /**
     * 删除哈希值
     *
     * @param key
     * @param field
     */
    void hdel(String key, String field);

    /**
     * 加一的原子操作
     *
     * @param key
     * @param field
     */
    Long incr(String key);

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    String set(byte[] key, byte[] value);

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    String setex(byte[] key, int seconds, byte[] value);

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    byte[] get(byte[] key);

    /**
     * 删除制定key 值
     *
     * @param key
     */
    Long del(String key);

    /**
     * set add
     *
     * @param key
     */
    Long sadd(String key, String... member);

    /**
     * set member key
     *
     * @param key
     * @param member
     * @return
     */
    Boolean sismember(String key, String member);

    /**
     * list lpush
     *
     * @param key
     */
    Long lpush(String key, String... member);

    /**
     * list lpop
     *
     * @param key
     */
    String lpop(String key);

    /**
     * list llen
     *
     * @param key
     */
    Long llen(String key);

    /**
     * list rpush
     *
     * @param key
     */
    Long rpush(String key, String... member);

    /**
     * 原子性的设置该Key为指定的Value，同时返回该Key的原有值。
     *
     * @param key
     * @param value
     */
    String getSet(String key, String value);

    /**
     * 设置哈希值
     *
     * @param key
     * @param field
     * @param value
     */
    void hsetObject(String key, String field, Object value);

    /**
     * 获取哈希值
     *
     * @param key
     * @param field
     * @param <T>
     * @return
     */
    <T> T hgetObject(String key, String field);

    /**
     * 获得符合给定模式 pattern 的 key
     *
     * @param keyPattern
     * @return
     */
    Set<String> keys(String keyPattern);

    /**
     * 返回集合 key 中的所有成员
     *
     * @param key
     * @return
     */
    Set<String> smembers(String key);

    /**
     * 删除哈希值
     *
     * @param key
     * @param field
     */
    void hdelObject(String key, String field);

    /**
     * 获取哈希所有key值集合
     *
     * @param key
     * @param <T>
     * @return
     */
    Set<String> hkeys(String key);

    /**
     * 运行lua脚本
     *
     * @param script
     * @param keys
     * @param args
     * @return
     */
    Object runScript(String script, List<String> keys, List<String> args);

    /**
     * 加锁
     *
     * @param lock
     * @param expired
     * @return
     */
    Boolean acquireLock(String lock, long expired);

    /**
     * 释放锁
     *
     * @param lock
     */
    void releaseLock(String lock);

    long rpushx(String key, String value, int seconds);

    long rpush(String key, int seconds, String... value);

    void ltrim(String key, int start, int end);

    List<String> lrange(String key, int start, int end);

    /**
     * 做减法
     * @param key
     * @return
     */
    public Long decr(final String key);

    /**
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetAll(final String key);

    /**
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hincrBy(final String key, final String field, final long value);

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public Long decrBy(final String key, final long value);

    /**
     *
     * @param key
     * @param value
     * @param nxxx
     * @param expx
     * @param time
     * @return
     */
    public String set(String key, String value, String nxxx, String expx, int time);

    /**
     *
     * @param key
     * @param hash
     * @return
     */
    public String hmset(final String key, final Map<String, String> hash);

    /**
     *
     * @param key
     * @return
     */
    public String rpop(final String key);

    /**
     * 获取redis剩余过期时间
     * @param key
     * @return
     */
    public Long ttl(final String key);

    /**
     * 获取redis剩余过期时间
     * @param key
     * @return
     */
    public Long ttl(final byte[] key);
}
