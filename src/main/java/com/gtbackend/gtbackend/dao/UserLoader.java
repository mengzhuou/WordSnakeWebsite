package com.gtbackend.gtbackend.dao;

import com.gtbackend.gtbackend.model.User;

import java.util.Optional;

public interface UserLoader {
    Optional<User> loadUserByEmail(String email);
}
