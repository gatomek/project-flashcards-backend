package pl.gatomek.flascard.backend.projectflashcardsbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FlashcardDeck {
    private String title;
    private List<Flashcard> cards = new ArrayList<>();

    public FlashcardDeck(String title) {
        setTitle(title);
    }

    public void add(Flashcard card) {
        cards.add(card);
    }
}
