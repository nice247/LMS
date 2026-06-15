package com.baha.oop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRequest {
    @NotNull
    private Long bookId;
    @NotNull
    private Long memberId;
    private boolean deliveryRequired;
    private String deliveryAddress;
}
