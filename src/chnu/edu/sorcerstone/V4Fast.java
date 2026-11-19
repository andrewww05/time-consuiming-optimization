package chnu.edu.sorcerstone;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class V4Fast implements WordCounter {

    @Override
    public String name() {
        return "v4 fast";
    }

    @Override
    public List<WordFreq> topWords(Path file, int limit) throws IOException {
        byte[] text = Files.readAllBytes(file);
        return count(text).top(limit);
    }

    static Table count(byte[] text) {
        Table table = new Table(text);
        int start = -1;
        int hash = 0;
        for (int i = 0; i < text.length; i++) {
            int c = text[i] | 0x20;
            if (c >= 'a' && c <= 'z') {
                if (start < 0) {
                    start = i;
                    hash = 0;
                }
                text[i] = (byte) c;
                hash = 31 * hash + c;
            } else if (start >= 0) {
                table.add(start, i - start, hash);
                start = -1;
            }
        }
        if (start >= 0) {
            table.add(start, text.length - start, hash);
        }
        return table;
    }

    static final class Table {
        private static final int INITIAL_CAPACITY = 1 << 13;

        private final byte[] text;
        private int[] offsets = new int[INITIAL_CAPACITY];
        private int[] lengths = new int[INITIAL_CAPACITY];
        private int[] hashes = new int[INITIAL_CAPACITY];
        private int[] counts = new int[INITIAL_CAPACITY];
        private int mask = INITIAL_CAPACITY - 1;
        private int shift = 32 - Integer.numberOfTrailingZeros(INITIAL_CAPACITY);
        private int size;

        Table(byte[] text) {
            this.text = text;
        }

        int size() {
            return size;
        }

        void add(int offset, int length, int hash) {
            int i = index(hash);
            while (true) {
                int count = counts[i];
                if (count == 0) {
                    offsets[i] = offset;
                    lengths[i] = length;
                    hashes[i] = hash;
                    counts[i] = 1;
                    if (++size * 2 > counts.length) {
                        grow();
                    }
                    return;
                }
                if (hashes[i] == hash && lengths[i] == length
                        && Arrays.equals(text, offsets[i], offsets[i] + length,
                                         text, offset, offset + length)) {
                    counts[i] = count + 1;
                    return;
                }
                i = (i + 1) & mask;
            }
        }

        private int index(int hash) {
            return (hash * 0x9E3779B9) >>> shift;
        }

        private void grow() {
            int[] oldOffsets = offsets;
            int[] oldLengths = lengths;
            int[] oldHashes = hashes;
            int[] oldCounts = counts;
            int capacity = oldCounts.length * 2;
            offsets = new int[capacity];
            lengths = new int[capacity];
            hashes = new int[capacity];
            counts = new int[capacity];
            mask = capacity - 1;
            shift--;
            for (int j = 0; j < oldCounts.length; j++) {
                if (oldCounts[j] == 0) {
                    continue;
                }
                int i = index(oldHashes[j]);
                while (counts[i] != 0) {
                    i = (i + 1) & mask;
                }
                offsets[i] = oldOffsets[j];
                lengths[i] = oldLengths[j];
                hashes[i] = oldHashes[j];
                counts[i] = oldCounts[j];
            }
        }

        private int order(int a, int b) {
            if (counts[a] != counts[b]) {
                return Integer.compare(counts[b], counts[a]);
            }
            return Arrays.compare(text, offsets[a], offsets[a] + lengths[a],
                                  text, offsets[b], offsets[b] + lengths[b]);
        }

        List<WordFreq> top(int limit) {
            int k = Math.min(limit, size);
            if (k <= 0) {
                return List.of();
            }
            PriorityQueue<Integer> heap = new PriorityQueue<>(k + 1, (a, b) -> order(b, a));
            for (int i = 0; i < counts.length; i++) {
                if (counts[i] == 0) {
                    continue;
                }
                if (heap.size() < k) {
                    heap.add(i);
                } else if (order(i, heap.peek()) < 0) {
                    heap.poll();
                    heap.add(i);
                }
            }
            List<WordFreq> result = new ArrayList<>(k);
            while (!heap.isEmpty()) {
                int i = heap.poll();
                String word = new String(text, offsets[i], lengths[i], StandardCharsets.ISO_8859_1);
                result.add(new WordFreq(word, counts[i]));
            }
            Collections.reverse(result);
            return result;
        }
    }
}
