package cn.xeblog.plugin.game.dld;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author eleven
 * @date 2023/10/12 15:49
 * @apiNote
 */
@DoGame(Game.IKUN)
public class IKunDld extends AbstractGame {

    /**
     * 主面板
     */
    private JPanel mainPanel;

    private JTabbedPane loginTab = new JBTabbedPane();

    private JComboBox loginTypeComboBox = null;

    private Integer loginType = 0;
    private static final String GAME_NAME = "爱坤大乐斗";
    @Override
    protected void start() {
        initPanel();
        int width = 240;
        int height = 300;
        mainPanel.setMinimumSize(new Dimension(width + 10, height + 10));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        mainPanel.add(loginPanel(), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getBackGameButton());
        buttonPanel.add(getExitButton());
        mainPanel.add(buttonPanel,BorderLayout.SOUTH);
        mainPanel.updateUI();
    }

    private JPanel loginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4,1));
        String[] loginTypeArr=  {
                "MAC自动登录",
                "账号密码登录",
                "邮箱验证码登录"
        };
        loginTypeComboBox = new ComboBox(loginTypeArr);
        loginPanel.add(getTitleLabel());
        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("登录类型："));
        typePanel.add(loginTypeComboBox);
        loginPanel.add(typePanel);
        JPanel textPanel = new JPanel(new GridLayout(2,2));
        JPanel btnPanel = new JPanel(new GridLayout(1,3));
        JButton loginBtn = new JButton("登录");
        JButton regBtn = new JButton("注册");
        JButton verifyBtn = new JButton("获取验证码");
        btnPanel.add(loginBtn);
        btnPanel.add(regBtn);

        JPanel accountPanel = new JPanel(new GridLayout(1,2));
        JPanel pwdPanel = new JPanel(new GridLayout(1,2));
        JTextField accountText = new JTextField();
        JTextField pwdText = new JTextField();
        loginTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginType = loginTypeComboBox.getSelectedIndex();
                textPanel.removeAll();
                accountPanel.removeAll();
                pwdPanel.removeAll();
                switch (loginType) {
                    case 1:
                        accountPanel.add(new JLabel("账号："));
                        accountPanel.add(accountText);
                        pwdPanel.add(new JLabel("密码："));
                        pwdPanel.add(pwdText);

                        break;
                    case 2:
                        accountPanel.add(new JLabel("邮箱："));
                        accountPanel.add(accountText);
                        pwdPanel.add(new JLabel("验证码"));
                        pwdPanel.add(pwdText);
                        btnPanel.add(verifyBtn);
                        break;
                    default:
                        break;
                }
                textPanel.add(accountPanel);
                textPanel.add(pwdPanel);
                loginPanel.updateUI();
                btnPanel.updateUI();
            }
        });
        loginPanel.add(textPanel);
        loginPanel.add(btnPanel);
        return loginPanel;
    }
    @Override
    protected void init() {
        initPanel();
        mainPanel.setMinimumSize(new Dimension(150, 200));
        JPanel startPanel = new JPanel();
        startPanel.setBounds(10, 10, 150, 200);
        mainPanel.add(startPanel);
        startPanel.add(getTitleLabel());
        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);
        vBox.add(Box.createVerticalStrut(5));
        vBox.add(getStartGameButton());
        vBox.add(getExitButton());
        mainPanel.updateUI();
    }

    private JLabel getTitleLabel() {
        JLabel title = new JLabel(GAME_NAME);
        title.setFont(new Font("", Font.BOLD, 14));
        return title;
    }

    /**
     * 初始化JPanel
     */
    private void initPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }

    @Override
    protected JPanel getComponent() {
        return mainPanel;
    }

    private JButton getStartGameButton() {
        JButton button = new JButton("开始游戏");
        button.addActionListener(e -> start());
        return button;
    }

    private JButton getBackGameButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }
}
