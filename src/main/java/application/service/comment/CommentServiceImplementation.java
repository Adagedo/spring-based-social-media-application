package application.service.comment;

import application.dto.requestDto.CommentRequestDto;
import application.entity.comment.CommentEntity;
import application.entity.post.PostEntity;
import application.entity.user.UserEntity;
import application.repository.comment.CommentRepository;
import application.repository.post.PostRepository;
import application.repository.user.UserRepository;
import application.service.storage.ImageStorage;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.events.Comment;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class CommentServiceImplementation implements CommentService{

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ImageStorage imageStorage;
    private final CommentRepository commentRepository;

    public CommentServiceImplementation(PostRepository postRepository, UserRepository userRepository, ImageStorage imageStorage, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageStorage = imageStorage;
        this.commentRepository = commentRepository;
    }

    @Override
    public ResponseEntity<?> createComment(String post_id, CommentRequestDto requestDto, UserDetails userDetails) {

        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        String current_user = userDetails.getUsername();
        Optional<PostEntity> optionalPost = postRepository.findById(UUID.fromString(post_id));
        UserEntity user = userRepository.findByUsername(current_user);

        if(optionalPost.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post not found");
        if(user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("invalid user");

        PostEntity post = optionalPost.get();

        if (requestDto.file() == null){
            CommentEntity comment = CommentEntity.builder()
                    .post(post)
                    .user(user)
                    .content(requestDto.content())
                    .build();
            commentRepository.save(comment);
        }else {

            MultipartFile originalFile;
            String filename = null;
            Long filesize = null;

            if(!requestDto.file().isEmpty()){
                originalFile = requestDto.file();
                filename = originalFile.getOriginalFilename();
                filesize = requestDto.file().getSize();
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

            CommentEntity comment = CommentEntity.builder()
                    .content(requestDto.content())
                    .post(post)
                    .user(user)
                    .image_path(String.valueOf(image_path))
                    .build();
            commentRepository.save(comment);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("comment created successfully");
    }

    @Override
    public ResponseEntity<?> editComment(String comment_id, CommentRequestDto requestDto, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        String current_user = userDetails.getUsername();
        UserEntity user = userRepository.findByUsername(current_user);
        Optional<CommentEntity> optionalComment = commentRepository.findById(UUID.fromString(comment_id));

        if(optionalComment.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment not found");
        CommentEntity comment = optionalComment.get();

        UserEntity author = comment.getUser();
        if(!author.getId().equals(user.getId())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("you are not authorized to edit this comment");

        if(requestDto.file().isEmpty()){
            comment.setContent(requestDto.content());
            commentRepository.save(comment);
        }else {

            MultipartFile originalFile = requestDto.file();
            String filename = originalFile.getOriginalFilename();
            long filesize = requestDto.file().getSize();

            assert filename != null;
            if(!filename.endsWith(".jpg") || !filename.endsWith(".png")) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Image format not supported, upload a .jpg file or a .png file");

            if(filesize > 22224157) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file to large, file size be 20mb or lower");

            Path image_path = this.imageStorage.load(filename);

            comment.setContent(requestDto.content());
            comment.setImage_path(String.valueOf(image_path));
            commentRepository.save(comment);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("comment updated!");
    }


    @Override
    public ResponseEntity<?> getAllCommentsAssociatedToAPost(String post_id, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        List<CommentEntity> comments = commentRepository.findByPostId(UUID.fromString(post_id));
        if (comments.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no comments found for this post");

        for(CommentEntity comment: comments){
            if (comment.getImage_path() != null){
                String name = comment.getImage_path().replaceFirst("^upload_dir/?", "");
                comment.setImage_path("http://localhost:8888/api/v1/images" + name.replace("\\", "/"));
            }
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(comments);
    }

    @Override
    public ResponseEntity<?> deleteComment(String comment_id, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        String current_user = userDetails.getUsername();

        Optional<CommentEntity> optionalComment = commentRepository.findById(UUID.fromString(comment_id));
        UserEntity author = userRepository.findByUsername(current_user);

        if(optionalComment.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("comment not found");
        if(author == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid user");
        CommentEntity comment = optionalComment.get();
        if(!comment.getUser().getId().equals(author.getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not Authorized to delete this comment");

        commentRepository.delete(comment);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("comment deleted successfully");
    }

    @Override
    public ResponseEntity<?> createReply(String parent_comment_id, CommentRequestDto requestDto, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");

        Optional<CommentEntity> comment = commentRepository.findById(UUID.fromString(parent_comment_id));

        String current_user = userDetails.getUsername();
        UserEntity author = userRepository.findByUsername(current_user);

        if(comment.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("this comment does not exist");
        if(author == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("invalid user");

        CommentEntity parentComment = comment.get();

        if(requestDto.file() == null){
            CommentEntity reply = CommentEntity.builder()
                    .content(requestDto.content())
                    .user(author)
                    .build();
            parentComment.addReply(reply);
        }else {
            MultipartFile originalFile = requestDto.file();
            String filename = originalFile.getOriginalFilename();
            long filesize = requestDto.file().getSize();

            assert filename != null;
            if(!filename.endsWith(".jpg") || !filename.endsWith(".png")) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Image format not supported, upload a .jpg file or a .png file");

            if(filesize > 22224157) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file to large, file size be 20mb or lower");

            Path image_path = this.imageStorage.load(filename);

            CommentEntity reply = CommentEntity.builder()
                    .content(requestDto.content())
                    .image_path(String.valueOf(image_path))
                    .user(author)
                    .build();
            parentComment.addReply(reply);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("reply created");
    }

    @Override
    public ResponseEntity<?> editReply(String parent_comment_id, CommentRequestDto requestDto, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");
        String current_user = userDetails.getUsername();
        UserEntity author = userRepository.findByUsername(current_user);
        Optional<CommentEntity> comment = commentRepository.findById(UUID.fromString(parent_comment_id));


        if(comment.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("this comment does not exist");
        if(author == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("invalid user");
        CommentEntity parentComment = comment.get();

        if(!author.getId().equals(parentComment.getUser().getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you are not authorized to edit this reply");
        if(requestDto.file() == null){
            CommentEntity update_reply = new CommentEntity();
            update_reply.setContent(requestDto.content());
            parentComment.addReply(update_reply);
        }else {
            MultipartFile originalFile = requestDto.file();
            String filename = originalFile.getOriginalFilename();
            long filesize = requestDto.file().getSize();

            assert filename != null;
            if(!filename.endsWith(".jpg") || !filename.endsWith(".png")) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Image format not supported, upload a .jpg file or a .png file");

            if(filesize > 22224157) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file to large, file size be 20mb or lower");

            Path image_path = this.imageStorage.load(filename);

            CommentEntity updated_reply = new CommentEntity();
            updated_reply.setContent(requestDto.content());
            updated_reply.setImage_path(String.valueOf(image_path));
            parentComment.addReply(updated_reply);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("reply created");

    }

    @Override
    public ResponseEntity<?> getAllRepliesAssociatedToAComment(String parent_comment_id, UserDetails userDetails) {

        return null;
    }

    @Override
    public ResponseEntity<?> deleteReply(String parent_comment_id, UserDetails userDetails) {
        if (userDetails.getUsername().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("you dont have an account, please create an account to continue using this service");
        String current_user = userDetails.getUsername();
        UserEntity author = userRepository.findByUsername(current_user);
        Optional<CommentEntity> comment = commentRepository.findById(UUID.fromString(parent_comment_id));


        if(comment.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("this comment does not exist");
        if(author == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("invalid user");
        CommentEntity parentComment = comment.get();

        if(!author.getId().equals(parentComment.getUser().getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you are not authorized to delete this reply");
        commentRepository.delete((CommentEntity) parentComment.getReplies());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("reply deleted");
    }
}
