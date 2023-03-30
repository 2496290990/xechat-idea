package cn.xeblog.plugin.game.zillionaire.dto;

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
    /**
     * 现金标签
     */
    private JLabel cashLabel;
    /**
     * 状态标签
     */
    private JLabel statusLabel;
    /**
     * 资产标签
     */
    private JLabel propertyLabel;
    /**
     * 昵称标签
     */
    private JLabel nicknameLabel;

}
