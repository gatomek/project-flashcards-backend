package pl.gatomek.flashcard.backend.projectflashcardsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ProjectFlashcardsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectFlashcardsBackendApplication.class, args);
    }

}
