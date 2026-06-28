package pl.gatomek.flashcard.backend.projectflashcardsbackend.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "pages", url = "https://gatomek.github.io/")
public interface GitHubPagesClient {

    @GetMapping(value = "/project-flashcard-data/manifest.json")
    List<Content> getManifest();

    @GetMapping(value = "/project-flashcard-data/{title}/{name}")
    String getFlashcardFromFile(@PathVariable String title, @PathVariable String name);

    @GetMapping(value = "/project-flashcard-data/{title}/{name}/{subName}")
    String getFlashcardFromFolder(@PathVariable String title, @PathVariable String name, @PathVariable String subName);

    @GetMapping(value = "/project-flashcard-data/{title}/{name}/{subName}")
    byte[] getImageFromFolder(@PathVariable String title, @PathVariable String name, @PathVariable String subName);
}
