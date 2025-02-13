package com.dogpaws.backend.service.hyepin;

import com.dogpaws.backend.dto.hyepin.DeclarationDto;
import com.dogpaws.backend.repository.dao.hyepin.DeclarationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeclarationService {

    private final DeclarationDao declarationDao;

    public int insertDeclaration(DeclarationDto declarationDto) {
        int result = declarationDao.insertDeclaration(declarationDto);
        return result;
    }
}
