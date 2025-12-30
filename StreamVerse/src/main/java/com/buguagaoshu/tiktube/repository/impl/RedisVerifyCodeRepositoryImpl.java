package com.buguagaoshu.tiktube.repository.impl;

import com.buguagaoshu.tiktube.repository.RedisRepository;
import com.buguagaoshu.tiktube.repository.VerifyCodeRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "tiktube", name = "open-redis", havingValue = "true")
public class RedisVerifyCodeRepositoryImpl implements VerifyCodeRepository {

    private static final String KEY_PREFIX = "verify:code:";

    private final RedisRepository redisRepository;

    public RedisVerifyCodeRepositoryImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    private static String buildKey(String key) {
        return KEY_PREFIX + key;
    }

    @Override
    public void save(String key, String code) {
        // 图形验证码：给一个较短 TTL，避免 Redis 无限制增长
        redisRepository.set(buildKey(key), code, 120);
    }

    @Override
    public void save(String key, String code, Long time) {
        // 兼容接口注释：time 单位为分钟
        long ttlSeconds = 0;
        if (time != null && time > 0) {
            ttlSeconds = time * 60;
        }
        if (ttlSeconds > 0) {
            redisRepository.set(buildKey(key), code, ttlSeconds);
        } else {
            redisRepository.set(buildKey(key), code);
        }
    }

    @Override
    public String find(String key) {
        Object val = redisRepository.get(buildKey(key));
        return val == null ? null : String.valueOf(val);
    }

    @Override
    public void remove(String key) {
        redisRepository.del(buildKey(key));
    }
}
