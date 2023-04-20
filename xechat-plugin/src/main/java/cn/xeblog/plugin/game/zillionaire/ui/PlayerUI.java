package cn.xeblog.plugin.game.zillionaire.ui;

import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @author eleven
 * @date 2023/4/20 9:09
 * @apiNote
 */
@Getter
public class PlayerUI {
    private JPanel playerPanel;
    private JLabel userLabel;
    private JLabel positionLabel;
    private JLabel cashLabel;
    private Font font = new Font("", 0, 10);


    /**
     * 获取玩家面板
     *
     * @param playerNode   玩家节点
     * @param position 点位名称
     * @return JPanel          面板
     */
    public PlayerUI(PlayerNode playerNode, PositionDto position) {
        playerPanel.setMaximumSize(new Dimension(100, 80));
        userLabel.setText(formatStr("【%s】 %s", playerNode.getPlayer(), getPlayerStatusStr(playerNode.getStatus())));
        positionLabel.setText(formatStr("【%d】 %s", position.getPosition(), position.getName()));
        cashLabel.setText(formatStr("现金: %d 资产:%d", playerNode.getCash(), playerNode.getProperty()));
    }

    /**
     * 刷新玩家位置信息
     *
     * @param playerNode  玩家节点
     * @param positionDto 点位数据
     */
    public void refreshPosition(PlayerNode playerNode, PositionDto positionDto) {
        refreshPosition(playerNode, positionDto.getName());
    }

    /**
     * 刷新玩家位置信息
     *
     * @param playerNode  玩家节点
     * @param positionMap 点位映射
     */
    public void refreshPosition(PlayerNode playerNode, Map<Integer, PositionDto> positionMap) {
        refreshPosition(playerNode, positionMap.get(playerNode.getPosition()));
    }

    /**
     * 刷新玩家节点
     *
     * @param playerNode   玩家节点
     * @param positionName 点位名称
     */
    public void refreshPosition(PlayerNode playerNode, String positionName) {
        userLabel.setText(formatStr("【%s】 %s", playerNode.getPlayer(), getPlayerStatusStr(playerNode.getStatus())));
        positionLabel.setText(formatStr("【%d】 %s", playerNode.getPosition(), positionName));
        cashLabel.setText(formatStr("现金: %d 资产:%d", playerNode.getCash(), playerNode.getProperty()));
    }

    /**
     * 获取玩家状态字符串
     *
     * @param status 玩家状态
     * @return String      状态节点
     */
    private String getPlayerStatusStr(Boolean status) {
        return status ? "正常" : "休息";
    }

    /**
     * 格式化字符串
     *
     * @param format 格式化字符串
     * @param data   替换数据
     * @return String      字符串
     */
    private String formatStr(String format, Object... data) {
        return String.format(format, data);
    }
}
