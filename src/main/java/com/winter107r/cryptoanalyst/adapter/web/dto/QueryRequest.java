package com.winter107r.cryptoanalyst.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QueryRequest(
        @NotBlank @Size(max = 500) String question,
        Integer topK   // optional, defaults to 3
) {}
