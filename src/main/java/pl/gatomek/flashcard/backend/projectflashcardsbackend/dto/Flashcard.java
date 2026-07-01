package pl.gatomek.flashcard.backend.projectflashcardsbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Flashcard {
    private final String name;
    private String type;
    private String uuid;
    private String img;
    private Page query;
    private Page answer;

    public Flashcard(String name) {
        this.name = name;
    }
}

