package application.controllers.post;

import application.dto.requestDto.PostRequestDto;
import application.service.post.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String getPost(@AuthenticationPrincipal UserDetails userDetails) {
        return "currently logged in user to create post" + userDetails.getUsername();
    }

    @PostMapping
    public ResponseEntity<?> createPost(@ModelAttribute PostRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.postService.createPost(requestDto, userDetails);
    }
}
