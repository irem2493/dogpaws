package com.dogpaws.backend.repository.dao.cys;

import com.dogpaws.backend.dto.cys.DogDto;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created on 2025-02-19 by 최윤서
 */
@Mapper
public interface MainDao {

    DogDto getDogById(int dogId);

}
