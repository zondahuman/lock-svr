package com.abin.lee.lock.redis.service.util;

import com.abin.lee.lock.redis.service.model.BeansTo;

import java.io.*;

/**
 * 序列化工具
 * 
 * @author
 */
public class SerializeUtil {

    /**
     * 序列化
     * 
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
        } finally {
            if (null != oos) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }
            if (null != baos) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
        } finally {
            if (null != ois) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }
            if (null != bais) {
                try {
                    bais.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


    public static void main(String[] args) {
        String str = "tobytes";
        BeansTo beansTo = new BeansTo();
        beansTo.setAge(5);
        beansTo.setId("aaaaaaaaaaaaaaaaaaa");
        beansTo.setName("johnWhite");
        byte[] input = SerializeUtil.serialize(beansTo);
        System.out.println("input=" + input);
        BeansTo result =(BeansTo) SerializeUtil.unserialize(input);
        System.out.println("result=" +result);
    }



}
