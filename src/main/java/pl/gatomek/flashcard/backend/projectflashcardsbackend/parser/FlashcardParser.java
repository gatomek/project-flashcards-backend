package pl.gatomek.flashcard.backend.projectflashcardsbackend.parser;

import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Flashcard;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Option;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FlashcardParser {

    private static final String SEPARATOR = "---";

    public Flashcard parse(String flashcardName, List<String> lines) {
        Flashcard card = new Flashcard(flashcardName);

        boolean isProp = false;
        boolean isQuery = false;
        boolean isAnswer = false;
        List<String> query = new ArrayList<>();
        List<String> answer = new ArrayList<>();

        int c = 0;
        for (String line : lines) {
            if (SEPARATOR.equals(line) && c == 0) {
                isProp = true;
                c++;
                continue;
            }

            if (SEPARATOR.equals(line) && isProp) {
                isProp = false;
                c++;
                continue;
            }

            if (isProp) {
                if (line.isEmpty()) {
                    c++;
                    continue;
                }

                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    c++;
                    continue;
                }

                String[] elems = trimmed.split(":");
                if (elems.length != 2) {
                    c++;
                    continue;
                }

                String name = elems[0].trim();
                if (name.isEmpty()) {
                    c++;
                    continue;
                }

                String value = elems[1].trim();

                if (name.equals("type")) {
                    card.setType(value);
                    c++;
                    continue;
                }

                if (name.equals("uuid")) {
                    card.setUuid(value);
                    c++;
                    continue;
                }
            }

            if (line.equals("# query")) {
                isQuery = true;
                isAnswer = false;
                c++;
                continue;
            }

            if (isQuery) {
                if (line.equals("# answer")) {
                    isQuery = false;
                    isAnswer = true;
                    c++;
                    continue;
                }

                query.add(line);
                c++;
                continue;
            }

            if (isAnswer) {
                answer.add(line);
                c++;
                continue;
            }
        }

        if (!query.isEmpty()) {
            card.setQuery(scan(query));
        }

        if (!answer.isEmpty()) {
            card.setAnswer(scan(answer));
        }

        return card;
    }

    private Page scan(List<String> lines) {
        List<Option> options = new ArrayList<>(10);
        List<String> contents = new ArrayList<>(10);

        ParserOption parserOption = null;

        for (String line : lines) {
            if (line.startsWith("## ")) {
                if (parserOption != null) {
                    options.add(parserOption.toOption());
                }

                parserOption = new ParserOption(line.substring(3));
                continue;
            }

            if (parserOption != null) {
                parserOption.getContents().add(line);
                continue;
            }

            contents.add(line);
        }

        if (parserOption != null) {
            options.add(parserOption.toOption());
        }

        String content = String.join( "\n", contents);
        return new Page(content, options);
    }
}
