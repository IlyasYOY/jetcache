package com.alicp.jetcache.hazelcast;

import com.alicp.jetcache.external.ExternalCacheConfig;
import com.hazelcast.map.IMap;

public class HazelcastCacheConfig <K, V> extends ExternalCacheConfig<K, V> {
    private IMap<K, V> map;

    public IMap<K, V> getMap() {
        return map;
    }

    public void setMap(IMap<K, V> map) {
        this.map = map;
    }
}
