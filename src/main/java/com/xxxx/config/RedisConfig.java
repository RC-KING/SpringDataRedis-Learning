package com.xxxx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.repository.NoRepositoryBean;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //为string类型key设置序列器,Redis的Key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //为string类型value设置序列器,Redis的Value
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //======================//
        //为hash类型key设置序列器,Hash的Key
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //为hash类型value设置序列器,Hash的Value
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration(){
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration
                .master("mymaster")
                .sentinel("192.168.243.128",26379)
                .sentinel("192.168.243.128",26380)
                .sentinel("192.168.243.128",26381)
                .setPassword("jwj422123");

        return redisSentinelConfiguration;
    }
}
