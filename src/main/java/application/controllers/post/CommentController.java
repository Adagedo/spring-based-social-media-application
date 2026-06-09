package application.controllers.post;

import application.dto.requestDto.CommentRequestDto;
import application.service.comment.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{post_id}")
    public ResponseEntity<?> commentOnPost(@PathVariable String post_id, @ModelAttribute CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.createComment(post_id, requestDto, userDetails);
    }

    @PostMapping("/update/{comment_id}")
    public ResponseEntity<?> updateComment(@PathVariable String comment_id, @ModelAttribute CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.editComment(comment_id, requestDto, userDetails);
    }

    @GetMapping("/{post_id}")
    public ResponseEntity<?> getPostComment(@PathVariable String post_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.getAllCommentsAssociatedToAPost(post_id, userDetails);
    }

    @DeleteMapping("/delete/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable String comment_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.deleteComment(comment_id, userDetails);
    }

    @PostMapping("/reply/{parent_comment_id}")
    public ResponseEntity<?> replyToPost(@PathVariable String parent_comment_id, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.createReply(parent_comment_id, requestDto, userDetails);
    }

    @PostMapping("/reply/update/{parent_comment_id}")
    public ResponseEntity<?> updateReply(@PathVariable String parent_comment_id, @RequestBody CommentRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.editReply(parent_comment_id, requestDto, userDetails);
    }

    @DeleteMapping("/reply/{parent_comment_id}")
    public ResponseEntity<?> deleReply(@PathVariable String parent_comment_id, @AuthenticationPrincipal UserDetails userDetails){
        return this.commentService.deleteReply(parent_comment_id, userDetails);
    }
}
