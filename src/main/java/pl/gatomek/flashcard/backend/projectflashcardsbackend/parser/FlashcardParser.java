package pl.gatomek.flashcard.backend.projectflashcardsbackend.parser;

import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Flashcard;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Option;
import pl.gatomek.flashcard.backend.projectflashcardsbackend.dto.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlashcardParser {
    private static final String FRONT_MATTER_SEPARATOR = "---";
    private static final String QUERY_HEADER = "# query";
    private static final String ANSWER_HEADER = "# answer";
    private static final String INFO_HEADER = "# info";
    private static final String OPTION_HEADER_BEGINNING = "## ";
    private static final String TYPE = "type";
    private static final String UUID = "uuid";
    private static final String DELIMITER = "\n";

    public Flashcard parse(String flashcardName, List<String> lines) {
        Flashcard card = new Flashcard(flashcardName);

        List<String> props = new ArrayList<>();
        List<String> query = new ArrayList<>();
        List<String> answer = new ArrayList<>();
        List<String> info = new ArrayList<>();

        SectionEnum section = SectionEnum.NONE;

        for (int p = 0; p < lines.size(); p++) {
            String line = lines.get(p);

            if (FRONT_MATTER_SEPARATOR.equals(line) && p == 0) {
                section = SectionEnum.PROPS;
                continue;
            }

            if (FRONT_MATTER_SEPARATOR.equals(line) && section == SectionEnum.PROPS) {
                section = SectionEnum.NONE;
                continue;
            }

            if (QUERY_HEADER.equals(line)) {
                section = SectionEnum.QUERY;
                continue;
            }

            if (ANSWER_HEADER.equals(line)) {
                section = SectionEnum.ANSWER;
                continue;
            }

            if (INFO_HEADER.equals(line)) {
                section = SectionEnum.INFO;
                continue;
            }

            switch (section) {
                case PROPS -> props.add(line);
                case QUERY -> query.add(line);
                case ANSWER -> answer.add(line);
                case INFO -> info.add(line);
                case NONE -> {
                }
            }
        }

        if (!props.isEmpty()) {
            Map<String, String> map = loadProps(props);
            String type = map.get(TYPE);
            if (type != null) {
                card.setType(type);
            }
            String uuid = map.get(UUID);
            if (uuid != null) {
                card.setUuid(uuid);
            }
        }

        if (!query.isEmpty()) {
            card.setQuery(scan(query));
        }

        if (!answer.isEmpty()) {
            card.setAnswer(scan(answer));
        }

        if (!info.isEmpty()) {
            card.setInfo(String.join(DELIMITER, info));
        }

        return card;
    }

    private Map<String, String> loadProps(List<String> props) {
        Map<String, String> map = HashMap.newHashMap(props.size());

        for (String prop : props) {
            String trimmed = prop.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] elems = trimmed.split(":");
            if (elems.length != 2) {
                continue;
            }

            String name = elems[0].trim();
            if (name.isEmpty()) {
                continue;
            }

            String value = elems[1].trim();
            map.put(name, value);
        }

        return map;
    }

    private Page scan(List<String> lines) {
        List<Option> options = new ArrayList<>(10);
        List<String> contents = new ArrayList<>(10);

        ParserOption parserOption = null;

        for (String line : lines) {
            if (line.startsWith(OPTION_HEADER_BEGINNING)) {
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

        String content = String.join(DELIMITER, contents);
        return new Page(content, options);
    }

    enum SectionEnum {
        NONE,
        PROPS,
        QUERY,
        ANSWER,
        INFO
    }
}
