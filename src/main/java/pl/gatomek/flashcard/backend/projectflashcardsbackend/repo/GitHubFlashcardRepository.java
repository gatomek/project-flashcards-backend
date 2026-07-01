package pl.gatomek.flashcard.backend.projectflashcardsbackend.repo;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Flashcard;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.parser.FlashcardParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class GitHubFlashcardRepository implements FlashcardRepo {
    private static final FlashcardParser PARSER = new FlashcardParser();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private List<FlashcardDeck> cachedDecks;
    private Instant timestamp;

    @Override
    public List<FlashcardDeck> getDecks() {
        List<FlashcardDeck> decks = getFromCache();
        if (decks != null) {
            return decks;
        }

        return updateCacheThenGetFromCache();
    }

    private boolean isCacheValid() {
        return cachedDecks != null
                && timestamp != null
                && Duration.between(timestamp, Instant.now()).toSeconds() < 60;
    }

    private List<FlashcardDeck> updateCacheThenGetFromCache() {
        writeLock.lock();
        try {
            List<FlashcardDeck> flashcardDecks = fromCache();
            if (flashcardDecks != null) {
                return flashcardDecks;
            }

            flashcardDecks = new ArrayList<>(100);

            log.info("GitHub data reloading");

            GitHub gitHub = GitHub.connectAnonymously();
            GHRepository repository = gitHub.getRepository("gatomek/project-flashcards-data");
            if (repository != null) {
                List<GHContent> decks = repository.getDirectoryContent("decks", "main");
                for (GHContent c : decks) {
                    FlashcardDeck flashcardDeck = new FlashcardDeck(c.getName());
                    PagedIterable<GHContent> cards = c.listDirectoryContent();
                    for (var card : cards) {
                        if (card.isFile()) {
                            try (InputStream inputStream = card.read();
                                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                                List<String> lines = reader.lines().toList();
                                flashcardDeck.add(PARSER.parse(card.getName(), lines));
                            }
                        } else if (card.isDirectory()) {
                            PagedIterable<GHContent> ghContents = card.listDirectoryContent();
                            if (ghContents != null) {
                                Flashcard parsed = null;

                                for (GHContent cx : ghContents) {
                                    if (cx.isFile() && cx.getName().endsWith(".md")) {
                                        try (InputStream inputStream = cx.read();
                                             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                                             BufferedReader reader = new BufferedReader(inputStreamReader)) {
                                            List<String> lines = reader.lines().toList();
                                            parsed = PARSER.parse(card.getName(), lines);
                                        }
                                        break;
                                    }
                                }

                                if (parsed != null) {
                                    for (GHContent cx : ghContents) {
                                        if (cx.isFile() && cx.getName().endsWith(".jpg")) {
                                            try (InputStream inputStream = cx.read()) {
                                                byte[] fileContent = inputStream.readAllBytes();
                                                String base64 = Base64.getEncoder().encodeToString(fileContent);
                                                parsed.setImg("data:image/jpg;base64," + base64);
                                            }
                                            break;
                                        }
                                    }
                                }

                                if (parsed != null) {
                                    flashcardDeck.add(parsed);
                                }
                            }
                        }
                    }
                    flashcardDecks.add(flashcardDeck);
                }
            }

            log.info("GitHub data reloaded");

            cachedDecks = flashcardDecks;
            timestamp = Instant.now();

            return fromCache();
        } catch (IOException ex) {
            log.error("IO Exception: {}", ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    private List<FlashcardDeck> fromCache() {
        if (isCacheValid()) {
            return List.copyOf(cachedDecks);
        } else {
            return null;
        }
    }

    private List<FlashcardDeck> getFromCache() {
        readLock.lock();
        try {
            return fromCache();
        } finally {
            readLock.unlock();
        }
    }
}
