package com.alicp.jetcache.hazelcast;

import com.alicp.jetcache.external.ExternalCacheBuilder;

public class HazelcastCacheBuilder<T extends ExternalCacheBuilder<T>> extends ExternalCacheBuilder<T> {
    protected HazelcastCacheBuilder() {
        buildFunc(config -> new HazelcastCache((HazelcastCacheConfig) config));
    }

    @Override
    public HazelcastCacheConfig getConfig() {
        if (config == null) {
            config = new HazelcastCacheConfig();
        }
        return (HazelcastCacheConfig) config;
    }

}
