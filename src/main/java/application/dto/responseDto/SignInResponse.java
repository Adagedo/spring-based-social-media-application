package application.dto.responseDto;

import java.util.UUID;

public record SignInResponse(UUID id, String username, String email, String status) {
}
