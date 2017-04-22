package com.abin.lee.lock.redis.service.impl;

import com.abin.lee.lock.redis.service.RedisCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class RedisManager {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);

    private JedisPool pool;

    public RedisManager(JedisPool pool) {
        this.pool = pool;
    }

    public <T> T request(RedisCallback<T> callback) {
        Jedis jedis = pool.getResource();
        try {
            jedis.connect();
            return callback.doInRequest(jedis);
        } catch (JedisConnectionException jce) {
            throw jce;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


}
