package com.winter107r.cryptoanalyst.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IngestRequest(
        @NotBlank @Size(max = 512) String title,
        @NotBlank String content,
        @NotBlank @Size(max = 128) String source,
        String symbol   // optional, defaults to "ALL"
) {}
