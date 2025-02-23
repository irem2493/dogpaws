package com.dogpaws.backend.repository.dao.rim;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FCMTokenDao {
    String getFcmToken(@Param("username") String username);
    void saveFcmToken(@Param("username") String username, @Param("token") String token);
    void deleteFcmToken(@Param("username") String username);
}