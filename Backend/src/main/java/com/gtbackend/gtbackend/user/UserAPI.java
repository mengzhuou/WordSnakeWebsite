package com.gtbackend.gtbackend.user;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping( path = "api/v1")
public class UserAPI {

    private final UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserAPI(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping("/getUserEmail")
    @ResponseBody
    public String getUserEmail(Principal principal) {
        if (principal != null){
            Optional<User> user = userService.getUser(principal.getName());
            if (user.isPresent()) {
                return user.get().getEmail();
            } else {
                throw new NoSuchElementException("User not found");
            }
        }
        return "User does not exist";
    }


    @PostMapping("/logout")
    public void logout(HttpServletRequest request) throws ServletException {
        request.logout();
    }

    @PostMapping("/login")
    public void login(@RequestBody Map<String, String> body) throws AuthenticationException, NoSuchElementException {
        String email = body.get("email");
        String password = body.get("password");
        Optional<User> tmp = userService.getUser(email);
        User usr = tmp.get();
        if (tmp.isEmpty()){
            throw new BadCredentialsException("Please enter your email or password!");
        }
        if(passwordEncoder.matches(password, usr.getPassword())){
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(usr.getEmail(),
                    usr.getPassword()));
        }else{
            throw new BadCredentialsException("Email or Password does not match our records!");
        }
    }

    @PostMapping("/register")
    public void addUser(@RequestBody Map<String, String> body) throws IllegalArgumentException, DateTimeParseException {
        User user = new User(body.get("email"), passwordEncoder.encode(body.get("password")),
                body.get("name"), LocalDate.parse(body.get("dob")));
        userService.addUser(user);
    }

    @PostMapping("/updateBestScore")
    public void updateBestScore(Principal principal, @RequestParam int score){
        if (principal != null){
            String email = principal.getName();
            Optional<User> user = userService.getUser(email);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found!");
            }
            userService.updateBestScore(email, score);
        }
    }
    @GetMapping("/getBestScore")
    public int getBestScore(Principal principal){
        String email = "";
        if (principal != null){
            email = principal.getName();
            Optional<User> user = userService.getUser(email);
            if (!user.isPresent()) {
                throw new IllegalArgumentException("User not found!");
            }
        }
        Integer bestScore = userRepository.getBestScore(email);
        if (bestScore == null){
            return 0;
        }
        return bestScore;
    }
}
