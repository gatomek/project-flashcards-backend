package pl.gatomek.flashcard.backend.projectflashcardsbackend.repo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.config.FileSystemRepoConfiguration;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Content;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Flashcard;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.parser.FlashcardParser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Profile("fsrepo")
@RequiredArgsConstructor
@Repository
public class FileSystemFlashcardRepository implements FlashcardRepo {
    private static final FlashcardParser PARSER = new FlashcardParser();
    private final FileSystemRepoConfiguration fileSystemRepoConfiguration;
    private final FilenameFilter jpgFilter = (f, name) -> name.endsWith(".jpg");
    private final FilenameFilter mdFilter = (f, name) -> name.endsWith(".md");

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
                                    if (fc.isFile()) {
                                        List<String> lines = Files.readAllLines(fc.toPath(), StandardCharsets.UTF_8);
                                        deck.add(PARSER.parse(fc.getName(), lines));
                                    } else if (fc.isDirectory()) {
                                        Flashcard parsed = null;

                                        File[] mds = fc.listFiles(mdFilter);
                                        if (mds != null && mds.length > 0) {
                                            File md = mds[0];
                                            List<String> lines = Files.readAllLines(md.toPath(), StandardCharsets.UTF_8);
                                            parsed = PARSER.parse(fc.getName(), lines);

                                        }

                                        if (parsed != null) {
                                            Content query = parsed.getQuery();
                                            if (query != null) {
                                                File[] jpgs = fc.listFiles(jpgFilter);
                                                if (jpgs != null && jpgs.length > 0) {
                                                    File jpg = jpgs[0];
                                                    byte[] fileContent = FileUtils.readFileToByteArray(jpg);
                                                    String base64 = Base64.getEncoder().encodeToString(fileContent);
                                                    query.setImg("data:image/jpg;base64," + base64);
                                                }
                                            }
                                        }

                                        if (parsed != null) {
                                            deck.add(parsed);
                                        }

                                    }
                                } catch (IOException ex) {
                                    log.error("Failed to read flashcard file: {}", fc.getAbsolutePath(), ex);
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
