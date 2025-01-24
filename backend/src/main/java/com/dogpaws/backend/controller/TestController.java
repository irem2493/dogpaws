package com.dogpaws.backend.controller;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String test() {
        return "test";
    }

    @GetMapping("/test2")
    public String postTest(@RequestParam("id") String id) {
        return "success";
    }

    @GetMapping("/test3")
    public String putTest() {
        System.out.println("테스트@@@@@@@@@@@@@@@@@@@@@@@ @");
        return "success";
    }

    @DeleteMapping("/test4")
    public String deleteTest(@RequestParam String id) {
        System.out.println("테스트@@@@@@@@@@@@@@@@@@@@@@@ " + id);

        return "success";
    }

    @GetMapping("/test5")
    public String test5(@RequestParam String id) {
        System.out.println("ㅁㄴㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ" + id);
        return "success";
    }
}

