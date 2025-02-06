package com.dogpaws.backend.repository.dao.hyepin;

import com.dogpaws.backend.dto.hyepin.DogMatchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DogMatchDao {
    public DogMatchDto getFilterBydogIdAndMatchType(@Param("dogId") int dogId, @Param("matchType") char matchType);
    public int insertFilter(DogMatchDto dogMatchDto);
    public int updateFilter(DogMatchDto dogMatchDto);
    public int deleteFilter(@Param("dogId") int dogId, @Param("matchType") char matchType);
}
