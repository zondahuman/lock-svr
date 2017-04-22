package com.abin.lee.lock.redis.service.impl;

import com.abin.lee.lock.redis.service.RedisCallback;
import com.abin.lee.lock.redis.service.RedisService;
import com.abin.lee.lock.redis.service.util.SerializeUtil;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by abin
 * Be Created in 2016/5/18.
 */
@Service
public class RedisServiceImpl implements RedisService {

    private static final String host = "172.16.2.145";
    private static final int port = 6379;
    private static final int timeout = 5000;

    private JedisPool rJedisPool;

    private JedisPool wJedisPool;

    private RedisManager rRedisManager;

    private RedisManager wRedisManager;
    JedisPoolConfig jedisPoolConfig = null;

    {
        jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(100);
        jedisPoolConfig.setMaxWaitMillis(5000);

        rJedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
        wJedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);

        this.wRedisManager = new RedisManager(wJedisPool);
        this.rRedisManager = new RedisManager(rJedisPool);
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public String set(final String key, final String value) {
        return wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * 设置值，有超时时间
     *
     * @param key
     * @param seconds
     * @param value
     */
    public String setex(final String key, final int seconds, final String value) {
        return wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    /**
     * 当且仅当 key 不存在时设置值
     *
     * @param key
     * @param value
     */
    public Long setnx(final String key, final String value) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        return rRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * 设置对象，有超时时间
     * @param key
     * @param seconds
     * @param object
     */
    public void setObject(final String key, final int seconds, final Object object) {
        wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.setex(key.getBytes(), seconds, SerializeUtil.serialize(object));
            }
        });
    }

    /**
     * 设置对象，无超时时间 慎用
     * @param key
     * @param object
     */
    public void setObject(final String key, final Object object) {
        wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.set(key.getBytes(), SerializeUtil.serialize(object));
            }
        });
    }

    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    public Object getObject(final String key) {

        return rRedisManager.request(new RedisCallback<Object>() {
            @Override
            public Object doInRequest(Jedis jedis) {

                return SerializeUtil.unserialize(jedis.get(key.getBytes()));
            }
        });
    }

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(final String key) {
        return rRedisManager.request(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    /**
     * 为key设置过期时间
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(final String key, final int seconds) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    public Boolean exists(final byte[] key) {
        return rRedisManager.request(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    /**
     * 设置哈希值
     *
     * @param key
     * @param field
     * @param value
     */
    public Long hset(final String key, final String field, final String value) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    /**
     * 获取哈希值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(final String key, final String field) {
        return rRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    /**
     * 删除哈希值
     *
     * @param key
     * @param field
     */
    public void hdel(final String key, final String field) {
        wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    /**
     * 加一的原子操作
     *
     * @param key
     */
    public Long incr(final String key) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public String set(final byte[] key, final byte[] value) {
        return wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     */
    public String setex(final byte[] key, final int seconds, final byte[] value) {
        return wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public byte[] get(final byte[] key) {
        return rRedisManager.request(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRequest(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * 删除制定key 值
     *
     * @param key
     */
    public Long del(final String key) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.del(key);
            };
        });
    }

    /**
     * set add
     *
     * @param key
     */
    public Long sadd(final String key, final String... member) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.sadd(key, member);
            };
        });
    }

    /**
     * set member key
     *
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(final String key, final String member) {
        return rRedisManager.request(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {
                return jedis.sismember(key, member);
            }
        });
    }

    /**
     * list lpush
     *
     * @param key
     */
    public Long lpush(final String key, final String... member) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.lpush(key, member);
            };
        });
    }

    /**
     * list lpop
     *
     * @param key
     */
    public String lpop(final String key) {
        return wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.lpop(key);
            };
        });
    }

    /**
     * list llen
     *
     * @param key
     */
    public Long llen(final String key) {
        return rRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.llen(key);
            };
        });
    }

    /**
     * list rpush
     *
     * @param key
     */
    public Long rpush(final String key, final String... member) {
        return wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.rpush(key, member);
            };
        });
    }

    /**
     * 原子性的设置该Key为指定的Value，同时返回该Key的原有值。
     *
     * @param key
     * @param value
     */
    public String getSet(final String key, final String value) {
        return wRedisManager.request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.getSet(key, value);
            };
        });
    }

    /**
     * 设置哈希值
     *
     * @param key
     * @param field
     * @param value
     */
    public void hsetObject(final String key, final String field, final Object value) {
        wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hset(key.getBytes(), field.getBytes(), SerializeUtil.serialize(value));
            }
        });
    }

    /**
     * 获取哈希值
     *
     * @param key
     * @param field
     * @param <T>
     * @return
     */
    public <T> T hgetObject(final String key, final String field) {
        return rRedisManager.request(new RedisCallback<T>() {
            @Override
            public T doInRequest(Jedis jedis) {
                return (T) SerializeUtil.unserialize(jedis.hget(key.getBytes(), field.getBytes()));
            }
        });
    }

    /**
     * 获得符合给定模式 pattern 的 key
     *
     * @param keyPattern
     * @return
     */
    public Set<String> keys(final String keyPattern) {
        return rRedisManager.request(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRequest(Jedis jedis) {
                return jedis.keys(keyPattern);
            }
        });
    }

    /**
     * 返回集合 key 中的所有成员
     *
     * @param key
     * @return
     */
    public Set<String> smembers(final String key) {
        return rRedisManager.request(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRequest(Jedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    /**
     * 删除哈希值
     *
     * @param key
     * @param field
     */
    public void hdelObject(final String key, final String field) {
        wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hdel(key.getBytes(), field.getBytes());
            }
        });
    }

    /**
     * 获取哈希所有key值集合
     *
     * @param key
     * @return
     */
    public Set<String> hkeys(final String key) {
        return rRedisManager.request(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRequest(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    /**
     * 运行lua脚本
     *
     * @param script
     * @param keys
     * @param args
     * @return
     */
    public Object runScript(String script, List<String> keys, List<String> args) {
//        return wRedisManager.runScript(script, keys, args);
        return null;
    }

    /**
     * 加锁
     *
     * @param lock
     * @param expired
     * @return
     */
    public Boolean acquireLock(final String lock, final long expired) {
        return wRedisManager.request(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRequest(Jedis jedis) {

                boolean success = false;

                // expired 单位为秒
                long value = System.currentTimeMillis() + expired * 1000 + 1;

                long acquired = jedis.setnx(lock, String.valueOf(value));
                if (acquired == 1) {
                    success = true;
                } else {
                    long oldValue = Long.valueOf(jedis.get(lock));

                    if (oldValue < System.currentTimeMillis()) {
                        String getValue = jedis.getSet(lock, String.valueOf(value));
                        if (Long.valueOf(getValue) == oldValue) {
                            success = true;
                        } else {
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                }
                return success;
            };
        });
    }

    /**
     * 释放锁
     *
     * @param lock
     */
    public void releaseLock(final String lock) {
        wRedisManager.request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                long current = System.currentTimeMillis();
                if (current < Long.valueOf(jedis.get(lock))) {
                    return jedis.del(lock);
                }
                return 0L;
            };
        });
    }

    public long rpushx(final String key, final String value, final int seconds) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                long length = jedis.rpushx(key, value);
                jedis.expire(key, seconds);
                return length;
            }
        });
    }

    public long rpush(final String key, final int seconds, final String... value) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                long length = jedis.rpush(key, value);
                jedis.expire(key, seconds);
                return length;
            }
        });
    }

    public void ltrim(final String key, final int start, final int end) {
        this.getwRedisManager().request(new RedisCallback<Object>() {
            @Override
            public Object doInRequest(Jedis jedis) {
                jedis.ltrim(key, start, end);
                return null;
            }
        });
    }

    public List<String> lrange(final String key, final int start, final int end) {
        return this.getwRedisManager().request(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRequest(Jedis jedis) {
                return jedis.lrange(key, start, end);
            }
        });
    }

    public Long decr(final String key) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    public Map<String, String> hgetAll(final String key) {
        return this.getwRedisManager().request(new RedisCallback<Map<String, String>>() {
            @Override
            public Map<String, String> doInRequest(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    public Long hincrBy(final String key, final String field, final long value) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.hincrBy(key, field, value);
            }
        });
    }


    public Long decrBy(final String key, final long value) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.decrBy(key, value);
            }
        });
    }

    public String set(final String key, final String value, final String nxxx, final String expx, final int time) {
        return this.getwRedisManager().request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.set(key, value, nxxx, expx, time);
            }
        });
    }

    public String hmset(final String key, final Map<String, String> hash) {
        return this.getwRedisManager().request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.hmset(key, hash);
            }
        });
    }

    public String rpop(final String key) {
        return this.getwRedisManager().request(new RedisCallback<String>() {
            @Override
            public String doInRequest(Jedis jedis) {
                return jedis.rpop(key);
            }
        });
    }

    public Long ttl(final String key) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }


    public Long ttl(final byte[] key) {
        return this.getwRedisManager().request(new RedisCallback<Long>() {
            @Override
            public Long doInRequest(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }


    public JedisPool getrJedisPool() {
        return rJedisPool;
    }

    public void setrJedisPool(JedisPool rJedisPool) {
        this.rJedisPool = rJedisPool;
    }

    public JedisPool getwJedisPool() {
        return wJedisPool;
    }

    public void setwJedisPool(JedisPool wJedisPool) {
        this.wJedisPool = wJedisPool;
    }

    public RedisManager getrRedisManager() {
        return rRedisManager;
    }

    public void setrRedisManager(RedisManager rRedisManager) {
        this.rRedisManager = rRedisManager;
    }

    public RedisManager getwRedisManager() {
        return wRedisManager;
    }

    public void setwRedisManager(RedisManager wRedisManager) {
        this.wRedisManager = wRedisManager;
    }

}