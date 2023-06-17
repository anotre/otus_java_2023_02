package ru.otus;

import org.slf4j.Logger;

import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.Map.Entry;

public class MyCache<K, V> implements HwCache<K, V> {
    private final WeakHashMap<K, V> cache;
    private final WeakHashMap<K, Long> cacheOrder;
    private Long currentMaxIndex = 0L; // как обнулять индекс, кэш может жить долго
    private final WeakHashMap<HwListener<K, V>, HwListener<K, V>> listenersWeakRefs = new WeakHashMap<>(); // слушатели не удаляются при удалении постоянной ссылки на него
    private final int MAX_SIZE;
    private final int MAX_SIZE_DEFAULT = 1000;
    private final String EMPTY_CACHE = "Cache is empty";
    private final String NO_ELEMENT_CACHE_TEMPLATE = "Cache has no a value by key: %s";
    private final String NO_SUCH_LISTENER = "No such listener";
    private final Logger logger;

    public MyCache(int maxSize, Logger logger) {
        var defaultLoadFactor = 0.75f;
        this.MAX_SIZE = (maxSize == 0) ? MAX_SIZE_DEFAULT : maxSize;
        this.cache = new WeakHashMap<>(this.MAX_SIZE, defaultLoadFactor);
        this.cacheOrder = new WeakHashMap<>(this.MAX_SIZE, defaultLoadFactor);
        this.logger = logger;
    }

    @Override
    public void put(K key, V value) {
        if (this.cacheOrder.size() == 0) {
            this.resetCurrentIndex();
        }

        if (this.cache.size() >= MAX_SIZE) {
            this.removeFirstInCacheQueue();
        }

        this.cache.put(key, value);
        this.cacheOrder.put(key, ++this.currentMaxIndex);

        this.notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        if (this.cache.containsKey(key)) {
            notifyListeners(key, this.get(key), "remove");
            this.cache.remove(key);
            this.cacheOrder.remove(key);
        } else {
            String exceptionMessage = String.format(NO_ELEMENT_CACHE_TEMPLATE, key);
            logger.debug(exceptionMessage);
        }
    }

    @Override
    public V get(K key) {
        if (!this.cache.containsKey(key)) {
            var exceptionMessage = String.format(NO_ELEMENT_CACHE_TEMPLATE, key);
            logger.debug(exceptionMessage);
        }

        return this.cache.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        this.listenersWeakRefs.putIfAbsent(listener, listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        if (!this.listenersWeakRefs.containsKey(listener)) {
            logger.debug(NO_SUCH_LISTENER);
            throw new NoSuchElementException(NO_SUCH_LISTENER);
        }

        this.listenersWeakRefs.remove(listener);
    }

    private void notifyListeners(K key, V value, String action) {
        this.listenersWeakRefs.values().forEach(listener -> {
            try {
                listener.notify(key, value, action);
            } catch (Exception e) {
                this.logger.error(e.getMessage());
            }
        });
    }

    private void removeFirstInCacheQueue() {
        long minIndex = Long.MAX_VALUE;
        long currentIndex;
        K minKey = null;

        for (Entry<K, Long> entry : this.cacheOrder.entrySet()) {
            currentIndex = entry.getValue();
            if (currentIndex < minIndex) {
                minKey = entry.getKey();
                minIndex = currentIndex;
            }
        }

        if (minKey != null) {
            this.remove(minKey);
        } else {
            logger.info(EMPTY_CACHE);
        }
    }

    private void resetCurrentIndex() {
        this.currentMaxIndex = 0L;
    }
}
