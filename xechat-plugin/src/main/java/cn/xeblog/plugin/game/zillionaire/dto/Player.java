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

    public void flushTips(){
        StringBuffer sb = new StringBuffer();
        String white = "  ";
        String br = "<br />";
        sb.append("<html>")
                .append("玩家: ").append(playerNode.getPlayer()).append(white)
                .append("状态: ").append(playerNode.getStatus() ? "正常" : "入狱").append(br)
                .append("位置: ").append(0).append(white)
                .append("名称: ").append("起点 ").append(br)
                .append("现金: ").append(playerNode.getCash()).append(white)
                .append("资产: ").append(playerNode.getProperty()).append(br)
                .append("</html>");
        tipsLabel.setText(sb.toString());
        tipsLabel.updateUI();
    }

}
