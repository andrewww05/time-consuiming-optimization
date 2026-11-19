package chnu.edu.sorcerstone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class V1Arrays implements WordCounter {

    @Override
    public String name() {
        return "v1 arrays";
    }

    @Override
    public List<WordFreq> topWords(Path file, int limit) throws IOException {
        String content = new String(Files.readAllBytes(file));
        content = content.replaceAll("[^A-Za-z ]", " ").toLowerCase(Locale.ROOT);

        String[] words = content.split(" +");

        Arrays.sort(words);

        String distinctString = " ";

        for (int i = 0; i < words.length; i++) {
            if (!distinctString.contains(words[i])) {
                distinctString += words[i] + " ";
            }
        }

        String[] distincts = distinctString.split(" ");
        int[] freq = new int[distincts.length];

        for (int i = 0; i < distincts.length; i++) {
            int count = 0;
            for (int j = 0; j < words.length; j++) {
                if (distincts[i].equals(words[j])) {
                    count++;
                }
            }
            freq[i] = count;
        }

        for (int i = 0; i < distincts.length; i++) {
            distincts[i] += " " + freq[i];
        }

        Arrays.sort(distincts, Comparator.comparing(str
                -> Integer.valueOf(str.replaceAll("[^0-9]", ""))));

        List<WordFreq> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, distincts.length); i++) {
            String[] parts = distincts[distincts.length - 1 - i].split(" ");
            result.add(new WordFreq(parts[0], Integer.parseInt(parts[1])));
        }
        return result;
    }
}
