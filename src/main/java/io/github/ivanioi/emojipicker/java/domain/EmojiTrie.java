package io.github.ivanioi.emojipicker.java.domain;

import java.util.*;

public class EmojiTrie {
    private final EmojiTrieNode root;
    private Long size;
    private final EmojiKeywordOptimizer keywordOptimizer;
    // 无法通过单词(a-z)检索的 keyword 直接放到 map 中
    private final Map<String, List<Emoji>> nonAlphabeticEmojiMap =  new HashMap<>();

    public EmojiTrie() {
        root = new EmojiTrieNode();
        size = 1L;

        keywordOptimizer = new FlagEmojiOptimizer();
    }

    public void insert(Emoji emoji) {
        emoji.getKeywords().forEach((keyword) -> {
           if (!isValid(keyword.toLowerCase())) insertNonAlphabeticEmojiKeyword(keyword, emoji);
           else insert(keyword.toLowerCase(), emoji);
        });

        // 优化 emoji keywords
        keywordOptimizer.handle(emoji).forEach(kw -> insert(kw, emoji));
    }

    private void insertNonAlphabeticEmojiKeyword(String keyword, Emoji emoji) {
        if (nonAlphabeticEmojiMap.containsKey(keyword.toLowerCase())) nonAlphabeticEmojiMap.get(keyword.toLowerCase()).add(emoji);
        else {
            List<Emoji> emojiList = new ArrayList<>();
            emojiList.add(emoji);
            nonAlphabeticEmojiMap.put(keyword.toLowerCase(), emojiList);
        }
        size++;
    }

    private static boolean isValid(String keyword) {
        return keyword.chars().filter(i -> i < 97 || i > 122).count() == 0;
    }

    public void insert(String keyword, Emoji emoji) {
        keyword = keyword.toLowerCase();

        if (!isValid(keyword)) {
            insertNonAlphabeticEmojiKeyword(keyword, emoji);
            return;
        }

        EmojiTrieNode currentNode = root;
        int[] idxs = keyword.chars().map(c -> c - 97).toArray();
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
        keyword = keyword.toLowerCase();
        ArrayList<Emoji> result = new ArrayList<>();

        if (!isValid(keyword)) {
            List<Emoji> emojiList = nonAlphabeticEmojiMap.get(keyword);
            return emojiList != null ? emojiList : result;
        }

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
