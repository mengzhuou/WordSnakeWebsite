package com.gtbackend.gtbackend.api;

import com.gtbackend.gtbackend.dao.UserRepository;
import com.gtbackend.gtbackend.dao.WordAdditionRepository;
import com.gtbackend.gtbackend.model.Role;
import com.gtbackend.gtbackend.model.User;
import com.gtbackend.gtbackend.security.JwtService;
import com.gtbackend.gtbackend.service.UserService;
import com.gtbackend.gtbackend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping( path = "api/v1")
public class UserAPI {

    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WordService wordService;
    String token = "";
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WordAdditionRepository wordAdditionRepository;

    @Autowired
    public UserAPI(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            WordAdditionRepository wordAdditionRepository,
            WordService wordService
            ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.wordAdditionRepository = wordAdditionRepository;
        this.wordService = wordService;
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) throws ServletException {
        request.logout();
    }

    //can get Bearer token from here, the last two rows are used for access user info
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) throws AuthenticationException, BadCredentialsException {
        String email = body.get("email");
        String password = body.get("password");
        Optional<User> tmp = userService.getUser(email);
        User usr = tmp.get();
        if (tmp.isEmpty()) {
            throw new BadCredentialsException("Please enter your email or password.");
        }
        if (passwordEncoder.matches(password, usr.getPassword())) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(usr.getUsername(),
            usr.getPassword()));
            try{
                token = jwtService.generateToken(usr);
                return ResponseEntity.ok(token);
            } catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate JWT token.");
            }
        } else {
            throw new BadCredentialsException("Email or Password does not match our records.");
        }
    }

    @PostMapping("/register")
    public void addUser(@RequestBody Map<String, String> body) throws IllegalArgumentException, DateTimeParseException {
        Role role = Role.USER;
        User user = new User(
                body.get("email"),
                passwordEncoder.encode(body.get("password")),
                body.get("name"),
                LocalDate.parse(body.get("dob")),
                false);
        userService.addUser(user);
    }

    @RequestMapping("/userInfo")
    public ResponseEntity<User> userInfo(){
        if (token != null) {
            String userEmail = jwtService.extractUsername(token);

            Optional<User> user = userService.getUser(userEmail);
            if (user.isPresent()){
                return ResponseEntity.ok(user.get());
            } else{
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @RequestMapping("/getUserEmail")
    public ResponseEntity<String> getUserEmail(){
        if (token != null) {
            String userEmail = jwtService.extractUsername(token);

            Optional<User> user = userService.getUser(userEmail);
            if (user.isPresent()){
                User emailOnlyUser = new User();
                emailOnlyUser.setEmail(user.get().getEmail());
                return ResponseEntity.ok(emailOnlyUser.getEmail());
            } else{
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @GetMapping("/getBestScore")
    @ResponseBody
    public int getBestScore() {
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            Integer bestScore = userRepository.getBestScore(userEmail);
            return bestScore != null ? bestScore : 0;
        } else {
            return 0;
        }
    }

    @GetMapping("/getUnlimitedBestScore")
    @ResponseBody
    public int getUnlimitedBestScore() {
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            Integer bestScore = userRepository.getUnlimitedBestScore(userEmail);
            return bestScore != null ? bestScore : 0;
        } else {
            return 0;
        }
    }

    @PostMapping("/updateBestScore")
    @ResponseBody
    public int updateBestScore(@RequestParam int currentScore) {
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            Integer previousBestScore = getBestScore();
            if (previousBestScore < currentScore) {
                userRepository.updateBestScore(userEmail, currentScore);
                return currentScore;
            };
            return previousBestScore;
        }
        return -1;
    }


    @PostMapping("/updateUnlimitedBestScore")
    @ResponseBody
    public int updateUnlimitedBestScore(@RequestParam int currentScore) {
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            Integer previousBestScore = getUnlimitedBestScore();
            if (previousBestScore < currentScore) {
                userRepository.updateUnlimitedBestScore(userEmail, currentScore);
                return currentScore;
            };
            return previousBestScore;
        }
        return -1;
    }

    @GetMapping("/getNumOfUsers")
    public int getNumOfUsers(){
        return userRepository.numOfUsers();
    }

    @GetMapping("/getSignupRank")
    @ResponseBody
    public int getSignupRank() {
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            Integer rank = userRepository.getSignupRank(userEmail);
            return rank != null ? rank : -1;
        } else {
            return -2;
        }
    }

    @GetMapping("/getLeaderBoard")
    public List<Object[]> getLeaderBoard(){ return userRepository.getLeaderBoard(); }

    @GetMapping("/getUnlimitedLeaderBoard")
    public List<Object[]> getUnlimitedLeaderBoard(){ return userRepository.getUnlimitedLeaderBoard(); }

    @GetMapping("/getRole")
    public String getRole() {
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            String role = userRepository.getRole(userEmail);
            return role;
        } else {
            return "Error getting email";
        }
    }
    @GetMapping("/isAdmin")
    public boolean isAdmin(){
        ResponseEntity<String> userEmailResponse = getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String userEmail = userEmailResponse.getBody();
            String role = userRepository.getRole(userEmail);
            if (role == "ADMIN"){
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
