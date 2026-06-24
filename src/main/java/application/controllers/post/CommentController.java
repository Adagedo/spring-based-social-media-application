package application.controllers.post;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import application.dto.requestDto.CommentRequestDto;
import application.service.comment.CommentService;

@RestController
@RequestMapping("/api/v1/users/posts/")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{post_id}")
    public ResponseEntity<?> commentOnPost(@PathVariable String post_id, @ModelAttribute CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.createComment(post_id, requestDto, userDetails);
    }

    @PutMapping("/{post_id}/{comment_id}")
    public ResponseEntity<?> updateComment(@PathVariable String comment_id, @ModelAttribute CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.editComment(comment_id, requestDto, userDetails);
    }

    @GetMapping("/{post_id}/comments")
    public ResponseEntity<?> getPostComment(@PathVariable String post_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.getAllCommentsAssociatedToAPost(post_id, userDetails);
    }

    @DeleteMapping("/{post_id}/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable String comment_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.deleteComment(comment_id, userDetails);
    }

    @PostMapping("/{post_id}/reply/{parent_comment_id}")
    public ResponseEntity<?> replyToPost(@PathVariable String parent_comment_id, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.createReply(parent_comment_id, requestDto, userDetails);
    }

    @PutMapping("/{post_id}/reply/{parent_comment_id}")
    public ResponseEntity<?> updateReply(@PathVariable String parent_comment_id, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.editReply(parent_comment_id, requestDto, userDetails);
    }

    @DeleteMapping("/{post_id}/comments/replies/{parent_comment_id}")
    public ResponseEntity<?> deleteReply(@PathVariable String parent_comment_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.deleteReply(parent_comment_id, userDetails);
    }
}
