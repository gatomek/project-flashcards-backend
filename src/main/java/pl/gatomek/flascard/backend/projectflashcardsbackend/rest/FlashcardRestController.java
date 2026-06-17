package pl.gatomek.flascard.backend.projectflashcardsbackend.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.FlashcardDeck;
import pl.gatomek.flascard.backend.projectflashcardsbackend.service.FlashcardService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/decks")
public class FlashcardRestController {

    private final FlashcardService flashcardService;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<FlashcardDeck>> getFlashcardDecks() {
        return ResponseEntity.ok(flashcardService.getFlashcardDecks());
    }
}
