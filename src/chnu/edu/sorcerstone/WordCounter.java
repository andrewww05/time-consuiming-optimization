package chnu.edu.sorcerstone;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface WordCounter {

    String name();

    List<WordFreq> topWords(Path file, int limit) throws IOException;
}
