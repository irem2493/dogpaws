package com.dogpaws.backend.controller.cys;

import com.dogpaws.backend.dto.cys.DogDto;
import com.dogpaws.backend.service.cys.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 2025-02-19 by 최윤서
 */
@RestController
@RequestMapping("/api/main")
public class MainController {

    @Autowired
    private MainService mainService;

    @GetMapping("/profile/{dogId}")
    public DogDto profile(@PathVariable int dogId) {
        DogDto dto = mainService.getDogById(dogId);
        return dto;
    }


}
