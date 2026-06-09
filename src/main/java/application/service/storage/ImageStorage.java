package application.service.storage;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface ImageStorage {

    void init();
    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadResource(String filename);

    void deleteAll();
}
