package io.github.ivanioi.emojipicker.java.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

public class EmojiPickerPanel extends SimpleToolWindowPanel {
    public EmojiPickerPanel(Project project) {
        super(true, true);
        EmojiPicker emojiPicker = new EmojiPicker(project);
        setContent(emojiPicker.getRootPanel());
    }
}
