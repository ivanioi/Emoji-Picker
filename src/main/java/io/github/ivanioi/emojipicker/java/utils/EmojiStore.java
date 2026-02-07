package io.github.ivanioi.emojipicker.java.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.util.ResourceUtil;
import io.github.ivanioi.emojipicker.java.domain.Emoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiStore {
    private static final Logger log = LoggerFactory.getLogger(EmojiStore.class);
    private static final Map<String, String> category2emojiChars = new HashMap<>();
    private static final Map<String, List<String>> category2tags = new HashMap<>();
    private static final Map<String, List<Emoji>> tag2emojis = new HashMap<>();
    private static final Map<String, String> tag2category = new HashMap<>();
    private static int size = 0;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Emoji> emojiList = objectMapper.readValue(ResourceUtil.getResource(EmojiStore.class.getClassLoader(), "config", "emojis.json"),
                    new TypeReference<List<Emoji>>() {});
            log.info("Configuration data loading......");
            log.info("Emoji Count: {}", emojiList.size());

            emojiList.forEach(emoji -> {
                if (!category2emojiChars.containsKey(emoji.getCategory())) {
                    category2emojiChars.put(emoji.getCategory(), emoji.getChars());
                }
                if (!category2tags.containsKey(emoji.getCategory())) {
                    category2tags.put(emoji.getCategory(), new ArrayList<>());
                }
                if (!tag2emojis.containsKey(emoji.getTag())) {
                    tag2emojis.put(emoji.getTag(), new ArrayList<>());
                }

                if (!category2tags.get(emoji.getCategory()).contains(emoji.getTag())) {
                    category2tags.get(emoji.getCategory()).add(emoji.getTag());
                }
                tag2emojis.get(emoji.getTag()).add(emoji);

                tag2category.put(emoji.getTag(), emoji.getCategory());
                size++;
            });
            log.info("Data loading and initialization are complete.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> listCategories() {
        return category2tags.keySet().stream().toList();
    }

    public static String getCategoryEmojiChars(String category) {
        return  category2emojiChars.get(category);
    }

    public static List<String> listTags(String category) {
        return category2tags.get(category);
    }

    public static List<Emoji> listEmojis(String tag) {
        return tag2emojis.get(tag);
    }

    public static int size() {
        return size;
    }

    public static String tag2category(String tag) {
        return tag2category.get(tag);
    }
}
