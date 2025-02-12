package com.dogpaws.backend.repository.dao.cys;

import com.dogpaws.backend.dto.ajy.DogDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created on 2025-02-11 by 최윤서
 */
@Mapper
public interface ChatDao {
    public DogDto getChatProfile(@Param("id") int id);
}
