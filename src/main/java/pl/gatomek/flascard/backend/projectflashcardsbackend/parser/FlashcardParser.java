package pl.gatomek.flascard.backend.projectflashcardsbackend.parser;

import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.Content;
import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.Flashcard;
import pl.gatomek.flascard.backend.projectflashcardsbackend.dto.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FlashcardParser {
    public Flashcard parse(String flashcardName, List<String> lines) {
        Flashcard card = new Flashcard(flashcardName);

        boolean isProp = false;
        boolean isQuery = false;
        boolean isAnswer = false;
        List<String> query = new ArrayList<>();
        List<String> answer = new ArrayList<>();

        int c = 0;
        for (String line : lines) {
            if (line.equals("---") && c == 0) {
                isProp = true;
                c++;
                continue;
            }

            if (line.equals("---") && isProp) {
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
            List<String> content = new ArrayList<>();
            List<Option> options = new ArrayList<>();
            scanQuery(query, content, options);

            StringJoiner sj = new StringJoiner("\n");
            for (String s : content) {
                sj.add(s);
            }
            card.setQuery(new Content(sj.toString(), options));
        }

        if (!answer.isEmpty()) {
            List<String> content = new ArrayList<>();
            List<Option> options = new ArrayList<>();
            scanQuery(answer, content, options);

            StringJoiner sj = new StringJoiner("\n");
            for (String s : content) {
                sj.add(s);
            }
            card.setAnswer(new Content(sj.toString(), options));
        }

        return card;
    }

    private void scanQuery(List<String> query, List<String> content, List<Option> options) {

        Option option = null;

        for (String line : query) {

            if (line.startsWith("##")) {
                if (option != null) {
                    options.add(option);
                }

                option = new Option(line.substring(3));
                continue;
            }

            if (option != null) {
                if (!line.isEmpty()) {
                    option.getContent().add(line);
                }
                continue;
            }

            content.add(line);
        }

        if (option != null) {
            options.add(option);
        }
    }
}
