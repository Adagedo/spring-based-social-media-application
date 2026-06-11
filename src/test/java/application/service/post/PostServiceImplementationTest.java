package application.service.post;

import application.dto.requestDto.PostRequestDto;

import application.entity.post.PostEntity;
import application.exceptions.custom_exception.CustomImageStorageException;
import application.repository.post.PostRepository;
import application.repository.user.UserRepository;
import application.service.storage.ImageStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PostServiceImplementationTest {

    @InjectMocks
    private PostServiceImplementation postServiceImplementation;

    @Mock
    private  PostRepository repository;

    @Mock
    private  ImageStorage imageStorage;

    @Mock
    private  UserRepository userRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowBadRequestException(){

        UserDetails mockUser = Mockito.mock(UserDetails.class);
        when(mockUser.getUsername()).thenReturn("service_admin");

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "text/plain",
                new byte[0]
        );

        PostRequestDto requestDto = new PostRequestDto(
                "my title",
                "my content",
                emptyFile
        );

        CustomImageStorageException exception = assertThrows(
                CustomImageStorageException.class, () -> postServiceImplementation.createPost(requestDto, mockUser)
        );

        assertEquals("error uploading file", exception.getMessage());
    }

    @Test
    void shouldReturnFORBIDDENForInvalidFileFormat() {


        UserDetails mockUser = Mockito.mock(UserDetails.class);
        when(mockUser.getUsername()).thenReturn("service_admin");
        String docxType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.docx",
                docxType,
                "fake image data".getBytes()
        );
        PostRequestDto requestDto = new PostRequestDto(
                "my title",
                "my content",
                file
        );

        ResponseEntity<?> result = postServiceImplementation.createPost(requestDto, mockUser);
        assertNotNull(result);
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("Image format not supported, upload a .jpg file or a .png file", result.getBody());
    }

    @Test
    void shouldCreatePost() {

        UserDetails mockUser = Mockito.mock(UserDetails.class);
        when(mockUser.getUsername()).thenReturn("service_admin");
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_image.jpg", // Must end with .jpg or .png
                "image/jpeg",
                "test data".getBytes()
        );
        PostRequestDto requestDto = new PostRequestDto(
                "my title",
                "my content",
                mockFile
        );

        ResponseEntity<?> result = postServiceImplementation.createPost(requestDto, mockUser);
        assertNotNull(result);
        assertEquals("post created successfully!", result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void shouldGetAllPost() {
        UserDetails mockUser = Mockito.mock(UserDetails.class);
        when(mockUser.getUsername()).thenReturn("service_admin");
        ResponseEntity <?> response = postServiceImplementation.getAllPost(mockUser);
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundIfPostByIdDoesNotExist() {
        UserDetails mockUser = Mockito.mock(UserDetails.class);
        when(mockUser.getUsername()).thenReturn("service_admin");
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_image.jpg", // Must end with .jpg or .png
                "image/jpeg",
                "test data".getBytes()
        );
        String post_id = "69ed82e3-fbf5-40b3-b020-fc7f51184a8c";
        PostEntity post = PostEntity.builder()
                .id(UUID.fromString(post_id))
                .title("title")
                .content("content")
                .image_path(mockFile.getOriginalFilename())
                .build();
        ResponseEntity<?> result = postServiceImplementation.getPostById(String.valueOf(post.getId()), mockUser);
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("post not found", result.getBody());
    }

    @Test
    void deletePost() {
    }
}