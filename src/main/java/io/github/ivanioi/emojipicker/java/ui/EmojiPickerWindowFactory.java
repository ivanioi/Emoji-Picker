package io.github.ivanioi.emojipicker.java.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class EmojiPickerWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        EmojiPickerPanel emojiPickerPanel = new EmojiPickerPanel(project);

        ContentManager contentManager = toolWindow.getContentManager();
        Content emojiPicker = contentManager.getFactory().createContent(emojiPickerPanel, "", true);
        contentManager.addContent(emojiPicker);


    }
}
