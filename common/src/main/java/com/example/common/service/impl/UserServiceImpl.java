package com.example.common.service.impl;

import com.example.common.entity.Role;
import com.example.common.entity.User;
import com.example.common.repository.ListingRepository;
import com.example.common.repository.UserRepository;
import com.example.common.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean saveUser(User user) {
        if (!userRepository.findByEmail(user.getEmail()).isPresent()) {
            user.setRole(Role.USER);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public User findUserById(int id) {
        Optional<User> byId = userRepository.findById(id);
        return byId.orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    @Override
    public boolean deleteUserById(int id) {
        if (userRepository.findById(id).isPresent()) {
            listingRepository.changeListingUserNullWhenUserDeleted(id, null);
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        if (user != null) {
            User fromDB = findUserById(user.getId());
            if (fromDB != null && fromDB.getRole() == user.getRole()) {
                if (user.getName() == null) {
                    user.setName(fromDB.getName());
                }
                if (user.getSurname() == null) {
                    user.setSurname(fromDB.getSurname());
                }
                if (user.getEmail() == null) {
                    user.setEmail(fromDB.getEmail());
                }
                if (user.getPassword() == null) {
                    user.setPassword(fromDB.getPassword());
                }
                userRepository.save(user);
                return true;
            }
            return false;
        }
        return false;
    }

}
