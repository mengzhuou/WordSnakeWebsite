package com.gtbackend.gtbackend.service;


import com.gtbackend.gtbackend.dao.UserLoader;
import com.gtbackend.gtbackend.dao.UserRepository;
import com.gtbackend.gtbackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoaderImpl implements UserLoader {

    private UserRepository userRepository;

    @Autowired
    public UserLoaderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> loadUserByEmail(String email) {
        return userRepository.findById(email);
    }
}

