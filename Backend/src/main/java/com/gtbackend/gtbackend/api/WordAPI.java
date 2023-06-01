package com.gtbackend.gtbackend.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gtbackend.gtbackend.dao.WordAdditionRepository;
import com.gtbackend.gtbackend.dao.WordRepository;
import com.gtbackend.gtbackend.model.Word;
import com.gtbackend.gtbackend.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/api/v1")
public class WordAPI {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordAdditionRepository wordAdditionRepository;
    @Autowired
    private WordService wordService;

    @RequestMapping("/getWords")
    public List<Word> getWords(){
        return wordRepository.findAll();
    }

    @RequestMapping("/getHintWordAndDef")
    public List<String> getHintWordAndDef(@RequestParam String inputWordLetter) throws IllegalArgumentException{
        List<String> hintWordsAndDefs = wordRepository.getHintWordAndDef(inputWordLetter, PageRequest.of(0,5));
        List<String> numberedHintWordsAndDefs = new ArrayList<>();
        Random random = new Random();
        int counter = 1;
        for (String hintWordAndDef : hintWordsAndDefs){
            int randomIndex = random.nextInt(hintWordsAndDefs.size());
            String randomHintWordAndDef  = hintWordsAndDefs.get(randomIndex);
            numberedHintWordsAndDefs.add(counter + ". " + randomHintWordAndDef + "\n");
            counter++;
        }
        return numberedHintWordsAndDefs;
    }


    @RequestMapping("/getWordAndDef")
    public List<String> getWordAndDef(@RequestParam String inputWord) throws IllegalArgumentException{
        if (isWordExist(inputWord)) {
            return wordRepository.getWordAndDef(inputWord);
        }
        throw new IllegalArgumentException("The word does not exist. Please enter a valid word.");
    }
    @RequestMapping("/isWordExist")
    public boolean isWordExist(@RequestParam String inputWord) throws IllegalArgumentException{
        return wordRepository.isWordExist(inputWord);
    }

    @RequestMapping("/getRandomStart")
    public String getRandomStart(){
        Random randomStart = new Random();
        return String.valueOf((char) (randomStart.nextInt(26) + 'a'));
    }

    @RequestMapping("/getLetterFromPreviousWord")
    public String getLetterFromPreviousWord(@RequestParam String inputWord) throws IllegalArgumentException{
        if (isWordExist(inputWord)){
            return String.valueOf(inputWord.charAt(inputWord.length() - 1));
        }
        throw new IllegalArgumentException("The word does not exist. Please enter a valid word.");
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
}
