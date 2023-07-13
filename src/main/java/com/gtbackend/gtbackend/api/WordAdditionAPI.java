package com.gtbackend.gtbackend.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gtbackend.gtbackend.dao.WordAdditionRepository;
import com.gtbackend.gtbackend.service.UserService;
import com.gtbackend.gtbackend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( path = "api/v1")
public class WordAdditionAPI {
    @Autowired
    private UserService userService;
    @Autowired
    private WordService wordService;
    @Autowired
    private UserAPI userAPI;
    @Autowired
    private WordAdditionRepository wordAdditionRepository;

    @Autowired
    public WordAdditionAPI(
            UserService userService,
            WordAdditionRepository wordAdditionRepository,
            WordService wordService,
            UserAPI userAPI
    ) {
        this.userService = userService;
        this.wordAdditionRepository = wordAdditionRepository;
        this.wordService = wordService;
        this.userAPI = userAPI;
    }

    @PostMapping("/getOnlineDefinition")
    public String getOnlineDefinition(@RequestParam String word) throws JsonProcessingException {
        return wordService.getOnlineDefinition(word);
    }

    @PostMapping("/requestForWordAddition")
    public boolean requestForWordAddition(@RequestParam String word) throws JsonProcessingException {
        ResponseEntity<String> userEmailResponse = userAPI.getUserEmail();
        if (userEmailResponse.getStatusCode().is2xxSuccessful()) {
            String definition = getOnlineDefinition(word);
            String userEmail = userEmailResponse.getBody();
            userService.requestForWordAddition(userEmail, word, definition);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/isWordLegitimate")
    public boolean isWordLegitimate(@RequestParam String word) throws JsonProcessingException {
        return wordService.isWordLegitimate(word);
    }

    @GetMapping("/isWordForAdditionExist")
    public boolean isWordForAdditionExist(@RequestParam String word){
        return wordAdditionRepository.isWordForAdditionExist(word);
    }

    @GetMapping("/getFromWordAddition")
    public List<String> getFromWordAddition(){
        return wordAdditionRepository.getFromWordAddition();
    }

    @PostMapping("/deleteWordAdditionDefinition")
    public boolean deleteWordAdditionDefinition(@RequestParam Integer wordAdditionId) {
        try {
            wordService.deleteWordAdditionDefinition(wordAdditionId);
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to delete this word definition row.");
        }
    }
}
