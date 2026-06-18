package pl.gatomek.flashcard.backend.projectflashcardsbackend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class Content {
    private final String main;
    private final List<Option> options;
}
