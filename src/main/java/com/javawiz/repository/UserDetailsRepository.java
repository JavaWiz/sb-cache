package com.javawiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.javawiz.entity.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long>{
}
