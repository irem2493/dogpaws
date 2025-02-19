package com.dogpaws.backend.repository.dao.common;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeDao {

    Integer checkLike(@Param("myDogId") int myDogId,
                      @Param("likeCode") char likeCode,
                      @Param("dogId") int dogId);

    int insertLike(@Param("myDogId") int myDogId,
                    @Param("likeCode") char likeCode,
                    @Param("dogId") int dogId);

    int deleteLike(@Param("myDogId") int myDogId,
                    @Param("likeCode") char likeCode,
                    @Param("dogId") int dogId);


}
