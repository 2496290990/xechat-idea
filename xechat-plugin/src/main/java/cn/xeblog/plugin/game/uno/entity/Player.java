package cn.xeblog.plugin.game.uno.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;


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

    public Player(PlayerNode playerNode, JPanel panel) {
        this.panel = panel;
        this.playerNode = playerNode;
    }


}
