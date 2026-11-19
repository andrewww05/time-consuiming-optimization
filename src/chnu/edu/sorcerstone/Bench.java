package chnu.edu.sorcerstone;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bench {

    private static final int TOP = 30;
    private static final long WARMUP_NANOS = 3_000_000_000L;
    private static final long MEASURE_NANOS = 5_000_000_000L;
    private static final int MIN_RUNS = 5;

    private static int blackhole;

    public static void main(String[] args) throws Exception {
        Path file = Path.of(args.length > 0 ? args[0] : "harry.txt");

        check(file);

        System.out.printf("%-12s %6s %12s %12s %12s%n", "version", "runs", "min ms", "median ms", "avg ms");
        for (WordCounter counter : Versions.ALL) {
            double[] ms = measure(counter, file);
            Arrays.sort(ms);
            System.out.printf("%-12s %6d %12.3f %12.3f %12.3f%n",
                    counter.name(), ms.length, ms[0], ms[ms.length / 2], Arrays.stream(ms).average().orElse(0));
        }
        System.out.println("blackhole " + blackhole);
    }

    private static double[] measure(WordCounter counter, Path file) throws Exception {
        long warmupEnd = System.nanoTime() + WARMUP_NANOS;
        int warmups = 0;
        while (warmups < 2 || System.nanoTime() < warmupEnd) {
            blackhole += counter.topWords(file, TOP).size();
            warmups++;
        }

        double[] ms = new double[1024];
        int runs = 0;
        long measureEnd = System.nanoTime() + MEASURE_NANOS;
        while (runs < MIN_RUNS || (System.nanoTime() < measureEnd && runs < ms.length)) {
            long t0 = System.nanoTime();
            List<WordFreq> top = counter.topWords(file, TOP);
            long t1 = System.nanoTime();
            blackhole += top.size();
            ms[runs++] = (t1 - t0) / 1_000_000.0;
        }
        return Arrays.copyOf(ms, runs);
    }

    private static void check(Path file) throws Exception {
        List<WordFreq> v3 = new V3HashMap().topWords(file, Integer.MAX_VALUE);
        List<WordFreq> v4 = new V4Fast().topWords(file, Integer.MAX_VALUE);
        if (!v3.equals(v4)) {
            throw new IllegalStateException("v4 != v3");
        }
        Map<String, Integer> v2 = toMap(new V2Streams().topWords(file, Integer.MAX_VALUE));
        if (!v2.equals(toMap(v4))) {
            throw new IllegalStateException("v4 != v2");
        }
        System.out.println("ok: v2, v3, v4 give same counts, distinct words = " + v4.size());

        List<WordFreq> v1Top = new V1Arrays().topWords(file, TOP);
        List<WordFreq> v4Top = v4.subList(0, TOP);
        System.out.println("v1 top " + TOP + ": " + v1Top);
        System.out.println("v4 top " + TOP + ": " + v4Top);
        System.out.println();
    }

    private static Map<String, Integer> toMap(List<WordFreq> list) {
        Map<String, Integer> map = new HashMap<>();
        list.forEach(w -> map.put(w.word(), w.freq()));
        return map;
    }
}
