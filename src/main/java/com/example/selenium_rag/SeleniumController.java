package com.example.selenium_rag;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class SeleniumController {

    private final SeleniumService seleniumService;

    public SeleniumController(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

    @PostMapping("/findWords")
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
