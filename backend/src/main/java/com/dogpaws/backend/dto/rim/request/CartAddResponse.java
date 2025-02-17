package com.dogpaws.backend.dto.rim.request;

import lombok.Builder;
import lombok.Data;

@Data
public class CartAddResponse {
    private String message;
    private boolean updated;
    @Builder
    public CartAddResponse(String message, boolean updated) {
        this.message = message;
        this.updated = updated;
    }
}
