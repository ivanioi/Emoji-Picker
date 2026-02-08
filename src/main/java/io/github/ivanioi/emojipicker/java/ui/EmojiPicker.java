package io.github.ivanioi.emojipicker.java.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.UIUtil;
import io.github.ivanioi.emojipicker.java.utils.EmojiStore;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

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
        List<String> categories = initCategroyContainer();
        initEmojiContainer(categories);
        initSearchInput();
    }

    private void initSearchInput() {
        // 初始化 searchInput
        searchInput.getTextEditor().addActionListener(e -> {
            String text = searchInput.getText();
            if (StringUtils.isBlank(text) || EmojiStore.tag2category(text) == null) return;
            // 1. 切换 card
            emojiContainerCardLayout.show(emojiContainer, EmojiStore.tag2category(text));
            // 2. 滚动对应 cardGroupComponent 到视口
            Rectangle bounds = emojiGroupComponentCache.get(text).getBounds();
            emojiGroupComponentCache.get(text).scrollRectToVisible(new Rectangle(0, 0, bounds.width, bounds.height));
        });
    }

    private void initEmojiContainer(List<String> categories) {
        // 初始化 Emoji Container
        emojis.getVerticalScrollBar().setUnitIncrement(35);
        emojis.setBackground(UIUtil.getPanelBackground().brighter());
        emojiContainer.setLayout(emojiContainerCardLayout);
        emojiContainer.setBackground(UIUtil.getPanelBackground().brighter());
        // 为每个 category 生成一个 Card Panel 存储相关的所有 Emoji
        categories.forEach((category) -> {
            JPanel panel = new JPanel(new VerticalLayout(10));
            panel.setBackground(UIUtil.getPanelBackground().brighter());
            // 把相同 tag 的 Emoji 放到一个 EmojiGroupCard 组件中
            EmojiStore.listTags(category).forEach(tag -> {
                EmojiGroupCard emojiGroupCard = new EmojiGroupCard(
                        tag,
                        EmojiStore.listEmojis(tag).stream().map(
                                emoji -> new EmojiCard(emoji.getChars(), emoji.getTag(), new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        // 复制 emoji 到系统剪切板中
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
                                        // 在底部展示 emoji 详情信息
                                        currentChars.setText(emoji.getChars());
                                        currentName.setText(emoji.getShortName() + "( " + emoji.getTag() + " of " + emoji.getCategory() + " )");
                                        currentKeywords.setText(emoji.getTag());
                                    }
                                })).toList());
                panel.add(emojiGroupCard);
                // cache EmojiGroupCard，用于将组件滚动到视口
                emojiGroupComponentCache.put(tag, emojiGroupCard);
            });

            emojiContainer.add(panel, category);
        });
        emojiContainerCardLayout.show(emojiContainer, categories.getFirst());
    }

    private @NotNull List<String> initCategroyContainer() {
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
        return categories;
    }


}
