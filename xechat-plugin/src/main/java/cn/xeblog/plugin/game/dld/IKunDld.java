package cn.xeblog.plugin.game.dld;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.common.Page;
import cn.xeblog.plugin.game.dld.model.dto.BattleDto;
import cn.xeblog.plugin.game.dld.model.dto.LoginDto;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.dld.model.dto.PlayerDto;
import cn.xeblog.plugin.game.dld.model.vo.PlayerVo;
import cn.xeblog.plugin.game.dld.model.vo.ProcessVo;
import cn.xeblog.plugin.game.dld.utils.HttpSendUtil;
import cn.xeblog.plugin.game.dld.utils.ResultUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import com.google.gson.Gson;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextArea;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author eleven
 * @date 2023/10/12 15:49
 * @apiNote
 */
//@DoGame(Game.IKUN)
@Slf4j
public class IKunDld extends AbstractGame {

    /**
     * 主面板
     */
    private JPanel mainPanel;

    private JPanel uiPanel = null;

    private JTabbedPane loginTab = new JBTabbedPane();

    private JTextArea processTextArea = new JBTextArea();

    private JBScrollPane processScroll = new JBScrollPane(processTextArea);

    private JComboBox loginTypeComboBox = null;

    private Integer loginType = 0;
    private static final String GAME_NAME = "爱坤大乐斗";

    private Gson gson = new Gson();

    private User currentUser = DataCache.getCurrentUser();

    @Override
    protected void start() {
        initPanel();
        uiPanel = new JPanel();
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
        uiPanel.setLayout(new GridLayout(4,1));
        String[] loginTypeArr=  {
                "MAC自动登录",
                "账号密码登录",
                "邮箱验证码登录"
        };
        loginTypeComboBox = new ComboBox(loginTypeArr);
        uiPanel.add(getTitleLabel());
        JPanel typePanel = new JPanel();
        typePanel.add(new JLabel("登录类型："));
        typePanel.add(loginTypeComboBox);
        uiPanel.add(typePanel);
        JPanel textPanel = new JPanel(new GridLayout(2,2));
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("登录");
        JButton regBtn = new JButton("注册");
        JButton verifyBtn = new JButton("获取验证码");
        btnPanel.add(loginBtn);
        btnPanel.add(regBtn);

        JPanel accountPanel = new JPanel(new GridLayout(1,2));
        JPanel pwdPanel = new JPanel(new GridLayout(1,2));
        JTextField accountText = new JTextField();
        JTextField pwdText = new JTextField();
        loginTypeComboBox.addActionListener(e -> {
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
            uiPanel.updateUI();
            btnPanel.updateUI();
        });
        uiPanel.add(textPanel);
        uiPanel.add(btnPanel);

        loginBtn.addActionListener(e -> {
            LoginDto dto = null;
            switch (loginType) {
                case 0:
                    if (null == currentUser || StrUtil.isBlank(currentUser.getUuid())) {
                        AlertMessagesUtil.showErrorDialog(GAME_NAME, "当前未登录插件，请选择其他方式登录");
                        break;
                    }
                    dto = LoginDto.macLogin();
                    break;
                case 1:
                case 2:
                    AlertMessagesUtil.showInfoDialog(GAME_NAME, "当前类型未实现");
                    break;
                default:
                    AlertMessagesUtil.showErrorDialog(GAME_NAME, "登录类型异常");
                    break;
            }
            if (dto != null) {
                LoginDto finalDto = dto;
                invoke(() -> {
                    log.info("当前开始执行登录流程");
                    Result loginResult = HttpSendUtil.post(Const.SYS_LOGIN, finalDto);
                    log.info("当前登录返回结果 -{}", loginResult);
                    AlertMessagesUtil.showInfoDialog(GAME_NAME, gson.toJson(loginResult));
                    if (loginResult.getCode() == 200 ) {
                        DataCache.loginToken = String.format("Bearer %s", loginResult.getData().toString());
                        initGamePanel();
                    } else {
                        AlertMessagesUtil.showErrorDialog(GAME_NAME, loginResult.getMessage());
                    }
                });
            }
        });
        return uiPanel;
    }

    private void initGamePanel() {
        uiPanel.removeAll();
        PlayerDto dto = new PlayerDto(new Page<PlayerVo>());
        Page beforePage = HttpSendUtil.postResult(Const.PLAYER_GET_ALL, dto, Page.class);
        Page<PlayerVo> page = ResultUtil.convertPageData(beforePage, PlayerVo.class);
        List<PlayerVo> records = page.getRecords();
        uiPanel.setLayout(new GridLayout(3, 1));
        uiPanel.add(getTitleLabel());
        JPanel listPanel = new JPanel(new GridLayout(records.size(), 1));
        processScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        processScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JPanel playerPanel = null;
        JButton battleBtn = null;
        JLabel playerInfoLabel = null;
        processTextArea.append("展示数据");
        for (PlayerVo record : records) {
            playerPanel = new JPanel();
            playerInfoLabel = new JLabel(String.format("%d [%s][Lv %d][%s]",
                    records.indexOf(record) + 1,
                    record.getRegion(),
                    record.getLevel(),
                    record.getNickname()
            ));
            playerInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
            playerPanel.add(playerInfoLabel, BorderLayout.WEST);

            if (!StrUtil.equalsIgnoreCase(record.getMac(), currentUser.getUuid())) {
                battleBtn = new JButton("战斗");
                playerPanel.add(battleBtn, BorderLayout.EAST);
                battleBtn.addActionListener(e -> {
                    processTextArea.removeAll();
                    processTextArea.append("展示数据");
                    invoke(() -> {
                        Result processResult = HttpSendUtil.post(Const.BATTLE_DO, new BattleDto(currentUser.getUuid(), record.getMac()));
                        List<ProcessVo> list = ResultUtil.convertListData(processResult, ProcessVo.class);
                        list.forEach(System.out::println);
                        list.forEach(item -> {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                            processTextArea.append(String.format("%s \n", item.getProcess()));
                        });
                    });
                });
            }
            listPanel.add(playerPanel);
        }
        JScrollPane playerScroll = new JBScrollPane(listPanel);
        playerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        playerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        uiPanel.add(playerScroll);
        uiPanel.add(processScroll);
        uiPanel.updateUI();
        mainPanel.updateUI();
    }


    @Override
    protected void init() {
        log.info("当前初始化游戏面板");
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
        log.info("初始化完成");
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
