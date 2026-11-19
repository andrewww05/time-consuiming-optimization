package chnu.edu.sorcerstone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class V2Streams implements WordCounter {

    @Override
    public String name() {
        return "v2 streams";
    }

    @Override
    public List<WordFreq> topWords(Path file, int limit) throws IOException {
        String content = new String(Files.readAllBytes(file));

        String contentCleaned = content.replaceAll("[^A-Za-z ]", " ").toLowerCase(Locale.ROOT);

        String[] words = contentCleaned.split(" +");
        List<String> list = new ArrayList<>(Arrays.asList(words));
        List<WordFreq> wordsList = new ArrayList<>();
        List<String> unique = list.stream().distinct().collect(Collectors.toList());
        unique.stream()
                .forEach(word -> {
                            int freq = (int) list.stream()
                                    .filter(item -> item.equals(word))
                                    .count();
                            wordsList.add(new WordFreq(word, freq));
                        }
                );

        return wordsList.stream()
                .sorted(Comparator.comparingInt(WordFreq::freq).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
