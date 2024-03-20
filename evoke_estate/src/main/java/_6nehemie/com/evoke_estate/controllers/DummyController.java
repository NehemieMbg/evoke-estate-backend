package _6nehemie.com.evoke_estate.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {
    
    @GetMapping("/test")
    public String test() {
        return "Hello World!";
    }
}
