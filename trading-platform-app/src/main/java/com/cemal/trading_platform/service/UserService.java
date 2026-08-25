package com.cemal.trading_platform.service;


import com.cemal.trading_platform.model.User;
import com.cemal.trading_platform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User addUser(User user) {
        String password=passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        return userRepository.save(user);
    }
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    public User updateUser(long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow();

        existingUser.setUsername(user.getUsername());
        existingUser.setBalance(user.getBalance());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

}
