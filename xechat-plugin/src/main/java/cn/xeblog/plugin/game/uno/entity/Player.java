package cn.xeblog.plugin.game.uno.entity;


import cn.xeblog.plugin.game.uno.ui.UserPanel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.event.ActionListener;


/**
 * @author eleven
 * @date 2023/4/10 9:47
 * @apiNote
 */
@Data
@NoArgsConstructor
public class Player {
    /**
     * 玩家节点
     */
    private PlayerNode playerNode;

    private JPanel panel;

    private UserPanel userPanel;

    public Player(PlayerNode playerNode, JPanel panel) {
        this.panel = panel;
        this.playerNode = playerNode;
    }

    public JPanel getUserUI(ActionListener e){
        userPanel = new UserPanel();
        return userPanel.getUserPanel(playerNode, e);
    }

    /**
     * 刷新面板
     */
    public void refreshUserPanel() {
        userPanel.refreshUserPanel(playerNode);
    }

}
