package application.dto.requestDto;
import org.springframework.web.multipart.MultipartFile;

public record PostRequestDto(
        String title, String content, MultipartFile image_file
){

}
