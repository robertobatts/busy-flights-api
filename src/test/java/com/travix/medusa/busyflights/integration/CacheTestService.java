package com.travix.medusa.busyflights.integration;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class CacheTestService {

    @CacheEvict(value = "flights", allEntries = true)
    public void evictAll() {
    }
}
