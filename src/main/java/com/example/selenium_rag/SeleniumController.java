package com.example.selenium_rag;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController

public class SeleniumController {

    private final SeleniumService seleniumService;

    public SeleniumController(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

@GetMapping("/")
    public ResponseEntity<String>healthcheck() {
        return ResponseEntity.ok("Selenium Service is running");
    }


    @PostMapping("/api/findWords")


    public ArrayList<String> findWords(@RequestBody ArrayList<String> words) {
        try {
           


            return seleniumService.findWordsRag(words);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error procesando búsqueda en Selenium",ex );
        }
    }
}
