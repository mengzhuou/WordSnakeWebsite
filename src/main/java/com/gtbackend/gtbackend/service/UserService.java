package com.gtbackend.gtbackend.service;

import com.gtbackend.gtbackend.dao.UserRepository;
import com.gtbackend.gtbackend.dao.WordAdditionRepository;
import com.gtbackend.gtbackend.model.User;
import com.gtbackend.gtbackend.model.WordAddition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService, UserLoader  {

    private UserRepository userRepository;
    private WordAdditionRepository wordAdditionRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            WordAdditionRepository wordAdditionRepository) {
        this.userRepository = userRepository;
        this.wordAdditionRepository = wordAdditionRepository;
    }

    @Override
    public Optional<User> loadUserByEmail(String email) {
        return userRepository.findById(email);
    }

    public Optional<User> getUser(String email){
        return userRepository.findById(email);
    }
    @Autowired
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public void addUser(User user) {
        if(userRepository.findById(user.getUsername()).isEmpty()){
            userRepository.save(user);
            userRepository.updateSignupRank(user.getEmail());
        }else{
            throw new DuplicateKeyException("User account (email) already exist");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> usr = userRepository.findById(username);
        if(usr.isEmpty()){
            throw new UsernameNotFoundException("User not present");
        }
        return usr.get();
    }

    @Transactional
    public void requestForWordAddition(String email, String word, String definition) {
        User user = userRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        WordAddition wordAddition = new WordAddition(user, word, definition);
        wordAdditionRepository.save(wordAddition);
    }
}
