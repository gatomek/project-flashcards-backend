package pl.gatomek.flashcard.backend.projectflashcardsbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FlashcardDeck {
    private final String title;
    private List<Flashcard> cards = new ArrayList<>();

    public FlashcardDeck(String title) {
        this.title = title;
    }

    public FlashcardDeck(String title, List<Flashcard> cards) {
        this.title = title;
        this.cards = cards;
    }

    public void add(Flashcard card) {
        cards.add(card);
    }
}
