package com.abin.lee.lock.redis.service.model;

import java.io.Serializable;

/**
 * Created by abin
 * Be Created in 2016/5/18.
 */
public class BeansTo implements Serializable {
    private String id;
    private String name;
    private Integer age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
