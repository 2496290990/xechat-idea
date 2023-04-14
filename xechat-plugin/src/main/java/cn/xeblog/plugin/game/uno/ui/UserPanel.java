package cn.xeblog.plugin.game.uno.ui;

import cn.xeblog.plugin.game.uno.entity.PlayerNode;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author eleven
 * @date 2023/4/14 14:07
 * @apiNote
 */
public class UserPanel {
    private JButton catchBtn;
    private JLabel nameLabel;
    private JLabel cardsLabel;
    private JLabel statusLabel;
    private JLabel unoLabel;
    private JPanel userPanel;

    public JPanel getUserPanel(PlayerNode playerNode, ActionListener actionListener){
        nameLabel.setText("昵称: " + playerNode.getPlayerName());
        cardsLabel.setText("手牌: " + playerNode.getCardsTotal());
        statusLabel.setText("状态: " );
        unoLabel.setText("UNO: NO");
        catchBtn.addActionListener(actionListener);
        return userPanel;
    }

    public void refreshUserPanel(PlayerNode playerNode) {
        nameLabel.setText("昵称: " + playerNode.getPlayerName());
        cardsLabel.setText("手牌: " + playerNode.getCardsTotal());
        statusLabel.setText("状态: ");
        unoLabel.setText("UNO: NO");
    }
}
