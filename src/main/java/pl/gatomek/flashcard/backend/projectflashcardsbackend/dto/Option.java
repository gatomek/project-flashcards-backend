package pl.gatomek.flashcard.backend.projectflashcardsbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Option {
    private String id;
    private List<String> content = new ArrayList<>();

    public Option(String id) {
        setId(id);
    }
}
