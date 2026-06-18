package pl.gatomek.flashcard.backend.projectflashcardsbackend.service;

import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;

import java.util.List;

public interface FlashcardService {
    List<FlashcardDeck> getFlashcardDecks();
}
