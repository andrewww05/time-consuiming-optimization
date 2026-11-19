package chnu.edu.sorcerstone;

import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        WordCounter counter = Versions.byKey(args.length > 0 ? args[0] : "v4");
        Path file = Path.of(args.length > 1 ? args[1] : "harry.txt");

        long start = System.nanoTime();
        List<WordFreq> top = counter.topWords(file, 30);
        long finish = System.nanoTime();

        top.forEach(System.out::println);
        System.out.println("------");
        System.out.printf("%s: %.3f ms%n", counter.name(), (finish - start) / 1_000_000.0);
    }
}
