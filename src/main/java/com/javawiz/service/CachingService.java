package com.javawiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableScheduling
public class CachingService {

	@Autowired
	private CacheManager cacheManager;

	public void evictAllCaches() {
		cacheManager.getCacheNames().stream().forEach(cacheName -> {
			log.debug("Clearing cache with name : {}", cacheName);
			cacheManager.getCache(cacheName).clear();
		});
	}
	
	@Scheduled(cron = "0 0/1 * * * ?")// execute after every 1 minute
    public void clearCacheSchedule(){
		cacheManager.getCacheNames().stream().forEach(cacheName -> {
			log.debug("Clearing cache with name [{}] as per scheduled.", cacheName);
			cacheManager.getCache(cacheName).clear();
		});
    }
}
