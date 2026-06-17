package pl.gatomek.flascard.backend.projectflashcardsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flascard.backend.projectflashcardsbackend.repo.Repo;

import java.util.List;

@RequiredArgsConstructor
@Service
class FlashcardServiceImplementation implements FlashcardService {
    private final Repo repo;

    public List<FlashcardDeck> getFlashcardDecks() {
        return repo.getDecks();
    }
}
