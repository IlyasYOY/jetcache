package com.alicp.jetcache.hazelcast;

import com.alicp.jetcache.CacheConfig;
import com.alicp.jetcache.CacheGetResult;
import com.alicp.jetcache.CacheResult;
import com.alicp.jetcache.CacheResultCode;
import com.alicp.jetcache.CacheValueHolder;
import com.alicp.jetcache.MultiGetResult;
import com.alicp.jetcache.external.AbstractExternalCache;
import com.hazelcast.core.EntryView;
import com.hazelcast.map.IMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HazelcastCache<K, V> extends AbstractExternalCache<K, V> {
    private final HazelcastCacheConfig<K, V> config;

    private final IMap<K, V> map;

    public HazelcastCache(HazelcastCacheConfig<K, V> config) {
        super(config);
        this.config = config;
        map = config.getMap();
    }


    @Override
    protected CacheGetResult<V> do_GET(K key) {
        EntryView<K, V> entryView = map.getEntryView(key);
        return entryViewToCacheGetResult(entryView);
    }

    @Override
    protected MultiGetResult<K, V> do_GET_ALL(Set<? extends K> keys) {
        return new MultiGetResult<>(CacheResultCode.SUCCESS, null, keys.stream()
                .map(map::getEntryView)
                .collect(Collectors.toMap(EntryView::getKey, this::entryViewToCacheGetResult)));
    }

    @Override
    protected CacheResult do_PUT(K key, V value, long expireAfterWrite, TimeUnit timeUnit) {
        map.put(key, value, expireAfterWrite, timeUnit);
        return CacheResult.SUCCESS_WITHOUT_MSG;
    }

    @Override
    protected CacheResult do_PUT_ALL(Map<? extends K, ? extends V> map, long expireAfterWrite, TimeUnit timeUnit) {
        map.forEach((k, v) -> this.map.put(k, v, expireAfterWrite, timeUnit));
        return CacheResult.SUCCESS_WITHOUT_MSG;
    }

    @Override
    protected CacheResult do_REMOVE(K key) {
        map.remove(key);
        return CacheResult.SUCCESS_WITHOUT_MSG;
    }

    @Override
    protected CacheResult do_REMOVE_ALL(Set<? extends K> keys) {
        keys.forEach(map::remove);
        return CacheResult.SUCCESS_WITHOUT_MSG;
    }

    @Override
    protected CacheResult do_PUT_IF_ABSENT(K key, V value, long expireAfterWrite, TimeUnit timeUnit) {
        map.putIfAbsent(key, value, expireAfterWrite, timeUnit);
        return CacheResult.SUCCESS_WITHOUT_MSG;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return (T) map;
    }

    @Override
    public CacheConfig<K, V> config() {
        return config;
    }

    private CacheGetResult entryViewToCacheGetResult(EntryView<K, V> entryView) {
        if (entryView == null) {
            return CacheGetResult.NOT_EXISTS_WITHOUT_MSG;
        }

        CacheValueHolder<V> valueHolder = new CacheValueHolder<>();
        valueHolder.setAccessTime(entryView.getLastAccessTime());
        valueHolder.setExpireTime(entryView.getExpirationTime());
        valueHolder.setValue(entryView.getValue());

        return new CacheGetResult<>(CacheResultCode.SUCCESS, null, valueHolder);
    }
}
