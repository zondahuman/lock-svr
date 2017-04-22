package com.abin.lee.lock.redis.service;

import redis.clients.jedis.Jedis;

public interface RedisCallback<T> {

    T doInRequest(Jedis jedis);

}