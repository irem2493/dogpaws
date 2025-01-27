package com.dogpaws.frontend.service;

import com.dogpaws.frontend.exception.UnauthorizedAccessException;
import com.dogpaws.frontend.global.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class ApiRequestService {

    private final WebClient webClient;

    public ApiRequestService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 사용방법
     * 파라미터 없는 GET 요청
     * ApiResponse response = apiRequestService.fetchData("/login");
     * <p>
     * 파라미터가 있는 GET 요청
     * Map<String, String> params = Map.of("key", "value");
     * ApiResponse response = apiRequestService.fetchData("/login", params);
     * <p>
     */
    public ApiResponse fetchData(String uri, Map<String, String> params, String token, boolean disableEncoding) {
        try {
            if (disableEncoding) {
                StringBuilder urlBuilder = new StringBuilder(uri);
                if (params != null && !params.isEmpty()) {
                    urlBuilder.append("?");
                    params.forEach((key, value) -> urlBuilder.append(key).append("=").append(value).append("&"));
                    urlBuilder.setLength(urlBuilder.length() - 1);
                }
                String fullUrl = urlBuilder.toString();
                log.info("인코딩되지 않은 URL: {}", fullUrl);


                return webClient
                        .get()
                        .uri(fullUrl)
                        .headers(headers -> {
                            if (token != null && !token.isEmpty()) {
                                headers.setBearerAuth(token);
                            }
                        })
                        .exchangeToMono(this::handleResponseWithAuthCheck)
                        .block();
            } else {
                return webClient
                        .get()
                        .uri(uriBuilder -> {
                            uriBuilder.path(uri);
                            if (params != null && !params.isEmpty()) {
                                params.forEach(uriBuilder::queryParam);
                            }
                            return uriBuilder.build();
                        })
                        .headers(headers -> {
                            if (token != null && !token.isEmpty()) {
                                headers.setBearerAuth(token);
                            }
                        })
                        .exchangeToMono(this::handleResponseWithAuthCheck)
                        .block();
            }
        } catch (UnauthorizedAccessException e) {
            throw e;
        } catch (Exception e) {
            log.error("GET 요청 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "GET 요청 실패", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * GET 요청으로 데이터 조회(파라미터 없이).
     */
    public ApiResponse fetchData(String uri) {
        return fetchData(uri, null, null,false);
    }

    /**
     * GET 요청으로 데이터 조회(파라미터 포함, 토큰 제외).
     */
    public ApiResponse fetchData(String uri, Map<String, String> params,Boolean disableEncoding) {
        return fetchData(uri, params,null, disableEncoding);
    }

    /**
     * GET 요청으로 데이터 조회(파라미터 제외, 토큰 포함).
     */
    public ApiResponse fetchDataToken(String uri, String token) {
        return fetchData(uri, null, token,false);
    }


    /**
     * 응답 처리 및 인증 체크.
     *  TODO: 차후 기능 개선
     */
    private Mono<ApiResponse> handleResponseWithAuthCheck(ClientResponse response) {
        HttpStatus statusCode = (HttpStatus) response.statusCode();

        if (statusCode == HttpStatus.FORBIDDEN) {
            return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        log.error("403 Forbidden 에러 발생: {}", errorBody);
                        throw new UnauthorizedAccessException("권한 없음");
                    });
        }

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        log.error("401 Unauthorized 에러 발생: {}", errorBody);
                        throw new UnauthorizedAccessException("인증 실패: 유효하지 않은 토큰");
                    });
        }
        return response.bodyToMono(Map.class)
                .map(body -> {
                    if (statusCode.is2xxSuccessful()) {
                        ApiResponse<Object> apiResponse = new ApiResponse<>(ApiResponse.ApiStatus.SUCCESS, body.get("body"));
                        log.info("데이터 {} ", apiResponse);
                        return apiResponse;
                    } else {
                        log.error("HTTP {} 에러: {}", statusCode.value(), body);
                        return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "HTTP " + statusCode.value() + " - " + body, statusCode.value());
                    }
                });
    }

    /**
     * POST 요청으로 데이터 전송 (파라미터 및 토큰 포함).
     */
    public ApiResponse postDataWithToken(String uri, Map<String, Object> body, String token) {
        try {
            return webClient
                    .post()
                    .uri(uri)
                    .headers(headers -> {
                        if (token != null && !token.isEmpty()) {
                            headers.setBearerAuth(token);
                        }
                    })
                    .bodyValue(body != null ? body : Map.of())
                    .exchangeToMono(this::handleResponseWithAuthCheck)
                    .block();
        } catch (UnauthorizedAccessException e) {
            throw e;
        } catch (Exception e) {
            log.error("POST 요청 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponse<>(ApiResponse.ApiStatus.ERROR, "POST 요청 실패", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}