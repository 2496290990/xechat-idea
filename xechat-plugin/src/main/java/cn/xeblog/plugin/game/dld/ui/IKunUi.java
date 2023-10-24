package cn.xeblog.plugin.game.dld.ui;

import lombok.Data;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/10/24 14:33
 * @apiNote
 */
@Data
public class IKunUi {

    private JPanel mainPanel;
    private JLabel gameTitle;
    private JPanel centerPanel;
    private JPanel footerPanel;

    public IKunUi(String gameTitle) {
        this.gameTitle.setText(gameTitle);
    }
}
