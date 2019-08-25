package com.javawiz.conroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javawiz.entity.UserDetails;
import com.javawiz.model.User;
import com.javawiz.service.UserService;

@RestController
@RequestMapping(value = "/api")
public class UserController {
	
    private final UserService userService;
    
    @Autowired
    UserController (UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users/all")
    public List<User> getAllUsers() {
        return userService.findAll();
    }
    
    @GetMapping(value = "/userDetails/all")
    public List<UserDetails> getUserDetails() {
    	return userService.getUsersDetails();
    }
}