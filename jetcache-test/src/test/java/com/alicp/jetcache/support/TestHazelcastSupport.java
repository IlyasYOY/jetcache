package com.alicp.jetcache.support;

import com.alicp.jetcache.CacheResult;
import com.alicp.jetcache.hazelcast.HazelcastCache;
import com.alicp.jetcache.hazelcast.HazelcastCacheConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestHazelcastSupport {
    HazelcastInstance hazelcast = Hazelcast.getOrCreateHazelcastInstance(Config.load()
            .setInstanceName("instance")
            .addMapConfig(new MapConfig("test")));
    IMap<Object, Object> map;

    @BeforeEach
    void setUp() {
        IMap<Object, Object> test = hazelcast.getMap("test");
        test.clear();
        map = test;
    }

    @Test
    void testPut() {
        HazelcastCache<Object, Object> objectObjectHazelcastCache = getCache();

        CacheResult put = objectObjectHazelcastCache.PUT("a", "a");
        Object a = map.get("a");
        assertNotNull(a);
    }

    private HazelcastCache<Object, Object> getCache() {
        HazelcastCacheConfig<Object, Object> objectObjectHazelcastCacheConfig = new HazelcastCacheConfig<>();
        objectObjectHazelcastCacheConfig.setMap(map);
        objectObjectHazelcastCacheConfig.setKeyPrefix("");
        return new HazelcastCache<>(objectObjectHazelcastCacheConfig);
    }
}
