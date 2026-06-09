package application.service.comment;

import application.dto.requestDto.CommentRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface CommentService {

    ResponseEntity<?> createComment(String user_id, String post_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> editComment(String user_id, String post_id, String comment_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> getAllComment(UserDetails userDetails);

    ResponseEntity<?> getAllCommentsAssociatedToAPost(String post_id, UserDetails userDetails);

    ResponseEntity<?> createReply(String user_id, String parent_comment_id, CommentRequestDto requestDto, UserDetails userDetails);

    ResponseEntity<?> editReply(String user_id, String parent_comment_id, CommentRequestDto requestDto);

    ResponseEntity<?> getAllRepliesAssociatedToAComment(String parent_comment_id, UserDetails userDetails);

    ResponseEntity<?> deleteReply(String user_id, String parent_id, UserDetails userDetails);
}
