package com.javawiz.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.javawiz.entity.UserDetails;
import com.javawiz.model.User;
import com.javawiz.repository.UserDetailsRepository;

@Service
@CacheConfig(cacheNames={"users"})
public class UserService {
	
	@Autowired
    private UserDetailsRepository repository;

    private List<User> users = new ArrayList<>();

    @Autowired
    UserService() {}

    @PostConstruct
    private void fillUsers() {
       users.add(User.builder().username("user_1").age(20).build());
       users.add(User.builder().username("user_2").age(76).build());
       users.add(User.builder().username("user_3").age(54).build());
       users.add(User.builder().username("user_4").age(30).build());
    }

    @Cacheable
    public List<User> findAll() {
        simulateSlowService();
        return this.users;
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Cacheable
    public List<UserDetails> getUsersDetails() {
    	return repository.findAll();
    }
}