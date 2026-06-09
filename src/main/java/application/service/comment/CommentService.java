package application.service.comment;

import application.dto.requestDto.CommentRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CommentService {

    ResponseEntity<?> createComment(String post_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> editComment(String comment_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> getAllCommentsAssociatedToAPost(String post_id, UserDetails userDetails);

    ResponseEntity<?> deleteComment(String comment_id, UserDetails userDetails);

    ResponseEntity<?> createReply(String parent_comment_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> editReply(String parent_comment_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> getAllRepliesAssociatedToAComment(String parent_comment_id, UserDetails userDetails);

    ResponseEntity<?> deleteReply(String parent_comment_id, UserDetails userDetails);
}
