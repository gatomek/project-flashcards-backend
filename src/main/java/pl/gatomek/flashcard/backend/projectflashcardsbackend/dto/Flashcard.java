package pl.gatomek.flashcard.backend.projectflashcardsbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Flashcard {
    private String name;
    private String type;
    private String uuid;
    private Content query;
    private Content answer;

    public Flashcard(String name) {
        setName(name);
    }
}

