package cn.xeblog.plugin.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/3/30 9:14
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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



}
