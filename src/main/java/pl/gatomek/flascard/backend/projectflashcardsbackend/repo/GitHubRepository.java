package pl.gatomek.flascard.backend.projectflashcardsbackend.repo;

import org.kohsuke.github.*;
import org.springframework.stereotype.Repository;
import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flascard.backend.projectflashcardsbackend.parser.FlashcardParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
class GitHubRepository implements Repo {
    private static final FlashcardParser PARSER = new FlashcardParser();

    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private List<FlashcardDeck> cachedDecks;
    private Instant timestamp;

    @Override
    public List<FlashcardDeck> getDecks() {
        List<FlashcardDeck> decks = getFromCache();
        if( decks != null) {
            return decks;
        }

        updateCache();

        return getFromCache();
    }

    private boolean isCacheValid() {
        return cachedDecks != null && timestamp != null && Duration.between(timestamp, Instant.now()).toSeconds() < 60;
    }

    private void updateCache() {
        List<FlashcardDeck> flashcardDecks = new ArrayList<>();

        try {
            GitHub gitHub = GitHub.connectAnonymously();
            GHRepository repository = gitHub.getRepository("gatomek/project-flashcard-data");
            if (repository != null) {
                List<GHContent> decks = repository.getDirectoryContent("decks", "main");
                for (GHContent c : decks) {
                    FlashcardDeck flashcardDeck = new FlashcardDeck(c.getName());
                    PagedIterable<GHContent> cards = c.listDirectoryContent();
                    for (var card : cards) {
                        InputStream inputStream = card.read();
                        List<String> lines = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().toList();
                        flashcardDeck.add(PARSER.parse(card.getName(), lines));
                    }
                    flashcardDecks.add(flashcardDeck);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        writeLock.lock();
        try {
            cachedDecks = flashcardDecks;
            timestamp = Instant.now();
        }
        finally {
            writeLock.unlock();
        }
    }

    private List<FlashcardDeck> getFromCache() {
        readLock.lock();
        try {
            if (isCacheValid()) {
                return cachedDecks;
            }
            else {
                return null;
            }
        }
        finally {
            readLock.unlock();
        }
    }
}
