package com.dogpaws.backend.repository.dao.common;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeDao {

    Integer checkLike(@Param("username") String username,
                      @Param("likeCode") char likeCode,
                      @Param("dogId") int dogId);

    int insertLike(@Param("username") String username,
                    @Param("likeCode") char likeCode,
                    @Param("dogId") int dogId);

    int deleteLike(@Param("username") String username,
                    @Param("likeCode") char likeCode,
                    @Param("dogId") int dogId);


}
