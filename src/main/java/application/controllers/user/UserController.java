package application.controllers.user;

import application.entity.user.UserEntity;
import application.repository.user.UserRepository;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// test controller thi will be removed in future updates
@RestController
@RequestMapping("/api/v1/users/test")
@AllArgsConstructor
public class UserController {

    private final UserRepository repository;


    // this api is a test api
    @GetMapping("/{id}")
    public EntityModel<UserEntity> findUser(@PathVariable String id){
        Optional<UserEntity> optionalUser = repository.findById(UUID.fromString(id));
        if(optionalUser.isEmpty()) throw new UsernameNotFoundException("user not found");
        UserEntity user = optionalUser.get();
        EntityModel<UserEntity> entityModel = EntityModel.of(user);
        WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).getAllUsers());
        entityModel.add(link.withRel("all-users"));
        return entityModel;
    }
    // test api
    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        List<UserEntity> users = repository.findAll();
        return ResponseEntity.of(Optional.of(users));
    }
}
