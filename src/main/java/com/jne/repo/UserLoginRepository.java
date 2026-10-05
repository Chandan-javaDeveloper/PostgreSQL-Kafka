package com.jne.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jne.model.UserLogin;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, String>{

}
