package cn.xeblog.plugin.game.dld.ui.game.playerPackege;

import lombok.Data;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/11/8 10:30
 * @apiNote
 */
@Data
public class PackageTab {
    private JPanel packageTabPanel;
    private JTabbedPane tab;
    private JPanel skillPanel;
    private JPanel weaponPanel;
    private JPanel materialsPanel;
    private JPanel propPanel;
}
