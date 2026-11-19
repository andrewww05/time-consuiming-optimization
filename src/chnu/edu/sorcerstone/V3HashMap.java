package chnu.edu.sorcerstone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class V3HashMap implements WordCounter {

    @Override
    public String name() {
        return "v3 hashmap";
    }

    @Override
    public List<WordFreq> topWords(Path file, int limit) throws IOException {
        String content = new String(Files.readAllBytes(file));
        String[] words = content.replaceAll("[^A-Za-z ]", " ")
                .toLowerCase(Locale.ROOT)
                .split(" +");

        Map<String, Integer> counts = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                counts.merge(word, 1, Integer::sum);
            }
        }

        return counts.entrySet().stream()
                .map(e -> new WordFreq(e.getKey(), e.getValue()))
                .sorted(WordFreq.ORDER)
                .limit(limit)
                .toList();
    }
}
