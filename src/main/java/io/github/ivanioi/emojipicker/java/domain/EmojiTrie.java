package io.github.ivanioi.emojipicker.java.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmojiTrie {
    private final EmojiTrieNode root;
    private Long size;

    public EmojiTrie() {
        root = new EmojiTrieNode();
        size = 1L;
    }

    public void insert(Emoji emoji) {
        emoji.getKeywords().forEach((keyword) -> {
           if (!isValid(keyword)) return;
           insert(keyword, emoji);
        });
    }

    private static boolean isValid(String keyword) {
        return keyword.chars().filter(i -> i < 97 || i > 122).count() == 0;
    }

    private void insert(String keyword, Emoji emoji) {
        if (!isValid(keyword)) return;

        EmojiTrieNode currentNode = root;
        int[] idxs = keyword.toLowerCase().chars().map(c -> c - 97).toArray();
        for (int i = 0; i < idxs.length; i++) {
            int idx = idxs[i];
            EmojiTrieNode nextNode = Objects.isNull(currentNode.getChildren()[idx]) ? new EmojiTrieNode() : currentNode.getChildren()[idx];
            if (Objects.isNull(currentNode.getChildren()[idx])) {
                currentNode.getChildren()[idx] = nextNode;
            }
            currentNode = nextNode;
            if (i == idxs.length - 1) {
                currentNode.setEndOfWord(true);
                if (Objects.isNull(currentNode.getEmojis())) {
                    currentNode.setEmojis(new ArrayList<>());
                }
                currentNode.getEmojis().add(emoji);
            }
        }
        size++;
    }

    public long size() {
        return size;
    }

    /**
     * 检索 keyword 路径上所有有效的子 keyword，比如 "faceflag" -> 被识别为 face and faceflag 两个 keyword
     * @param keyword
     * @return
     */
    public List<Emoji> search(String keyword) {
        if (!isValid(keyword)) return new ArrayList<>();
        ArrayList<Emoji> result = new ArrayList<>();

        int[] idxs = keyword.toLowerCase().chars().map(c -> c - 97).toArray();
        var currentNode = root;
        /**
         * 检索 keyword 路径上所有有效的子 keyword，比如 "faceflag" -> 被识别为 face and faceflag 两个 keyword
         */
        for (int idx : idxs) {
            EmojiTrieNode nextNode = currentNode.getChildren()[idx];
            if (Objects.isNull(nextNode)) break;
            if (nextNode.isEndOfWord()) result.addAll(nextNode.getEmojis());
            currentNode = nextNode;
        }

        return result;
    }
}
