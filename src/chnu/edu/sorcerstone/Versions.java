package chnu.edu.sorcerstone;

import java.util.List;

public final class Versions {

    public static final List<WordCounter> ALL =
            List.of(new V1Arrays(), new V2Streams(), new V3HashMap(), new V4Fast());

    private Versions() {
    }

    public static WordCounter byKey(String key) {
        for (WordCounter c : ALL) {
            if (c.name().startsWith(key + " ")) {
                return c;
            }
        }
        throw new IllegalArgumentException("unknown version " + key + ", expected v1..v4");
    }
}
