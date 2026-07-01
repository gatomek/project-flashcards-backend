package pl.gatomek.flashcard.backend.projectflashcardsbackend.parser;

import lombok.Getter;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Option;

import java.util.ArrayList;
import java.util.List;

@Getter
class ParserOption {
    private final String id;
    private final List<String> contents = new ArrayList<>();

    public ParserOption(String id) {
        this.id = id;
    }

    public Option toOption() {
        String content = String.join("\n", contents);
        return new Option(id, content);
    }
}
