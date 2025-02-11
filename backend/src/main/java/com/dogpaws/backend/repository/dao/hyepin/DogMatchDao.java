package com.dogpaws.backend.repository.dao.hyepin;

import com.dogpaws.backend.dto.hyepin.FilterDto;
import com.dogpaws.backend.dto.hyepin.MatchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DogMatchDao {
    //필터
    public FilterDto getFilterBydogIdAndMatchType(@Param("dogId") int dogId, @Param("matchType") char matchType);
    public int insertFilter(FilterDto filterDto);
    public int updateFilter(FilterDto filterDto);
    public int deleteFilter(@Param("dogId") int dogId, @Param("matchType") char matchType);
    
    //매칭 (친구)
    public List<MatchDto> getDogFriendMatchList(@Param("dogId") int dogId, @Param("username") String username);
}
