# Caching Data with Spring boot

## Overview

Spring framework provides cache abstraction api for different cache providers. The usage of the API is very simple, yet very powerful. We will see the annotation based Java configuration on caching. Note that we can achieve similar functionality through XML configuration as well.

## Getting Started

Use the spring-boot-starter-cache starter package to easily add the caching dependencies:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### @EnableCaching

It enables Spring’s annotation-driven cache management capability. In spring boot project, we need to add it to the boot application class annotated with @SpringBootApplication. Spring provides one concurrent hashmap as default cache, but we can override CacheManager to register external cache providers as well easily.

### @Cacheable

It is used on the method level to let spring know that the response of the method are cacheable. Spring manages the request/response of this method to the cache specified in annotation attribute. For example, @Cacheable ("cache-name1", "cache-name2").

@Cacheable annotation has more options. Like we can specify the key of the cache from the request of the method. If nothing specified, spring uses all the class fields and use those as cache key (mostly HashCode) to maintain caching but we can override this behavior by providing key information.

```
Cacheable(value="books", key="#isbn")
public Book findStoryBook(ISBN isbn, boolean checkWarehouse, boolean includeUsed)
 
@Cacheable(value="books", key="#isbn.rawNumber")
public Book findStoryBook (ISBN isbn, boolean checkWarehouse, boolean includeUsed)
 
@Cacheable(value="books", key="T(classType).hash(#isbn)")
public Book findStoryBook (ISBN isbn, boolean checkWarehouse, boolean includeUsed)
```

We can also use conditional caching as well. For example,

```
@Cacheable(value="book", condition="#name.length < 50")
public Book findStoryBook (String name)
```

### @CachePut
Sometimes we need to manipulate the cacheing manually to put (update) cache before method call. This will allow us to update the cache and will also allow the method to be executed. The method will always be executed and its result placed into the cache (according to the @CachePut options).

It supports the same options as @Cacheable and should be used for cache population rather then method flow optimization.

```
Note that using @CachePut and @Cacheable annotations on the same method is generally discouraged because they have different behaviors. While the latter causes the method execution to be skipped by using the cache, the former forces the execution in order to execute a cache update.
This leads to unexpected behavior and with the exception of specific corner-cases (such as annotations having conditions that exclude them from each other), such declarations should be avoided.
```

### @CacheEvict
It is used when we need to evict (remove) the cache previously loaded of master data. When CacheEvict annotated methods will be executed, it will clear the cache.

We can specify key here to remove cache, if we need to remove all the entries of the cache then we need to use allEntries=true. This option comes in handy when an entire cache region needs to be cleared out – rather then evicting each entry (which would take a long time since it is inefficient), all the entries are removed in one operation.

### @Caching
This annotation is required when we need both CachePut and CacheEvict at the same time.

```
@Caching(evict = { 
  @CacheEvict("addresses"), 
  @CacheEvict(value="directory", key="#customer.name") })
public String getAddress(Customer customer) {...}
```

We can group multiple caching annotations with @Caching, and use it to implement your own customized caching logic.

### @CacheConfig
With the @CacheConfig annotation, you can streamline some of the cache configuration into a single place – at the class level – so that you don’t have to declare things multiple times:

```
@CacheConfig(cacheNames={"addresses"})
public class CustomerDataService {
 
    @Cacheable
    public String getAddress(Customer customer) {...}
```

### Register a cache engine with spring boot

Spring boot provides integration with following cache providers. Spring boot does the auto configuration with default options if those are present in class path and we have enabled cache by @EnableCaching in the spring boot application.

JCache (JSR-107) (EhCache 3, Hazelcast, Infinispan, and others)

1. EhCache 2.x
2. Hazelcast
3. Infinispan
4. Couchbase
5. Redis
6. Caffeine
7. Simple cache

We can override specific cache behaviors in Spring boot by overriding the cache provider specific settings.

## Ways to clear all cache

### Using CacheManager

Next, let’s have a look at how we can evict the cache using the CacheManager provided by the Spring Cache module. First, we have to auto-wire the implemented CacheManager bean.

And then we can clear the caches with it based on our needs:

```
@Autowired
CacheManager cacheManager;
 
public void evictSingleCacheValue(String cacheName, String cacheKey) {
    cacheManager.getCache(cacheName).evict(cacheKey);
}
 
public void evictAllCacheValues(String cacheName) {
    cacheManager.getCache(cacheName).clear();
}
```

As we can see in the code, the clear() method will clear all the cache entries and the evict() method will clear values based on a key.

### How to Evict All Caches?

Spring doesn’t provide an out of the box functionality to clear all the caches. But we can achieve this easily by using the getCacheNames() method of the cache manager.

### Eviction on Demand

Let’s now see how we can clear all the caches on demand. In order to create a trigger point, we have to expose an end point first:

```
@RestController
public class CachingController {
     
    @Autowired
    CachingService cachingService;
     
    @GetMapping("clearAllCaches")
    public void clearAllCaches() {
        cachingService.evictAllCaches();
    }
}
```

In the CachingService, we can then clear all the caches by iterating over the cache names obtained from the cache manager:

```
public void evictAllCaches() {
    cacheManager.getCacheNames().stream()
      .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
}
```

### Automatic Eviction
There are certain use cases where cache eviction should be performed automatically at certain intervals. In this case, we can make use of the Spring’s task scheduler:

```
@Scheduled(fixedRate = 6000)
public void evictAllcachesAtIntervals() {
    evictAllCaches();
}
```


