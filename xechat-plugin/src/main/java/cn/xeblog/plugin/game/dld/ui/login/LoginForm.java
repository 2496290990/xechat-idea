package cn.xeblog.plugin.game.dld.ui.login;

import lombok.Data;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author eleven
 * @date 2023/10/24 10:19
 * @apiNote
 */
@Data
public class LoginForm {
    private JPanel dldPanel;
    private JComboBox<String> loginTypeComboBox;
    private JLabel typeLabel;
    private JLabel gameTitle;
    private JTextField accountTextField;
    private JTextField pwdTextField;
    private JButton loginBtn;
    private JButton verifyBtn;
    private JLabel accountLabel;
    private JLabel pwdLabel;
    private JButton regBtn;
    private JPanel dldMainPanel;
    private JPanel gamePanel;
    private JPanel extPanel;

    private Integer loginType = 0;



    public LoginForm() {

        loginTypeComboBox.addItem("MAC自动登录");
        loginTypeComboBox.addItem("账号密码登录");
        loginTypeComboBox.addItem("邮箱验证码登录");
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        verifyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        loginTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginType = loginTypeComboBox.getSelectedIndex();
                switch (loginType) {
                    case 0:
                        accountVisible(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void accountVisible(boolean flag) {
        accountLabel.setVisible(flag);
        accountTextField.setVisible(flag);
        pwdLabel.setVisible(flag);
        pwdTextField.setVisible(flag);
    }

}
