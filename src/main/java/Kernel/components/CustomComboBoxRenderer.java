package Kernel.components;

import javax.swing.*;
import java.awt.*;

public class CustomComboBoxRenderer implements ListCellRenderer<String> {
    private Color backgroundColor;

    public CustomComboBoxRenderer(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = new JLabel(value);
        label.setOpaque(true);
        label.setBackground(backgroundColor);
        if (isSelected) {
            label.setBackground(list.getSelectionBackground());
        }

        return label;
    }
}