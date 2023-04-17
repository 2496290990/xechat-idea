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
    private JLabel teamFlag;
    private ActionListener catchAction;

    public JPanel getUserPanel(PlayerNode playerNode, ActionListener catchAction){
        nameLabel.setText(String.format("【%s】", playerNode.getPlayerName()));
        cardsLabel.setText(String.format("手牌: %d", playerNode.getCardsTotal()));
        statusLabel.setText(String.format("【%s】", playerNode.getPlayerStatus().getStatus()));
        unoLabel.setText(playerNode.getUno() ? "UNO" : "");
        teamFlag.setText(playerNode.getTeamName());
        this.catchAction = catchAction;
        catchBtn.addActionListener(catchAction);
        return userPanel;
    }

    public void refreshUserPanel(PlayerNode playerNode) {
        cardsLabel.setText(String.format("手牌: %d", playerNode.getCardsTotal()));
        statusLabel.setText(String.format("【%s】", playerNode.getPlayerStatus().getStatus()));
        unoLabel.setText(playerNode.getUno() ? "UNO" : "");
    }
}
