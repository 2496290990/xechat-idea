package cn.xeblog.plugin.game.dld.ui.login;

import lombok.Data;

import javax.swing.*;

/**
 * @author eleven
 * @date 2023/10/30 15:22
 * @apiNote
 */
@Data
public class AccountLogin {
    private JPanel accountLoginPanel;
    private JTextField account;
    private JButton loginBtn;
    private JButton regBtn;
    private JPanel accountPanel;
    private JLabel accountLabel;
    private JPanel pwdPanel;
    private JLabel pwdLabel;
    private JPanel btnPanel;
    private JTextField password;
}
