package pl.gatomek.flascard.backend.projectflashcardsbackend.service;

import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.FlashcardDeck;

import java.util.List;

public interface FlashcardService {
    List<FlashcardDeck> getFlashcardDecks();
}
