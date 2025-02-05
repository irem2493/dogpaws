package com.dogpaws.backend.repository.dao.cys;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created on 2025-02-04 by 최윤서
 */
@Mapper
public interface DbtiDao {

    void dogType(@Param("dogType")String dogType, @Param("dogId")int dogId);

}
