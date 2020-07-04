package com.luxf.thread.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 小66
 * @date 2020-07-02 21:07
 **/
public class ConcurrentHashMapDemo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> cacheMap = new ConcurrentHashMap<>(20);
        cacheMap.put("CORE_LIKE", 2);
        cacheMap.put("CORE_EQ", 1);
        /**
         * ConcurrentHashMap的部分特有方法的具体使用、 参数：parallelismThreshold --> 并行阈值、(最大并行数量)
         */
        // 如果key不存在,则push、 如果key存在,则合并value
        Integer mergeValue = cacheMap.merge("CORE_EQ", 5, (integer, integer2) -> integer + integer2);
        //查询第一个匹配到的Key, 要返回什么值,根据具体Function的实现
        String keys = cacheMap.searchKeys(20, key -> key.startsWith("CORE") ? key : null);
        Integer values = cacheMap.searchValues(20, integer -> integer > 1 ? integer : null);
        String reduceKeys = cacheMap.reduceKeys(30, (s, s2) -> s + "," + s2);
        cacheMap.forEach(20, (key, value) -> System.out.println("key = " + key + " ：" + value));
        cacheMap.forEachKey(10, key -> System.out.println("key = " + key));
        cacheMap.forEachValue(12, integer -> System.out.println("value = " + integer));
        System.out.println("keys = " + keys);
        System.out.println("values = " + values);
        System.out.println("reduceKeys = " + reduceKeys);
        System.out.println("cacheMap.size() = " + cacheMap.size());
        System.out.println("mergeValue = " + mergeValue);
    }
}
