package cn.xeblog.plugin.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.plugin.game.zillionaire.ui.PlayerUI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.util.Map;

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

    private PlayerUI playerUI;



    public Player(PlayerNode playerNode, JPanel panel) {
        this.playerNode = playerNode;
        this.panel = panel;
    }

    public void refreshTips(PositionDto position) {
        //tipsLabel.setText(getTipsString(position));
        //tipsLabel.updateUI();
        playerUI.refreshPosition(playerNode, position);
    }

    public void refreshTips(Map<Integer, PositionDto> positionMap) {
        refreshTips(positionMap.get(playerNode.getPosition()));
    }

    private String getTipsString(PositionDto position) {
        StringBuffer sb = new StringBuffer();
        String white = "  ";
        String br = "<br />";
        boolean nullFlag = null == position;
        sb.append("<html>")
                .append("玩家: ").append(playerNode.getPlayer()).append(white)
                .append("状态: ").append(playerNode.getStatus() ? "正常" : "休息").append(br)
                .append("位置: ").append(nullFlag ? 0 : position.getPosition()).append(white)
                .append("名称: ").append(nullFlag ? "起点" : position.getName()).append(br)
                .append("现金: ").append(playerNode.getCash()).append(white)
                .append("资产: ").append(playerNode.getProperty()).append(br)
                .append("</html>");
        return sb.toString();
    }
}
