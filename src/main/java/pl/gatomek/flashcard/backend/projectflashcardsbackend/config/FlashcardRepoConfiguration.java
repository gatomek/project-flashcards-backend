package pl.gatomek.flashcard.backend.projectflashcardsbackend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.feign.GitHubPagesClient;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.repo.FileSystemFlashcardRepository;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.repo.FlashcardRepo;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.repo.GitHubFlashcardRepository;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.repo.GitHubPagesRepository;

@Configuration
public class FlashcardRepoConfiguration {
    @Profile("fsrepo")
    @Bean
    public FlashcardRepo fileSystemFlashcardRepository(FileSystemRepoConfiguration fileSystemRepoConfiguration) {
        return new FileSystemFlashcardRepository(fileSystemRepoConfiguration);
    }

    @Profile("pages")
    @Bean
    public FlashcardRepo gitHubPagesFlashcardRepository(GitHubPagesClient gitHubPagesClient) {
        return new GitHubPagesRepository(gitHubPagesClient);
    }

    @Bean
    @ConditionalOnMissingBean(FlashcardRepo.class)
    public FlashcardRepo gitHubFlashcardRepository() {
        return new GitHubFlashcardRepository();
    }
}
