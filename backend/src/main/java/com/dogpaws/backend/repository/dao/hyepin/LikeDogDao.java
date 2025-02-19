package com.dogpaws.backend.repository.dao.hyepin;

import com.dogpaws.backend.dto.hyepin.DeclarationDto;
import com.dogpaws.backend.dto.hyepin.LikeDogDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LikeDogDao {

    public List<LikeDogDto> getLikeList(@Param("myDogId") int myDogId, @Param("likeCode") String likeCode);
}
