package com.dogpaws.backend.controller.ajy;

import com.dogpaws.backend.dto.ajy.DogDto;
import com.dogpaws.backend.dto.ajy.DogResponseDto;
import com.dogpaws.backend.service.ajy.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dog")
public class DogController {

    private final DogService dogService;

    @GetMapping("/dogList/{username}")
    public List<DogDto> getDogList(@PathVariable String username) {
        return dogService.getDogs(username);
    }

}
