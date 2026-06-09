package application.dto.requestDto;

import org.springframework.web.multipart.MultipartFile;

public record CommentRequestDto(String content, MultipartFile file) {
}
