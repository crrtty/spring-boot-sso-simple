package com.ly;

import com.ly.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author robben
 * @date 2023/12/19
 */
@Import(RedisConfig.class)
@DataRedisTest
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisTest {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Test
    public void testSet(){
        redisTemplate.opsForValue().set("sso:test:set1", "testValue1");
        redisTemplate.opsForSet().add("sso:test:set2", "asdf");
        redisTemplate.opsForHash().put("sso:hash1", "name1", "lms1");
        redisTemplate.opsForHash().put("sso:hash1", "name2", "lms2");
        redisTemplate.opsForHash().put("sso:hash1", "name3", "lms3");
        assertThat(redisTemplate.opsForValue().get("sso:test:set")).isEqualTo(null);
        assertThat((String)redisTemplate.opsForHash().get("sso:hash1", "name1")).isEqualTo("lms1");
    }
}
