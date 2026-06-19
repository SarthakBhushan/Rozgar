package com.rozgar.backend.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(

        @NotNull(message = "Thread ID is required")
        Long threadId,

        @NotNull(message = "Message content cannot be empty")
        @NotBlank(message = "Message content cannot be blank")
        @Size(min = 1, max = 2000, message = "Message cannot exceed 2000 characters")
        String content
) {}
