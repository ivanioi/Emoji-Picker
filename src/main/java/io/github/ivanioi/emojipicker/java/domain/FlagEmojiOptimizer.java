package io.github.ivanioi.emojipicker.java.domain;

import java.util.ArrayList;
import java.util.List;

public class FlagEmojiOptimizer implements EmojiKeywordOptimizer {
    private EmojiKeywordOptimizer next;

    @Override
    public void setNext(EmojiKeywordOptimizer next) {
        this.next = next;
    }

    @Override
    public List<String> handle(Emoji emoji) {
        /**
         * 由于所有国家的 flag emoji 的 keyword 只有 flag，这导致搜索 country name 无法查到对应的 flag
         * 将每个国家名称也当作 emoji 的 keywords 插入 EmojiTire
         * 国旗 emoji 的名称格式为:
         *    {
         *        "shortName": "flag: Albania",
         *        "chars": "🇦🇱",
         *        "code": "U+1F1E6 U+1F1F1",
         *        "category": "Flags",
         *        "tag": "country-flag",
         *        "keywords": [
         *           "flag"
         *        ]
         *    },
         */

        List<String> result = new ArrayList<>();
        if ("country-flag".equals(emoji.getTag()) && emoji.getShortName().startsWith("flag: ")) {
            String countryName = emoji.getShortName().split(": ")[1];
            result.add(countryName);
        }

        if (next != null) result.addAll(next.handle(emoji));
        return  result;
    }
}
