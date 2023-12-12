package cn.xeblog.plugin.game.dld.ui.game;

import lombok.Data;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/10/24 16:15
 * @apiNote
 */
@Data
public class PvpTab {
    private JPanel pvpPanel;
    private JPanel playerArea;
    private JTextArea fightArea;
    private JButton clearBtn;
    private JButton refreshBtn;
    private JLabel fightLabel;
    private JLabel playerLabel;
    private JTextField searchTextField;
    private JButton searchBtn;
    private JButton prevBtn;
    private JButton nextBtn;
    private JLabel pageLabel;
    private JPanel pvpFooterPanel;
    private JButton toBtn;

    public PvpTab() {

    }
}
