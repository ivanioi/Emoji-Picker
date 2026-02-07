package io.github.ivanioi.emojipicker.java.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import io.github.ivanioi.emojipicker.java.domain.Emoji;
import io.github.ivanioi.emojipicker.java.utils.EmojiStore;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiPicker {
    private JPanel mainPanel;
    private SearchTextField searchInput;
    private JPanel categoryContainer;
    private JPanel emojiDetail;
    private JScrollPane categories;
    private JPanel emojiContainer;
    private JScrollPane emojis;
    private JLabel currentChars;
    private JLabel currentName;
    private JPanel details;
    private JLabel currentKeywords;

    private CardLayout emojiContainerCardLayout = new CardLayout();

    // 用于检索 emoji tag 后将其滚动到视口中
    private Map<String, EmojiGroupCard> emojiGroupComponentCache = new HashMap<>();

    private Project project;
    public EmojiPicker(Project project) {
        this.project = project;
        initComponents();
    }

    public JComponent getRootPanel() {
        return mainPanel;
    }

    private void initComponents() {
        // 初始化 Category Container
        List<String> categories = EmojiStore.listCategories();
        categories.forEach((category) -> {
            categoryContainer.add(new EmojiCard(EmojiStore.getCategoryEmojiChars(category), category, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    emojiContainerCardLayout.show(emojiContainer, category);
                    emojis.getVerticalScrollBar().setValue(0);
                }
            }));
        });

        // 初始化 Emoji Container
        emojis.getVerticalScrollBar().setUnitIncrement(35);
        emojis.setBackground(UIUtil.getPanelBackground().brighter());
        emojiContainer.setLayout(emojiContainerCardLayout);
        emojiContainer.setBackground(UIUtil.getPanelBackground().brighter());
        categories.forEach((category) -> {
            JPanel panel = new JPanel(new VerticalLayout(10));
            panel.setBackground(UIUtil.getPanelBackground().brighter());
            EmojiStore.listTags(category).forEach(tag -> {
                EmojiGroupCard emojiGroupCard = new EmojiGroupCard(
                        tag,
                        EmojiStore.listEmojis(tag).stream().map(
                                emoji -> new EmojiCard(emoji.getChars(), emoji.getTag(), new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        StringSelection stringSelection = new StringSelection(emoji.getChars());
                                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

                                        ToolWindowManager.getInstance(project).invokeLater(() -> {
                                            NotificationGroupManager.getInstance()
                                                    .getNotificationGroup("Emoji Picker(IZ) Notifaction Group")
                                                    .createNotification("Cpied: " + emoji.getChars(), NotificationType.INFORMATION)
                                                    .notify(project);
                                        });

                                    }

                                    @Override
                                    public void mouseEntered(MouseEvent e) {
                                        currentChars.setText(emoji.getChars());
                                        currentName.setText(emoji.getShortName() + "( " + emoji.getTag() + " of " + emoji.getCategory() + " )");
                                        currentKeywords.setText(emoji.getTag());
                                    }
                                })).toList());
                panel.add(emojiGroupCard);

                emojiGroupComponentCache.put(tag, emojiGroupCard);
            });

            emojiContainer.add(panel, category);
        });
        emojiContainerCardLayout.show(emojiContainer, categories.getFirst());

        // 初始化 searchInput
        searchInput.getTextEditor().addActionListener(e -> {
            String text = searchInput.getText();
            if (StringUtils.isBlank(text) || EmojiStore.tag2category(text) == null) return;
            // 1. 切换 card
            emojiContainerCardLayout.show(emojiContainer, EmojiStore.tag2category(text));
            // 2. 滚动对应 tag 到视口中
            Rectangle bounds = emojiGroupComponentCache.get(text).getBounds();
            emojiGroupComponentCache.get(text).scrollRectToVisible(new Rectangle(0, 0, bounds.width, bounds.height));
        });
    }




    public static class EmojiGroupCard extends JBPanel<EmojiGroupCard> {

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
            return new Dimension(Integer.MAX_VALUE, preferredSize.height);
        }
    }

    private static class EmojiCard extends JPanel {
        private final Color normalColor = UIUtil.getPanelBackground();
        private final Color hoverColor = JBColor.namedColor("Button.hoverBackground", new JBColor(0xe6e6e6, 0x4e5052));

        /**
         *
         * @param emojiText
         * @param description
         * @param callback    mouseEntered, mouseExited, mouseClicked
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
}
