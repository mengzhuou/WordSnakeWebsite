package com.gtbackend.gtbackend.api;

import com.gtbackend.gtbackend.dao.FeedbackRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class FeedbackAPI {
    private FeedbackRepository feedbackRepository;
    private UserAPI userAPI;

    public FeedbackAPI(FeedbackRepository feedbackRepository, UserAPI userAPI) {
        this.feedbackRepository = feedbackRepository;
        this.userAPI = userAPI;
    }

    @PostMapping("/addFeedback")
    @ResponseBody
    public void addFeedback(@RequestParam String message, Float rating) {
        ResponseEntity<String> userEmailResponse = userAPI.getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String email = userEmailResponse.getBody();
            LocalDateTime timestampValue = LocalDateTime.now();
            String timestamp = timestampValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String status = "New";

            feedbackRepository.addFeedback(email, timestamp, message, rating, status);
        }
    }

    @GetMapping("/getFeedback")
    public List<String> getFeedback(){
        return feedbackRepository.getFeedback();
    }

    @PostMapping("/updateFeedbackStatus")
    @ResponseBody
    public void updateFeedbackStatus(@RequestParam Long id, String status){
        feedbackRepository.updateFeedbackStatus(id, status);
    }
}
