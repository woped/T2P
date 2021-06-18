package de.dhbw.text2process.controler;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ErrorControler {

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/defaultError")
    public String getErrorPage(){
        return "You just discovered a misbehavior, thank you for helping us to enhance WoPeDs usability.";
    }
}
