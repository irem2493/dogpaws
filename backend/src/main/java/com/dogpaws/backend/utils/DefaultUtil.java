package com.dogpaws.backend.utils;

import com.dogpaws.backend.dto.hyepin.FilterDto;

public class DefaultUtil {
    //필터 기본값 설정
    public static FilterDto SetDefault(FilterDto filterDto) {
        if (filterDto.getBreedGbnCd() == null || filterDto.getBreedGbnCd().isEmpty()) {
            filterDto.setBreedGbnCd(null);  // 빈 문자열일 때 null로 설정
        }
        if (filterDto.getWeight() == null) {
            filterDto.setWeight(0);
        }
        if (filterDto.getIsMix() == null || filterDto.getIsMix().isEmpty()) {
            filterDto.setIsMix("Y");
        }
        if (filterDto.getBloodTestCertified() == null || filterDto.getBloodTestCertified().isEmpty()) {
            filterDto.setBloodTestCertified("N");
        }
        if (filterDto.getVaccinationCertified() == null || filterDto.getVaccinationCertified().isEmpty()) {
            filterDto.setVaccinationCertified("N");
        }
        if (filterDto.getHealthRecordCertified() == null || filterDto.getHealthRecordCertified().isEmpty()) {
            filterDto.setHealthRecordCertified("N");
        }

        return filterDto;
    }


}
