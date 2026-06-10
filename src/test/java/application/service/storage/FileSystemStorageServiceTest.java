package application.service.storage;

import application.exceptions.custom_exception.CustomImageStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class FileSystemStorageServiceTest {

    private FileSystemStorageService fileSystemStorageService;

    @Mock
    private ImageStorageProperties imageStorageProperties;

    @Mock
    private Path rootLocation;

    @TempDir
    private Path sharedTempDir;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        Mockito.when(imageStorageProperties.getLocation()).thenReturn("test_upload_location");

        fileSystemStorageService = new FileSystemStorageService(imageStorageProperties);
    }

    @Test
    void shouldThrowCustomImageStorageExceptionWhenFileIsEmpty() {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.txt",
                "text/plain",
                new byte[0]
        );        CustomImageStorageException exception = assertThrows(
                CustomImageStorageException.class, ()-> fileSystemStorageService.store(emptyFile)
        );
        assertEquals("failed to store empty file", exception.getMessage());
    }

    @Test
    void shouldCopyMultipartFileToDestination() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "fake image data".getBytes()
        );

        Path destinationFile = sharedTempDir.resolve("uploads/test-image.jpg");
        try (var inputStream = file.getInputStream()) {
            Files.createDirectories(destinationFile.getParent());
            Files.copy(inputStream, destinationFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileSystemStorageService.store(file);
        assertThat(Files.exists(destinationFile)).isTrue();
        assertThat(Files.readString(destinationFile)).isEqualTo("fake image data");
    }


    @Test
    void shouldThrowCustomImageStorageExceptionWhenFileNameIsNull() {
        CustomImageStorageException exception = assertThrows(
                CustomImageStorageException.class, () -> fileSystemStorageService.load("")
        );
        assertEquals("file name cannot be null", exception.getMessage());
    }

    @Test
    void shouldLoadFileContent(){

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "fake image data".getBytes()
        );

        Path result = fileSystemStorageService.load(file.getOriginalFilename());
        assertNotNull(result);
    }
}