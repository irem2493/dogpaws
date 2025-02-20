package com.dogpaws.backend.client;

import com.dogpaws.backend.dto.rim.request.PaymentResponse;
import com.dogpaws.backend.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
@Service
public class TossPaymentClient {

    private final RestTemplate restTemplate;

    //@Value("${payment.toss.secret-key}")
    private String TOSS_SECRET_KEY = "test_sk_0RnYX2w532D5RXpDBnKMrNeyqApQ";

    //@Value("${payment.toss.api-url}")
    private String TOSS_API_URL = "https://api.tosspayments.com/v2/payments/";

    public PaymentResponse requestPaymentConfirm(String paymentKey, String orderId, Integer amount) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(TOSS_SECRET_KEY, "");
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> params = new HashMap<>();
            params.put("orderId", orderId);
            params.put("amount", amount);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(params, headers);

            ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(
                    TOSS_API_URL + paymentKey + "/confirm",
                    request,
                    PaymentResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }

            throw new PaymentException("결제 승인 요청 실패");

        } catch (RestClientException e) {
            log.error("토스페이먼츠 API 호출 실패: {}", e.getMessage(), e);
            throw new PaymentException("결제 승인 중 오류가 발생했습니다.");
        }
    }
}