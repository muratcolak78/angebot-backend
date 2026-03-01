package com.angebot.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public String health(){
        return "ok";
    }
    @GetMapping("/me")
    public String me(@RequestHeader("Authorization") String auth){
        return  "authorized";
    }
}
