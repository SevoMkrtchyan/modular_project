package com.example.common.service;

import com.example.common.entity.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    boolean saveUser(User user);

    User findUserById(int id);

    User findUserByEmail(String email);

    boolean deleteUserById(int id);

    boolean updateUser(User user);


}
