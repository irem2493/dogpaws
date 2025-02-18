package com.dogpaws.backend.repository.dao.common;

import com.dogpaws.backend.dto.common.GubnDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GubnDao {
    public List<GubnDto> getGubnList(@Param("groupCode") String groupCode);
    public GubnDto getGubn(@Param("groupCode") String groupCode, @Param("gubnCode") String gubnCode);
}
