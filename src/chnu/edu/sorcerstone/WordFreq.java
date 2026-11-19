package chnu.edu.sorcerstone;

import java.util.Comparator;

public record WordFreq(String word, int freq) {

    public static final Comparator<WordFreq> ORDER =
            Comparator.comparingInt(WordFreq::freq).reversed().thenComparing(WordFreq::word);

    @Override
    public String toString() {
        return word + " " + freq;
    }
}
