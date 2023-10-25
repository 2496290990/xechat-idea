package cn.xeblog.plugin.game.dld.ui.game;

import lombok.Data;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/10/24 16:03
 * @apiNote
 */
@Data
public class MasterGame {
    private JPanel gamePanel;
    private JTabbedPane tab;
    private JPanel pvpPanel;
    private JPanel instancePanel;
    private JPanel playerPanel;
    private JPanel packagePanel;
    private JPanel boosPanel;
}
