package com.dogpaws.backend.dao.common;

import com.dogpaws.backend.dto.common.GubnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GubnDao {
    public List<GubnDto> getGubnList(@Param("gubnCode") String gubnCode);
    public List<GubnDto> getSkillList();
    public List<GubnDto> getSkillName(@Param("stackCode") String stackCode);
}
