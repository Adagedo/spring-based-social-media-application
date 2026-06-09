package application.service.post;

import application.dto.requestDto.PostRequestDto;
import application.entity.post.PostEntity;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface PostService {

    ResponseEntity<?> createPost(PostRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> getAllPost(UserDetails userDetails);

    ResponseEntity<?> getPostById(String post_id, UserDetails userDetails);

    ResponseEntity<?> getAllOwnersPost(String owner_id, UserDetails userDetails);

    ResponseEntity<?> editPost(String post_id, PostRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> deletePost(String post_id, UserDetails userDetails);

}
