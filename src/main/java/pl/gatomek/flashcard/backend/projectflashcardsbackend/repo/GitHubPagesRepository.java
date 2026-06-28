package pl.gatomek.flashcard.backend.projectflashcardsbackend.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Flashcard;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.feign.Content;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.feign.GitHubPagesClient;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.parser.FlashcardParser;

import java.util.*;

@RequiredArgsConstructor
@Profile("pages")
@Repository
public class GitHubPagesRepository implements FlashcardRepo {

    private static final FlashcardParser PARSER = new FlashcardParser();
    private final GitHubPagesClient gitHubPagesClient;

    @Override
    public List<FlashcardDeck> getDecks() {
        List<FlashcardDeck> decks = new ArrayList<>();

        List<Content> contents = gitHubPagesClient.getManifest();
        for (Content c : contents) {
            String title = c.getName();

            List<Flashcard> flashcards = new ArrayList<>();
            for (Content card : c.getContent()) {
                if ("file".equals(card.getType())) {
                    loadFlashcardContentFromFile(title, card).ifPresent(flashcards::add);
                } else if ("dir".equals(card.getType())) {
                    loadFlashcardContentFromFolder(title, card).ifPresent(flashcards::add);
                }
            }

            decks.add(new FlashcardDeck(title, flashcards));
        }

        return decks;
    }

    private Optional<Flashcard> loadFlashcardContentFromFolder(String title, Content card) {
        Optional<String> mdFile = findFile(card, ".md");
        if (mdFile.isPresent()) {
            String content = gitHubPagesClient.getFlashcardFromFolder(title, card.getName(), mdFile.get());
            String[] lines = content.split("\n");

            Flashcard parsed = PARSER.parse(card.getName(), Arrays.stream(lines).toList());

            Optional<String> jpgFile = findFile(card, ".jpg");
            if( jpgFile.isPresent()) {
                byte[] img = gitHubPagesClient.getImageFromFolder(title, card.getName(), jpgFile.get());
                String base64 = Base64.getEncoder().encodeToString(img);
                parsed.getQuery().setImg("data:image/jpg;base64," + base64);
            }

            return Optional.of(parsed);
        }

        return Optional.empty();
    }

    private Optional<String> findFile(Content card, String endsWithFilter) {
        return card.getContent().stream()
                .map(Content::getName)
                .filter( c -> c.endsWith(endsWithFilter))
                .findFirst();
    }

    private Optional<Flashcard> loadFlashcardContentFromFile(String title, Content card) {
        String content = gitHubPagesClient.getFlashcardFromFile(title, card.getName());
        String[] lines = content.split("\n");
        Flashcard parsed = PARSER.parse(card.getName(), Arrays.stream(lines).toList());
        return Optional.of(parsed);
    }
}
