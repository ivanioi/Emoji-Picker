package io.github.ivanioi.emojipicker.java.ui;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class EmojiCard extends JPanel {
    private final Color normalColor = UIUtil.getPanelBackground();
    private final Color hoverColor = JBColor.namedColor("Button.hoverBackground", new JBColor(0xe6e6e6, 0x4e5052));

    /**
     *
     * @param emojiText
     * @param description
     * @param callback   only calling mouseEntered, mouseExited, mouseClicked
     */
    public EmojiCard(String emojiText, String description, MouseAdapter callback) {
        // 设置卡片布局
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(60, 60)); // 固定的卡片大小
        this.setBackground(normalColor);

        // 设置卡片边框：圆角边框 (IntelliJ 风格)
        this.setBorder(JBUI.Borders.customLine(JBColor.border(), 1));

        // 设置气泡提示文本
        this.setToolTipText(description);

        // 添加 Emoji 文本
        JLabel label = new JLabel(emojiText, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        this.add(label, BorderLayout.CENTER);

        // 4. 鼠标悬浮逻辑 (Hover Effect)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // 鼠标进入，改变背景色
                setBackground(hoverColor);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                callback.mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 鼠标离开，恢复背景色
                setBackground(normalColor);
                setCursor(Cursor.getDefaultCursor());
                callback.mouseExited(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 可以在这里添加点击 Emoji 的动作
                callback.mouseClicked(e);
            }
        });
    }
}