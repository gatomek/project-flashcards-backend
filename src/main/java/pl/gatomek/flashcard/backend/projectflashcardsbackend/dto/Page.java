package pl.gatomek.flashcard.backend.projectflashcardsbackend.dto;

import java.util.List;

public record Page(String main, List<Option> options) {
}
