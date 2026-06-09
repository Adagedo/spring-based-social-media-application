package application.controllers.post;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    @GetMapping("/{image_name}")
    public ResponseEntity<?> getImage(@PathVariable String image_name, @AuthenticationPrincipal UserDetails userDetails){

        Resource resource = new FileSystemResource("upload_dir/" + image_name);

        if (!resource.exists()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("image not found");
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.IMAGE_JPEG).body(resource);
    }
}
