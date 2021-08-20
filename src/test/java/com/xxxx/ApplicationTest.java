package com.xxxx;

import com.xxxx.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class ApplicationTest {
    /**
     * 测试<String,String>
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void StringRedisTemplateTest() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set("ddd", "jxx");
        String name = (String) valueOperations.get("ddd");
        System.out.println(name);
    }

    /**
     * 测试自定义的 RedisTemplate (配置文件)
     * <String, Object>
     */
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void MyRedisTemplateTest() {
        User user = new User();
        user.setUsername("jdd");
        user.setAge("18");
        user.setSex("男");
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("user", user);
        User usUser = (User) valueOperations.get("user");
        System.out.println(usUser);
    }


    /**
     * 1.操作String
     */
    @Test
    public void testString() {
        // 先获取操作对象 ValueOperations 是操作 Sting类型的
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 添加一条数据
        valueOperations.set("username", "zhangsan");
        valueOperations.set("age", "18");
        // redis中以层级关系、目录形式存储数据
        valueOperations.set("user:01", "lisi");
        valueOperations.set("user:02", "wangwu");
        // 通过Map添加多条数据
        Map<String, String> userMap = new HashMap<>();
        userMap.put("address", "bj");
        userMap.put("sex", "1");
        // 通过Map同时添加多条数据
        valueOperations.multiSet(userMap);
        // 获取一条数据
        Object username = valueOperations.get("username");
        System.out.println(username);


        // 获取多条数据
        List<String> keys = new ArrayList<>();
        keys.add("username");
        keys.add("age");
        keys.add("address");
        keys.add("sex");
        List<Object> resultList = valueOperations.multiGet(keys);
        resultList.forEach(System.out::println);
        // 删除
        redisTemplate.delete("username");
    }


    /**
     * 2.操作Hash
     */
    @Test
    public void testHash() {
        HashOperations<String, String, String> hashOperations =
                redisTemplate.opsForHash();
        /*
         * 添加一条数据
         *     参数一：redis的key
         *     参数二：hash的key
         *     参数三：hash的value
         */
        hashOperations.put("userInfo", "name", "lisi");
        hashOperations.put("userInfo", "age", "18");
        // 添加多条数据
        Map<String, String> map = new HashMap<String, String>();
        map.put("age", "20");
        map.put("sex", "1");
        hashOperations.putAll("userInfo", map);

        // 获取一条数据
        String name = hashOperations.get("userInfo", "name");
        System.out.println(name);

        // 获取多条数据
        List<String> keys = new ArrayList<>();
        keys.add("age");
        keys.add("sex");
        List<String> resultList = hashOperations.multiGet("userInfo", keys);
        resultList.forEach(System.out::println);

        // 获取Hash类型所有的数据
        Map<String, String> userMap = hashOperations.entries("userInfo");
        userMap.forEach((k,v)-> System.out.println(k+ "--" +v));

        // 删除 用于删除hash类型数据
        hashOperations.delete("userInfo", "name");
    }

    // 3.操作list
    @Test
    public void testList() {
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        // 左添加(上)
        listOperations.leftPush("students", "Wang Wu");
        listOperations.leftPush("students", "Li Si");
        // 左添加(上) 把value值放到key对应列表中pivot值的左面，如果pivot值存在的话
        listOperations.leftPush("students", "Wang Wu", "Li Si");
        // 右添加(下)
        listOperations.rightPush("students", "Zhao Liu");

        // 获取 start起始下标 end结束下标 包含关系
        List<Object> students = listOperations.range("students", 0, 2);
        students.forEach(System.out::println);
        // 根据下标获取
        Object stu = listOperations.index("students", 1);
        System.out.println(stu);
        // 获取总条数
        Long total = listOperations.size("students");
        System.out.println("总条数：" + total);
        // 删除单条 删除列表中存储的列表中几个出现的Li Si。
        listOperations.remove("students", 1, "Li Si");
        // 删除多条
        redisTemplate.delete("students");
    }

    // 4.操作set-无序
    @Test
    public void testSet() {
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        // 添加数据
        String[] letters = new String[]{"aaa", "bbb", "ccc", "ddd", "eee"};
        //setOperations.add("letters", "aaa", "bbb", "ccc", "ddd", "eee");
        setOperations.add("letters", letters);
        // 获取数据
        Set<Object> let = setOperations.members("letters");
        let.forEach(System.out::println);
        // 删除
        setOperations.remove("letters", "aaa", "bbb");
    }

    // 5.操作sorted set-有序
    @Test
    public void testSortedSet() {
        ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
        // 添加一条数据
        zSetOperations.add("score", "xnz",1D);
        zSetOperations.add("score", "jxx",2D);
        zSetOperations.add("score", "jdd",3D);

        // 一次性添加多条数据
        ZSetOperations.TypedTuple<Object> t1 =
                new DefaultTypedTuple<Object>("zhangsan", 7D);
        ZSetOperations.TypedTuple<Object> t2 =
                new DefaultTypedTuple<Object>("lisi", 3D);
        ZSetOperations.TypedTuple<Object> t3 =
                new DefaultTypedTuple<Object>("wangwu", 5D);
        ZSetOperations.TypedTuple<Object> t4 =
                new DefaultTypedTuple<Object>("zhaoliu", 6D);
        ZSetOperations.TypedTuple<Object> t5 =
                new DefaultTypedTuple<Object>("tianqi", 2D);

        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
        tuples.add(t1);
        tuples.add(t2);
        tuples.add(t3);
        tuples.add(t4);
        tuples.add(t5);
        // 通过tuples一次性添加数据
        zSetOperations.add("score", tuples);

        // 获取数据
        Set<Object> scores = zSetOperations.range("score", 0, 4);
        scores.forEach(System.out::println);
        // 获取总条数
        Long total = zSetOperations.size("score");
        System.out.println("总条数：" + total);
        // 删除
        zSetOperations.remove("score", "zhangsan", "lisi");
    }

    // 获取所有key
    @Test
    public void testAllKeys() {
        // 当前库key的名称(直接使用redisTemplate进行操作)
        Set<String> keys = redisTemplate.keys("*");
        keys.forEach(System.out::println);
    }

    // 通用删除
    @Test
    public void testDelete() {
        // 删除 通用 适用于所有数据类型
        redisTemplate.delete("score");
    }

    /**
     * 其他操作
     */
    @Test
    public void testEx() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 方法一：新建key,设置定时
        valueOperations.set("code", "abcd", 180, TimeUnit.SECONDS);
        // 方法二：给已存在的key设置失效时间
        boolean flag = redisTemplate.expire("code", 180, TimeUnit.SECONDS);
        // 获取指定key的失效时间
        Long l = redisTemplate.getExpire("code");
        System.out.println(flag+"==="+l);
        // 如果不存在则设置(NX)
        valueOperations.setIfAbsent("key", "ssss",180, TimeUnit.SECONDS);
        // 如果存在则设置(XX)
        valueOperations.setIfPresent("key", "xxx");
    }
}
