package com.dogpaws.backend.repository.dao.hyepin;

import com.dogpaws.backend.dto.hyepin.DeclarationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeclarationDao {

    public int insertDeclaration(DeclarationDto declarationDto);
}
