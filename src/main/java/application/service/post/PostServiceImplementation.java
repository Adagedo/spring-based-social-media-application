package application.service.post;

import application.dto.requestDto.PostRequestDto;
import application.entity.post.PostEntity;
import application.entity.user.UserEntity;
import application.repository.post.PostRepository;
import application.repository.user.UserRepository;
import application.service.storage.ImageStorage;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImplementation implements PostService{

    private final PostRepository repository;

    private final ImageStorage imageStorage;

    private final UserRepository userRepository;




    public PostServiceImplementation(PostRepository repository, ImageStorage imageStorage, UserRepository userRepository) {
        this.repository = repository;
        this.imageStorage = imageStorage;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createPost(PostRequestDto requestDto, UserDetails userDetails){

        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        String title = requestDto.title();
        String content = requestDto.content();
        MultipartFile originalFile = null;
        String filename = null;
        Long filesize = null;

        if(requestDto.image_file()!=null && !requestDto.image_file().isEmpty()){
            originalFile = requestDto.image_file();
            filename = originalFile.getOriginalFilename();
            filesize = requestDto.image_file().getSize();
        }

        if(filename == null || filename.isEmpty()){
            System.out.println("in here....");
            try {
                throw new BadRequestException("please upload a valid image file");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        if(!filename.endsWith(".jpg") || !filename.endsWith(".png")) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Image format not supported, upload a .jpg file or a .png file");

        if(filesize > 22224157) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file to large, file size be 20mb or lower");

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

    @Override
    public ResponseEntity<?> getAllPost(UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        List<PostEntity> posts = this.repository.findAll().stream()
                .filter(post -> post.getImage_path() != null && post.getImage_path().startsWith("upload_dir"))
                .peek(post -> {
                    String name = post.getImage_path().replaceFirst("^upload_dir/?", "");
                    post.setImage_path("http://localhost:8888/api/v1/images" + name.replace("\\", "/"));
                }).toList();

        if (posts.isEmpty()) ResponseEntity.status(404).body("not post to fetch");

        return ResponseEntity.status(200).body(posts);
    }

    @Override
    public ResponseEntity<?> getPostById(String post_id, UserDetails userDetails) {

        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        Optional<PostEntity> OptionalPost = repository.findById(UUID.fromString(post_id));
        if(OptionalPost.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post not found");
        }
        PostEntity post = OptionalPost.get();
        String name = post.getImage_path().replaceFirst("^upload_dir/?", "");
        post.setImage_path("http://localhost:8888/api/v1/images" + name.replace("\\", "/"));

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(post);
    }

    @Override
    public ResponseEntity<?> getAllOwnersPost(String owner_id, UserDetails userDetails) {

        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        Optional<UserEntity> optionalUser = userRepository.findById(UUID.fromString(owner_id));

        if(optionalUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("invalid user");

        UserEntity user = optionalUser.get();
        List<PostEntity> posts = repository.findByUser(user).stream()
                .filter(post -> post.getImage_path() != null && post.getImage_path().startsWith("upload_dir"))
                .peek(post -> {
                    String name = post.getImage_path().replaceFirst("^upload_dir/?", "");
                    post.setImage_path("http://localhost:8888/api/v1/images" + name.replace("\\", "/"));
                }).toList();

        if (posts.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user have not created any post");

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(posts);
    }


    @Override
    public ResponseEntity<?> editPost(String post_id, PostRequestDto requestDto, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        String current_user = userDetails.getUsername();

        UserEntity user = userRepository.findByUsername(current_user);
        Optional<PostEntity> optionalPost = repository.findById(UUID.fromString(post_id));

        if(optionalPost.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post not found");

        PostEntity post = optionalPost.get();
        UserEntity postOwner = post.getUser();

        if(postOwner.getId().equals(user.getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you are not authorized to update this post");

        MultipartFile originalFile = null;
        String filename = null;
        Long filesize = null;

        if(requestDto.image_file()!=null && !requestDto.image_file().isEmpty()){
            originalFile = requestDto.image_file();
            filename = originalFile.getOriginalFilename();
            filesize = requestDto.image_file().getSize();
        }

        if(filename == null || filename.isEmpty()){
            try {
                throw new BadRequestException("please upload a valid image file");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        if(!filename.endsWith(".jpg") || !filename.endsWith(".png")) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Image format not supported, upload a .jpg file or a .png file");

        if(filesize > 22224157) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file to large, file size be 20mb or lower");

        Path image_path = this.imageStorage.load(filename);

        post.setTitle(requestDto.title());
        post.setContent(requestDto.content());
        post.setImage_path(String.valueOf(image_path));
        post.setUpdateAt(Timestamp.valueOf(String.valueOf(Instant.now())));

        this.imageStorage.store(originalFile);
        this.repository.save(post);

        return ResponseEntity.status(HttpStatus.CREATED).body("post updated");

    }

    @Override
    public ResponseEntity<?> deletePost(String post_id, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        Optional<PostEntity> optionalPost = repository.findById(UUID.fromString(post_id));

        if(optionalPost.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post not found");

        PostEntity post = optionalPost.get();

        String current_user = userDetails.getUsername();

        UserEntity user = userRepository.findByUsername(current_user);

        UserEntity postOwner = post.getUser();

        if(!postOwner.getId().equals(user.getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you are not authorized to deleted this post");

        this.repository.delete(post);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("post deleted successfully");
    }
}
