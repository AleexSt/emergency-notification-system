package com.example.template.dto.request;

import jakarta.validation.Valid;
import lombok.Builder;

import java.util.List;


@Builder
public record RecipientListRequest(
        @Valid List<Long> recipientsIds
) {
}
