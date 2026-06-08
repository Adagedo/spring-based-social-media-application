package application.service.post;

import application.exceptions.custom_exception.CustomImageStorageException;
import application.exceptions.custom_exception.CustomImageStorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.FileSystemUtils;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements ImageStorage {

    private final Path rootLocation;

    public FileSystemStorageService(ImageStorageProperties imageStorageProperties) {
        if (imageStorageProperties.getLocation().trim().isEmpty()){
            throw new CustomImageStorageException("file upload location cannot be empty");
        }
        this.rootLocation = Paths.get(imageStorageProperties.getLocation());
    }

    @Override
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new CustomImageStorageException("Could not initialize storage",e);
        }
    }

    @Override
    public void store(MultipartFile file) {

        try{
            if (file.isEmpty()){
                throw new CustomImageStorageException("failed to store empty file");
            }
            Path absoluteRootLocation = this.rootLocation.toAbsolutePath().normalize();
            Path destinationFile = absoluteRootLocation
                    .resolve(Paths.get(Objects.requireNonNull(file.getOriginalFilename())))
                    .normalize();

            if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())){
                throw new CustomImageStorageException("cannot store file outside current directory");
            }
            try(InputStream inputStream = file.getInputStream()){
                java.nio.file.Files.createDirectories(destinationFile.getParent());
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (IOException e) {
            throw new CustomImageStorageException("Failed to store file ",e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.rootLocation, 1).filter(
                    path -> !path.equals(this.rootLocation)
            ).map(this.rootLocation::relativize);
        }catch (IOException e) {
            throw new CustomImageStorageException("failed to read files", e);
        }
    }



    @Override
    public Path load(String filename) {
        if (filename.isEmpty()){
            throw new CustomImageStorageException("file name cannot be null");
        }
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadResource(String filename) {

        try{
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isFile()){
                return resource;
            }
            else {
                throw new CustomImageStorageFileNotFoundException("could not load file" + filename);
            }
        } catch (MalformedURLException e) {
            throw new CustomImageStorageFileNotFoundException("could not load image file " + filename);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}
