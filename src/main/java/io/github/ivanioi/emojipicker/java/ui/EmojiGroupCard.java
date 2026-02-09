package io.github.ivanioi.emojipicker.java.ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EmojiGroupCard extends JBPanel<EmojiGroupCard> {

    private final JPanel gridPanel;

    /**
     * @param title 分组标题
     */
    public EmojiGroupCard(String title) {

        this.setLayout(new BorderLayout());
        this.setBackground(UIUtil.getPanelBackground().brighter());
        this.setBorder(JBUI.Borders.empty(10, 5));

        // 1. 标题部分
        if (!StringUtils.isBlank(title)) {
            JBLabel titleLabel = new JBLabel(title);
            titleLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD, 25f));
            titleLabel.setBorder(JBUI.Borders.empty(0, 5, 8, 0));
            this.add(titleLabel, BorderLayout.NORTH);

        }

        // 2. 表情网格部分 - 固定 8 列
        // GridLayout(rows, cols, hgap, vgap)
        // rows 设为 0 表示行数根据组件数量自动计算
        gridPanel = new JPanel(new GridLayout(0, 8, 5, 5));
        gridPanel.setBackground(UIUtil.getPanelBackground().brighter());

        // 3. 避免 cell 高度被拉伸
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(gridPanel, BorderLayout.NORTH);
        this.add(wrapper, BorderLayout.CENTER);
    }

    public EmojiGroupCard(String title, List<EmojiCard> emojiCards) {
        this(title);
        for (EmojiCard emojiCard : emojiCards) {
            this.addEmoji(emojiCard);
        }
    }


    public void addEmoji(EmojiCard emojiCard) {
        gridPanel.add(emojiCard);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension preferredSize = getPreferredSize();
        /**
         * 避免 gridLayout 拉伸高度:
         *  EmojiPicker 使用 CardLayout 展示不同 Category 下的 Emoji, 最外部的容器是一个 ScrollPanel, 它的高度由拥有最多 Emoji 的 CardPanel 决定
         *  这会导致其它 CardPanel 存在大量的空余高度，该组件使用 GridLayout 布局 EmojiCard 组件, 默认会拉伸其中 CELL 的高度填充空余高度
         */
        return new Dimension(Integer.MAX_VALUE, preferredSize.height);
    }
}