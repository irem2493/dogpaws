package com.dogpaws.backend.service.ajy;

import com.dogpaws.backend.dto.ajy.LocationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Service
public class LocationService {

    @Value("${kakao.api.key}")  // application.properties에서 값 가져오기
    private String kakaoApiKey;

    public LocationDto getCoordinatesFromAddress(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);  // API 키 적용
        headers.set("KA", "appName/1.0 os/Windows");  // 필수 KA 헤더 추가

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            JSONObject json = new JSONObject(response.getBody());
            JSONArray documents = json.getJSONArray("documents");
            if (!documents.isEmpty()) {
                JSONObject firstResult = documents.getJSONObject(0);
                double lat = firstResult.getDouble("y");  // 위도
                double lng = firstResult.getDouble("x");  // 경도

                LocationDto location = new LocationDto();
                location.setLatitude(lat);
                location.setLongitude(lng);

                return location;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
