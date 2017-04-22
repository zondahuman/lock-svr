package com.abin.lee.lock.redis.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JedisProxy implements InvocationHandler {

    private final JedisPool jedisPool;

    public JedisProxy(JedisPool pool) {
        this.jedisPool = pool;
    }

    public static JedisCommands newInstance(JedisPool pool) {
        return (JedisCommands) Proxy.newProxyInstance(Jedis.class.getClassLoader(), Jedis.class.getInterfaces(), new JedisProxy(pool));
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        Object result;
        Jedis jedis = obtainJedis();
        try {
            result = m.invoke(jedis, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new JedisException("Unexpected proxy invocation exception: " + e.getMessage(), e);
        } finally {
            returnJedis(jedis);
        }
        return result;
    }

    private Jedis obtainJedis() {
        Jedis jedis;
        jedis = jedisPool.getResource();
        return jedis;
    }

    private void returnJedis(Jedis jedis) {
        try {
            if (jedis.isConnected()) {
                jedis.ping();
                jedisPool.returnResource(jedis);
            } else {
                jedisPool.returnBrokenResource(jedis);
            }
        } catch (JedisException e) {
            jedisPool.returnBrokenResource(jedis);
        }
    }
}