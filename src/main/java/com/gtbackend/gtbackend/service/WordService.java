package com.gtbackend.gtbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gtbackend.gtbackend.dao.WordAdditionRepository;
import com.gtbackend.gtbackend.dao.WordRepository;
import com.gtbackend.gtbackend.model.Word;
import com.gtbackend.gtbackend.model.WordAddition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
public class WordService {

    @Autowired
    private WordAdditionRepository wordAdditionRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private RestTemplate restTemplate; // Used for making HTTP requests

    private static final String DICTIONARY_API_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    public String getOnlineDefinition(String word) throws JsonProcessingException {
        String apiUrl = DICTIONARY_API_BASE_URL + word;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        String jsonResponse = responseEntity.getBody();

        List<String> definitions = extractDefinitions(jsonResponse);
        return String.join(". ", definitions);
    }

    private List<String> extractDefinitions(String jsonResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        List<String> definitions = new ArrayList<>();

        if (rootNode.isArray()) {
            for (JsonNode entryNode : rootNode) {
                JsonNode meaningsNode = entryNode.get("meanings");

                if (meaningsNode.isArray() && meaningsNode.size() > 0) {
                    for (JsonNode meaningNode : meaningsNode) {
                        JsonNode definitionsNode = meaningNode.get("definitions");

                        if (definitionsNode.isArray() && definitionsNode.size() > 0) {
                            for (JsonNode definitionNode : definitionsNode) {
                                String definition = definitionNode.get("definition").asText();
                                definition = definition.endsWith(".") ? definition.substring(0, definition.length() - 1) : definition;
                                definitions.add(definition);
                            }
                        }
                    }
                }
            }
        }
        return definitions;
    }

    public boolean isWordLegitimate(String word) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = DICTIONARY_API_BASE_URL + word;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            String jsonResponse = responseEntity.getBody();
            boolean isLegitimate = !extractDefinitions(jsonResponse).isEmpty();
            return isLegitimate;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            } else {
                throw e;
            }
        }
    }

    public void storeWordDefinition(Integer wordAdditionId) {
        // Retrieve the WordAddition object from the database
        WordAddition wordAddition = wordAdditionRepository.findById(wordAdditionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid wordAdditionId"));

        String word = wordAddition.getWord();
        String definition = wordAddition.getDefinition();

        // Create a new Word object and set the word and definition values
        Word wordObject = new Word();
        wordObject.setWord(word);
        wordObject.setDefinition(definition);
        wordRepository.save(wordObject);
    }

    public void deleteWordAdditionDefinition(Integer wordAdditionId) {
        wordAdditionRepository.deleteById(wordAdditionId);
    }
}
