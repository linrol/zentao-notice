/**
 * 
 */
package com.alinkeji.zentaonotice.init;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author linrol
 * @since JDK 1.8
 * @version V1.0 Date:2020年10月19日 上午12:22:09 Copyright (c) 2020, linrol@77hub.com All Rights
 *          Reserved.
 */

@Configuration
public class RedisConfig {

  @Bean
  @SuppressWarnings("all")
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

    template.setConnectionFactory(factory);

    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer(Object.class);

    ObjectMapper om = new ObjectMapper();

    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

    // om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

    jackson2JsonRedisSerializer.setObjectMapper(om);

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    // key采用String的序列化方式

    template.setKeySerializer(stringRedisSerializer);

    // hash的key也采用String的序列化方式

    template.setHashKeySerializer(stringRedisSerializer);

    // value序列化方式采用jackson

    template.setValueSerializer(jackson2JsonRedisSerializer);

    // hash的value序列化方式采用jackson
    template.setHashValueSerializer(jackson2JsonRedisSerializer);

    template.afterPropertiesSet();

    return template;

  }

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(10);
    taskScheduler.initialize();
    return taskScheduler;
  }

}

