package com.dogpaws.backend.repository.dao.common;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeDao {

    Integer checkLike(@Param("username") String username,
                      @Param("likeCode") String likeCode,
                      @Param("likeId") String likeId);

    void insertLike(@Param("username") String username,
                    @Param("likeCode") String likeCode,
                    @Param("likeId") String likeId);

    void deleteLike(@Param("username") String username,
                    @Param("likeCode") String likeCode,
                    @Param("likeId") String likeId);
}
