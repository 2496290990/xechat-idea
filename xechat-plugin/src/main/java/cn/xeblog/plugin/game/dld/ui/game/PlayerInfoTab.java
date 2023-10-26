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
    private JPanel playerPanel;
    private JProgressBar lvProgress;
    private JScrollPane attrScroll;
    private JLabel lvLabel;

}
