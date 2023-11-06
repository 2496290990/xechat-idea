package cn.xeblog.plugin.game.dld.ui.game;

import lombok.Data;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/10/26 8:47
 * @apiNote
 */
@Data
public class PlayerInfoTab {
    private JPanel infoPanel;
    private JProgressBar lvProgress;
    private JScrollPane attrScroll;
    private JLabel lvLabel;
    private JPanel attrPanel;
    private JPanel energyPanel;
    private JPanel hpAndAttackPanel;
    private JPanel nameAndAccountPanel;
    private JPanel defenceAndHitPanel;
    private JPanel comboAndFleePanel;
    private JProgressBar energyProgress;
    private JLabel energyLabel;
    private JLabel hp;
    private JLabel attack;
    private JLabel defender;
    private JLabel hit;
    private JLabel combo;
    private JLabel flee;
    private JLabel nickname;
    private JLabel account;
    private JPanel speedPanel;
    private JLabel speed;
    private JLabel critical;

}
