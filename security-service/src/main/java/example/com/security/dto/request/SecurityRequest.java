package example.com.security.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


public record SecurityRequest(
        @NotNull
        String username,
        @NotNull
        String password
) {
}
