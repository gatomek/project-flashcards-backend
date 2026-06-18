package pl.gatomek.flashcard.backend.projectflashcardsbackend.repo;

import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;

import java.util.List;

public interface FlashcardRepo {
    List<FlashcardDeck> getDecks();
}
