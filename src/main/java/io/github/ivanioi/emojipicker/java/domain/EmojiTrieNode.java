package io.github.ivanioi.emojipicker.java.domain;

import java.util.List;

public class EmojiTrieNode {
    private EmojiTrieNode[] children = new EmojiTrieNode[26];
    private boolean isEndOfWord = false;
    private List<Emoji> emojis = null;

    public EmojiTrieNode() {
    }

    public EmojiTrieNode(EmojiTrieNode[] children, boolean isEndOfWord, List<Emoji> emojis) {
        this.children = children;
        this.isEndOfWord = isEndOfWord;
        this.emojis = emojis;
    }


    public EmojiTrieNode[] getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public List<Emoji> getEmojis() {
        return emojis;
    }

    public void setChildren(EmojiTrieNode[] children) {
        this.children = children;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public void setEmojis(List<Emoji> emojis) {
        this.emojis = emojis;
    }
}
