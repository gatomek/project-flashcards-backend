package pl.gatomek.flascard.backend.projectflashcardsbackend.repo;

import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.FlashcardDeck;

import java.util.List;

public interface Repo {
    List<FlashcardDeck> getDecks();
}
