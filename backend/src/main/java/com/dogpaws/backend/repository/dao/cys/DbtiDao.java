package com.dogpaws.backend.repository.dao.cys;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created on 2025-02-04 by 최윤서
 */
@Mapper
public interface DbtiDao {

    public void dogType(String dogType);

}
