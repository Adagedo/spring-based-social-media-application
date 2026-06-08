package application.service.post;

import application.dto.requestDto.PostRequestDto;
import application.entity.post.PostEntity;
import application.entity.user.UserEntity;
import application.repository.post.PostRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class PostService {

    private final PostRepository repository;

    private final ImageStorage imageStorage;



    public PostService(PostRepository repository, ImageStorage imageStorage) {
        this.repository = repository;
        this.imageStorage = imageStorage;
    }

    public ResponseEntity<?> createPost(PostRequestDto requestDto, UserDetails userDetails){

        if(userDetails == null){
            ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");
        }

        assert userDetails != null;
        String current_user = userDetails.getUsername();
        if (current_user.isEmpty()){
            ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");
        }

        String title = requestDto.title();
        String content = requestDto.content();
        MultipartFile originalFile = null;
        String filename = null;
        Long filesize = null;
        System.out.println(title + content);

        if(requestDto.image_file()!=null && !requestDto.image_file().isEmpty()){
            originalFile = requestDto.image_file();
            filename = originalFile.getOriginalFilename();
            filesize = requestDto.image_file().getSize();

            System.out.println(filename);
        }

        if(filename == null || filename.isEmpty()){
            System.out.println("in here....");
            try {
                throw new BadRequestException("please upload a valid image file");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(filesize);
        if(filesize > 22224157){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("image size too big please upload another image of size 1 mb or lower");
        }

        Path image_path = this.imageStorage.load(filename);
        PostEntity post = PostEntity.builder()
                .title(title)
                .content(content)
                .image_path(String.valueOf(image_path))
                .user((UserEntity) userDetails)
                .build();

        this.imageStorage.store(originalFile);
        this.repository.save(post);
        return  ResponseEntity.ok("post created successfully!");
    }
}
