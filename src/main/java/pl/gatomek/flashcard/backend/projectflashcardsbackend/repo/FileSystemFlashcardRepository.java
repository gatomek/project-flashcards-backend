package pl.gatomek.flashcard.backend.projectflashcardsbackend.repo;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.config.FileSystemRepoConfiguration;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.parser.FlashcardParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Profile("fsrepo")
@RequiredArgsConstructor
@Repository
public class FileSystemFlashcardRepository implements FlashcardRepo {
    private static final Logger LOGGER = Logger.getLogger(FileSystemFlashcardRepository.class.getName());
    private final FileSystemRepoConfiguration fileSystemRepoConfiguration;
    private static final FlashcardParser PARSER = new FlashcardParser();

    @Override
    public List<FlashcardDeck> getDecks() {
        List<FlashcardDeck> decks = new ArrayList<>();

        File repo = new File(fileSystemRepoConfiguration.getFolder());
        if (repo.exists() && repo.isDirectory()) {

            File[] files = repo.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        FlashcardDeck deck = new FlashcardDeck(f.getName());

                        File[] cards = f.listFiles();
                        if (cards != null) {
                            for (File fc : cards) {
                                try {
                                    List<String> lines = Files.readAllLines(fc.toPath());
                                    deck.add(PARSER.parse(fc.getName(), lines));
                                } catch (IOException ex) {
                                    LOGGER.severe(ex.getMessage());
                                }
                            }
                        }

                        decks.add(deck);
                    }
                }
            }
        }

        return decks;
    }
}
