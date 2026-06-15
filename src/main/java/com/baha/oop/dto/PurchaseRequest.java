package com.baha.oop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequest {
    @NotNull
    private Long bookId;
    
    @NotNull
    private Long memberId;
    
    private String deliveryAddress;
}
