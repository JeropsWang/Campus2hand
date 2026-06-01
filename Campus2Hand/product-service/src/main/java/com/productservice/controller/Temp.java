package com.productservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Temp {

    @GetMapping("/temp")
    public void Hello(){
        System.out.println("HI");
    }


}
