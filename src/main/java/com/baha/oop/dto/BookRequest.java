package com.baha.oop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @Pattern(regexp = "^(\\d{10}|\\d{13})?$", message = "ISBN must be 10 or 13 digits")
    private String isbn;
}
