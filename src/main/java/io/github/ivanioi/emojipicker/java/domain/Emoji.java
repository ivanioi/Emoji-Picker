package io.github.ivanioi.emojipicker.java.domain;

public class Emoji {
    private String chars;
    private String code;
    private String shortName;
    private String category;
    private String tag;

    public Emoji() {}

    public Emoji(String chars, String code, String shortName, String category, String tag) {
        this.chars = chars;
        this.code = code;
        this.shortName = shortName;
        this.category = category;
        this.tag = tag;
    }

    // https://unicode.org/emoji/charts/full-emoji-list.html#subdivision-flag

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
