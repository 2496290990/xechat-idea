package cn.xeblog.plugin.game.dld;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.dto.LoginDto;
import cn.xeblog.plugin.game.dld.utils.HttpSendUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

import static cn.xeblog.plugin.game.dld.Const.GAME_NAME;

/**
 * @author eleven
 * @date 2023/10/24 12:27
 * @apiNote
 */
@DoGame(Game.IKUN)
@Slf4j
public class IKunDldXl extends AbstractGame {

    /**
     * 主面板
     */
    private JPanel mainPanel;

    private JPanel centerPanel;

    private JPanel footerPanel;

    private JButton loginBtn;

    private Integer loginType = 0;

    //=====以下为各种UI=====
    private IKunUi iKunUi;

    private LoginFormUi loginFormUi;

    private MacLogin macLogin;


    private Gson gson = new Gson();
    @Override
    protected void start() {
        log.info("开始了");
        initLoginForm();
        centerPanel.removeAll();
        centerPanel.add(loginFormUi.getLoginPanel());
        centerPanel.updateUI();
        log.info("结束了");
    }


    @Override
    protected void init() {
        log.info("当前初始化游戏面板");
        iKunUi = new IKunUi(GAME_NAME);
        if (mainPanel == null) {
            mainPanel = iKunUi.getMainPanel();
            centerPanel = iKunUi.getCenterPanel();
            footerPanel = iKunUi.getFooterPanel();
        }
        mainPanel.setMinimumSize(new Dimension(150, 200));
        footerPanel.add(getStartGameButton());
        footerPanel.add(getExitButton());
        mainPanel.updateUI();
        log.info("初始化完成");
    }

    @Override
    protected JPanel getComponent() {
        if (iKunUi == null) {
            iKunUi = new IKunUi(GAME_NAME);
        }
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

    /**
     * 初始化登录
     */
    private void initLoginForm() {
        loginFormUi = new LoginFormUi();
        JComboBox<String> typeComboBox = loginFormUi.getTypeComboBox();
        JPanel typePanel = loginFormUi.getTypePanel();
        typeComboBox.addItem("MAC自动登录");
        typeComboBox.addItem("账号密码登录");
        typeComboBox.addItem("邮箱验证码登录");
        initMacLogin();
        typePanel.add(macLogin.getMacPanel());
        typeComboBox.addActionListener(e -> {
            loginType = typeComboBox.getSelectedIndex();
            log.info("当前选择登录类型 - {}", loginType);
            typePanel.removeAll();
            switch (loginType) {
                case 0:
                    typePanel.add(macLogin.getMacPanel());
                    break;
                case 1:
                case 2:
                default:
                    break;
            }
            typePanel.updateUI();
            log.info("登录类型UI刷新成功");
        });
        loadLoginAction();
    }

    private void initMacLogin() {
        macLogin = new MacLogin();
        loginBtn = macLogin.getLoginBtn();
    }

    /**
     * 加载登录action
     */
    private void loadLoginAction() {
        log.info("加载登录按钮事件");
        loginBtn.addActionListener(e -> {
            switch (loginType) {
                case 0 :
                    loginByMac();
                    break;
                case 1:
                    loginByAccount();
                    break;
                case 2:
                    loginByEmail();
                    break;
                default:
                    AlertMessagesUtil.showInfoDialog(GAME_NAME, "请选择MAC登录");
                    break;
            }

        });
        log.info("加载登录按钮事件完成");
    }

    private void loginByMac() {
        User currentUser = DataCache.getCurrentUser();
        if (currentUser== null || StrUtil.isBlank(currentUser.getUuid())) {
            AlertMessagesUtil.showErrorDialog(GAME_NAME, "请先登录插件再进行游戏");
            // TODO: 2023/10/24 自动执行 #over 9
            return;
        } else {
            LoginDto macLoginDto = LoginDto.macLogin();
            invoke(() -> {
                log.info("当前开始执行登录流程");
                Result loginResult = HttpSendUtil.post(Const.SYS_LOGIN, macLoginDto);
                log.info("当前登录返回结果 -{}", loginResult);
                AlertMessagesUtil.showInfoDialog(GAME_NAME, gson.toJson(loginResult));
                if (loginResult.getCode() == 200 ) {
                    DataCache.loginToken = String.format("Bearer %s", loginResult.getData().toString());
                } else {
                    AlertMessagesUtil.showErrorDialog(GAME_NAME, loginResult.getMessage());
                }
            });
        }
    }

    private void loginByAccount() {
        AlertMessagesUtil.showInfoDialog(GAME_NAME, "功能暂未开发，请选择MAC登录");
    }

    private void loginByEmail() {
        AlertMessagesUtil.showInfoDialog(GAME_NAME, "功能暂未开发，请选择MAC登录");
    }
}
