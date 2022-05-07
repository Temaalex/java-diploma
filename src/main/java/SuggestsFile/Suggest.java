package SuggestsFile;


import lombok.Data;

import java.util.Objects;
@Data
public class Suggest {
    private final String keyWord;
    private final String title;
    private final String url;
    private final String lowercase;

    public Suggest(String keyWord, String title, String url) {
        this.keyWord = keyWord;
        this.title = title;
        this.url = url;
        this.lowercase = keyWord.toLowerCase();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suggest suggest = (Suggest) o;
        return Objects.equals(keyWord, suggest.keyWord)
                && Objects.equals(title, suggest.title)
                && Objects.equals(url, suggest.url);
    }
    @Override
    public int hashCode() {
        return Objects.hash(keyWord, title, url);
    }

    @Override
    public String toString() {
        return String.format("Suggest: keyWord = %s, title = %s, url = %s", keyWord, title, url);
    }
}
