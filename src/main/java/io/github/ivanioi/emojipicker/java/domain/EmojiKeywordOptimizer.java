package io.github.ivanioi.emojipicker.java.domain;

import java.util.List;

/**
 * 用于从 emoji 中提取新的 keywords
 */
public interface EmojiKeywordOptimizer {
    void setNext(EmojiKeywordOptimizer next);
    List<String> handle(Emoji emoji);
}
