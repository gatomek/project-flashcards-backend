package pl.gatomek.flashcard.backend.projectflashcardsbackend.feign;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Content {
    private String type;
    private String name;
    private List<Content> content;
}
