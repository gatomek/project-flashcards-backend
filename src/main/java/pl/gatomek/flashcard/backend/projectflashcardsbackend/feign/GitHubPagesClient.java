package pl.gatomek.flashcard.backend.projectflashcardsbackend.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Profile("pages")
@FeignClient(value = "pages", url = "${resource.url}")
public interface GitHubPagesClient {

    @GetMapping(value = "/manifest.json")
    List<Content> getManifest();

    @GetMapping(value = "/{title}/{name}")
    String getFlashcardFromFile(@PathVariable String title, @PathVariable String name);

    @GetMapping(value = "/{title}/{name}/{subName}")
    String getFlashcardFromFolder(@PathVariable String title, @PathVariable String name, @PathVariable String subName);

    @GetMapping(value = "/{title}/{name}/{subName}")
    byte[] getImageFromFolder(@PathVariable String title, @PathVariable String name, @PathVariable String subName);
}
