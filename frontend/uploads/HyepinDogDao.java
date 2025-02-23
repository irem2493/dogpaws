package com.dogpaws.backend.repository.dao.hyepin;


import com.dogpaws.backend.dto.hyepin.TestDogDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HyepinDogDao {

    // 기본 정보 삽입, auto-generated key는 dogId에 설정됨
    void insertDog(TestDogDto dog);

    // 성격 정보 삽입 (dog_id와 성격 코드)
    void insertDogPersonal(@Param("dogId") int dogId, @Param("dogPersonalGbnCd") String dogPersonalGbnCd);

    // 놀이 정보 삽입 (dog_id와 놀이 코드)
    void insertDogPlay(@Param("dogId") int dogId, @Param("dogPlayGbnCd") String dogPlayGbnCd);
}