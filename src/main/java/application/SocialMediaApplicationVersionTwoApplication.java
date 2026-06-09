package application;

import application.service.storage.ImageStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImageStorageProperties.class)
public class SocialMediaApplicationVersionTwoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaApplicationVersionTwoApplication.class, args);
	}

}
