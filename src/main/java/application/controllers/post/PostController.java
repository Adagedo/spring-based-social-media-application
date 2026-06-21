package application.controllers.post;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.dto.requestDto.PostRequestDto;
import application.service.post.PostServiceImplementation;

@RestController
@RequestMapping("/api/v1/users/posts")
public class PostController {

    private final PostServiceImplementation postService;

    public PostController(PostServiceImplementation postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(@ModelAttribute PostRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.postService.createPost(requestDto, userDetails);
    }

    @GetMapping
    public ResponseEntity<?> getAllPost(@AuthenticationPrincipal UserDetails userDetails){
        return this.postService.getAllPost(userDetails);
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<?> getSinglePost(@PathVariable String post_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.postService.getPostById(post_id, userDetails);
    }

    @GetMapping("/{owner_id}")
    public ResponseEntity<?> getPostByOwner(@PathVariable String owner_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.postService.getAllOwnersPost(owner_id, userDetails);
    }

    @PostMapping("/{post_id}")
    public ResponseEntity<?> updatePost(@PathVariable String post_id, @ModelAttribute PostRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.postService.editPost(post_id, requestDto, userDetails);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@PathVariable String post_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.postService.deletePost(post_id, userDetails);
    }
}
