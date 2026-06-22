package pl.gatomek.flashcard.backend.projectflashcardsbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("fsrepo")
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "repo")
public class FileSystemRepoConfiguration {
    private String folder;
}
