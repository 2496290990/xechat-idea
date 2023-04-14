package cn.xeblog.plugin.game.uno.ui;

import cn.xeblog.commons.entity.game.uno.Card;
import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author eleven
 * @date 2023/4/14 12:52
 * @apiNote
 */
public class CardPanel {
    private JPanel cardPanel;
    private JLabel value;
    private Card card;
    Color ideaLineColor = new Color(0x47, 0x57, 0x65);
    Color ideaEditorColor = new Color(0x2b, 0x2b, 0x2b);
    Color ideaGrayColor = new Color(0xAF, 0xB1, 0xB3);
    Font functionFont = new Font("", 1, 9);
    Font numFont = new Font("", 1, 14);

    public CardPanel(){

    }

    public JPanel getCardPanel(Card card, Color color) {
        this.card = card;
        if (null == color) {
            color = card.getChangeColor() == null ? card.getColor() : card.getChangeColor();
        }
        cardPanel.setBorder(new LineBorder(color, 1));
        cardPanel.setPreferredSize(new Dimension(45, 60));
        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        value.setText(card.getValue());
        value.setForeground(color);
        value.setFont(card.getIsFunctionCard() ? functionFont : numFont);
        return cardPanel;
    }

}
