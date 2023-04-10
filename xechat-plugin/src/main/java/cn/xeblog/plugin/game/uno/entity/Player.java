package cn.xeblog.plugin.game.uno.entity;


import javax.swing.*;

/**
 * @author eleven
 * @date 2023/4/10 9:47
 * @apiNote
 */
public class Player {
    /**
     * 玩家节点
     */
    private PlayerNode playerNode;
    /**
     * 玩家容器
     */
    private JPanel panel;
    /**
     * 提示面板
     */
    private JLabel tipsLabel;

    public Player(PlayerNode playerNode, JPanel panel) {
        this.playerNode = playerNode;
        this.panel = panel;
    }

    public void showTips(String tips) {
        tipsLabel.setText(tips);
        tipsLabel.updateUI();
    }

    public void refreshTips() {
        tipsLabel.setText("");
        tipsLabel.updateUI();
    }
}
