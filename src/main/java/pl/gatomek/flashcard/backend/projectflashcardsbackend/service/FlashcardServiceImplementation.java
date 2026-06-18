package pl.gatomek.flashcard.backend.projectflashcardsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.repo.FlashcardRepo;

import java.util.List;

@RequiredArgsConstructor
@Service
class FlashcardServiceImplementation implements FlashcardService {
    private final FlashcardRepo flashcardRepo;

    public List<FlashcardDeck> getFlashcardDecks() {
        return flashcardRepo.getDecks();
    }
}
