package cn.xeblog.plugin.game.dld;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.common.Page;
import cn.xeblog.plugin.game.dld.model.dto.*;
import cn.xeblog.plugin.game.dld.model.entity.InstanceNpc;
import cn.xeblog.plugin.game.dld.model.entity.Weapon;
import cn.xeblog.plugin.game.dld.model.vo.*;
import cn.xeblog.plugin.game.dld.ui.IKunUi;
import cn.xeblog.plugin.game.dld.ui.game.InstanceListTab;
import cn.xeblog.plugin.game.dld.ui.game.MasterGame;
import cn.xeblog.plugin.game.dld.ui.game.PlayerInfoTab;
import cn.xeblog.plugin.game.dld.ui.game.PvpTab;
import cn.xeblog.plugin.game.dld.ui.game.playerPackege.PackageDetailTab;
import cn.xeblog.plugin.game.dld.ui.game.playerPackege.PackageTab;
import cn.xeblog.plugin.game.dld.ui.login.AccountLogin;
import cn.xeblog.plugin.game.dld.ui.login.LoginFormUi;
import cn.xeblog.plugin.game.dld.ui.login.MacLogin;
import cn.xeblog.plugin.game.dld.utils.HttpSendUtil;
import cn.xeblog.plugin.game.dld.utils.ResultUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import cn.xeblog.plugin.util.NotifyUtils;
import com.google.common.collect.Lists;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.panels.VerticalBox;
import com.jgoodies.forms.layout.CellConstraints;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.taskdefs.condition.Not;
import org.yaml.snakeyaml.events.Event;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

import static cn.xeblog.plugin.game.dld.Const.*;

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
    /**
     * 切换用的tab页
     */
    private JTabbedPane tab;

    private VerticalBox box;

    private JTextArea fightArea;

    private JPanel playerArea;
    private JPanel npcArea;

    private final FlowLayout FLOW_LEFT_LAYOUT = new FlowLayout(FlowLayout.LEFT);

    //=====以下为各种UI=====
    private IKunUi iKunUi;

    private LoginFormUi loginFormUi;

    private MacLogin macLogin;

    private AccountLogin accountLogin;

    private MasterGame masterGame;

    private PvpTab pvpTab;

    private PlayerInfoTab playerInfoTab;

    private InstanceListTab instanceListTab;

    private PackageTab packageTab;

    /**
     * 详细的背包分类tab
     */
    private JTabbedPane packageTypeTab;

    /**
     * 详情tab
     */
    private PackageDetailTab detailTab;

    /**
     * 当前玩家
     */
    private PlayerVo currentPlayer;

    private String account;

    private String password;

    private Dimension maximumSize = new Dimension(800, 600);



    @Override
    protected void start() {
        log.info("开始了");
        initLoginForm();
        centerPanel.removeAll();
        box = new VerticalBox();
        box.setMaximumSize(maximumSize);
        box.add(loginFormUi.getLoginPanel());
        centerPanel.setPreferredSize(maximumSize);
        centerPanel.add(box);
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
        mainPanel.setPreferredSize(maximumSize);
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
                    initAccountLogin();
                    typePanel.add(accountLogin.getAccountLoginPanel());
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
        loadLoginAction();
    }

    private void initAccountLogin() {
        accountLogin = new AccountLogin();
        loginBtn = accountLogin.getLoginBtn();
        loadLoginAction();
    }

    private void initEmailLogin() {
        loadLoginAction();
    }

    /**
     * 加载登录action
     */
    private void loadLoginAction() {
        log.info("加载登录按钮事件");
        loginBtn.addActionListener(e -> {
            switch (loginType) {
                case 0:
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
    }

    /**
     * 初始化主游戏Tab面板
     */
    private void initMasterGame() {
        log.info("加载登录按钮事件完成");
        log.info("开始加载游戏主面板");
        masterGame = new MasterGame();
        tab = masterGame.getTab();
        loadPvpTab();
        JPanel gamePanel = masterGame.getGamePanel();

        tab.setPreferredSize(maximumSize);
        log.info("GameTabbed width {} ， height {}", tab.getWidth(), tab.getHeight());
        refreshCenterPanel(gamePanel);
        log.info("游戏面板加载完成");
        tab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tab.getSelectedIndex();
                switch (selectedIndex) {
                    case 0:
                        loadPvpTab();
                        break;
                    case 1:
                        loadInstanceTab();
                        break;
                    case 2:
                        loadPlayerInfoTab();
                        break;
                    case 3:
                        loadPackageTab();
                        break;
                    case 4:
                        loadBossTab();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * pvp Tab页
     */
    private void loadPvpTab() {
        tab.setSelectedIndex(0);
        JPanel pvpPanel = masterGame.getPvpPanel();
        pvpPanel.removeAll();
        pvpPanel.setLayout(FLOW_LEFT_LAYOUT);
        pvpTab = new PvpTab();
        playerArea = pvpTab.getPlayerArea();
        fightArea = pvpTab.getFightArea();
        JButton clearBtn = pvpTab.getClearBtn();
        clearBtn.addActionListener(e -> {
            fightArea.setText(Const.CLEAR_MSG);
            updateUI(tab, box, centerPanel);
        });
        JButton refreshBtn = pvpTab.getRefreshBtn();
        invoke(this::refreshPlayerList);
        refreshBtn.addActionListener(e -> invoke(this::refreshPlayerList));
        pvpPanel.add(pvpTab.getPvpPanel());
        updateUI(tab, pvpPanel);
    }

    /**
     * 副本tab
     */
    private void loadInstanceTab() {
        tab.setSelectedIndex(1);
        JPanel instancePanel = masterGame.getInstancePanel();
        instancePanel.removeAll();
        instancePanel.setLayout(FLOW_LEFT_LAYOUT);
        instanceListTab = new InstanceListTab();
        JPanel instanceListPanel = instanceListTab.getInstanceListPanel();
        invoke(() -> {
            Result result = HttpSendUtil.post(Const.INSTANCE_LIST, null);
            List<InstanceVo> instanceVoList = ResultUtil.convertListData(result, InstanceVo.class);
            instanceListPanel.setLayout(new GridLayout(instanceVoList.size(), 1));
            log.info("当前副本列表 {}", instanceVoList);
            JPanel itemPanel = null;
            JLabel itemLabel = null;
            JButton itemBtn = null;
            for (InstanceVo instance : instanceVoList) {
                itemPanel = new JPanel(FLOW_LEFT_LAYOUT);
                Integer accessLevel = instance.getAccessLevel();
                itemLabel = new JLabel(String.format("%d [%s] [%d级准入] [层数 %d]",
                        instanceVoList.indexOf(instance),
                        instance.getInstanceName(),
                        accessLevel,
                        instance.getFloorNum()));
                boolean accessFlag = currentPlayer.getLevel() >= accessLevel;
                itemLabel.setForeground(accessFlag ? Color.GREEN : Color.GRAY);
                itemBtn = new JButton("进入副本");
                if (!accessFlag) {
                    itemBtn.setEnabled(false);
                }
                itemBtn.addActionListener(e -> {
                    if (!accessFlag) {
                        NotifyUtils.error(GAME_NAME, "等级不足", true);
                    } else {
                        loadInstanceNpc(instance);
                    }
                });
                itemPanel.add(itemLabel);
                itemPanel.add(itemBtn);
                instanceListPanel.add(itemPanel);
            }
        });

        instancePanel.add(instanceListPanel);
        updateUI(tab, instancePanel);
    }

    /**
     * 玩家信息
     */
    private void loadPlayerInfoTab() {
        tab.setSelectedIndex(2);
        JPanel playerPanel = masterGame.getPlayerPanel();
        playerPanel.removeAll();
        playerInfoTab = new PlayerInfoTab();
        JLabel lvLabel = playerInfoTab.getLvLabel();
        JProgressBar lvProgress = playerInfoTab.getLvProgress();
        JLabel energyLabel = playerInfoTab.getEnergyLabel();
        JProgressBar energyProgress = playerInfoTab.getEnergyProgress();
        invoke(() -> {
            Result result = HttpSendUtil.post(Const.PLAYER_DETAIL, new PlayerDto());
            if (Const.ERROR_CODE.equals(result.getCode())) {
                NotifyUtils.error(GAME_NAME, result.toString(), false);
            } else {
                PlayerInfoVo playerInfoVo = ResultUtil.convertObjData(result, PlayerInfoVo.class);
                String lvTips = String.format("%d/%d", playerInfoVo.getExp(), playerInfoVo.getNextLvExp());
                lvLabel.setText(String.format("Lv:%d(%s)", playerInfoVo.getLevel(), lvTips));
                lvProgress.setMaximum(playerInfoVo.getNextLvExp());
                lvProgress.setValue(playerInfoVo.getExp());
                // 体力
                energyLabel.setText(String.format("体力: (%d/%d)", playerInfoVo.getEnergy(), playerInfoVo.getMaxEnergy()));
                energyProgress.setValue(playerInfoVo.getEnergy());
                energyProgress.setMaximum(playerInfoVo.getMaxEnergy());
                // 昵称
                playerInfoTab.getNickname().setText(String.format("昵称: %s", playerInfoVo.getNickname()));
                // 账号
                playerInfoTab.getAccount().setText(String.format("账号: %s", StrUtil.isBlank(playerInfoVo.getAccount()) ? "暂无" : playerInfoVo.getAccount()));

                // hp
                playerInfoTab.getHp().setText(String.format("生命: %d", playerInfoVo.getHp()));
                // 攻击
                playerInfoTab.getAttack().setText(String.format("攻击: %d", playerInfoVo.getAttack()));

                // 防御
                playerInfoTab.getDefender().setText(String.format("防御: %d", playerInfoVo.getDefence()));
                // 命中率
                playerInfoTab.getHit().setText(String.format("命中率: %.3f", playerInfoVo.getHitRate()));

                // 闪避
                playerInfoTab.getFlee().setText(String.format("闪避率: %.3f", playerInfoVo.getFlee()));
                // 连击
                playerInfoTab.getCombo().setText(String.format("连击率: %.3f", playerInfoVo.getComboRate()));
                // 速度
                playerInfoTab.getCritical().setText(String.format("暴击率:%.3f", playerInfoVo.getCriticalChance()));
                // 速度
                playerInfoTab.getSpeed().setText(String.format("速度:%d", playerInfoVo.getSpeed()));

            }
        });
        playerPanel.setLayout(FLOW_LEFT_LAYOUT);
        playerPanel.add(playerInfoTab.getInfoPanel());
    }

    /**
     * 背包
     */
    private void loadPackageTab() {
        tab.setSelectedIndex(3);
        JPanel packagePanel = masterGame.getPackagePanel();
        packagePanel.removeAll();
        packagePanel.setLayout(FLOW_LEFT_LAYOUT);
        packageTab= new PackageTab();
        packageTypeTab = packageTab.getTab();
        packageTypeTab.setSelectedIndex(1);
        JPanel packageTabPanel = packageTab.getPackageTabPanel();
        packagePanel.add(packageTabPanel);
        loadPlayerWeapon();
        packageTypeTab.addChangeListener(e -> {
            int packageType = packageTypeTab.getSelectedIndex();
            log.info("当前类型{}", packageType);
            switch (packageType) {
                case 1 :
                    loadPlayerWeapon();
                    break;
                default:
                    NotifyUtils.info(GAME_NAME, packageType + "功能没开发");
            }
        });
    }

    private void loadPlayerWeapon() {
        JPanel weaponPanel = packageTab.getWeaponPanel();
        weaponPanel.removeAll();
        detailTab = new PackageDetailTab();
        JPanel detailPanel = detailTab.getDetailPanel();
        detailPanel.setBorder(new LineBorder(JBColor.BLUE, 1));
        refreshPlayerWeapon();
        JTextArea detailArea = detailTab.getDetailArea();
        detailArea.setText("就是不给你看物品描述");
        weaponPanel.add(detailPanel, new CellConstraints());
        updateUI(weaponPanel);
    }

    private void refreshPlayerWeapon() {
        invoke(() -> {
            log.info("当前获取玩家武器开始");
            QueryWeaponDto dto = new QueryWeaponDto();
            dto.setPlayerId(currentPlayer.getId());
            Result result = HttpSendUtil.post(GET_ALL_WEAPON, dto);
            List<Weapon> weaponList = ResultUtil.convertListData(result, Weapon.class);
            List<List<Weapon>> partition = Lists.partition(weaponList, 5);
            JPanel propsPanel = detailTab.getPropsPanel();
            propsPanel.setLayout(new GridLayout(partition.size(), 5));
            JPanel detailRowPanel = null;
            JPanel detailPanel = null;
            JLabel detailLabel = null;
            for (List<Weapon> weapons : partition) {
                detailRowPanel = new JPanel(new GridLayout(1, 5));
                for (Weapon weapon : weapons) {
                    detailPanel = new JPanel();
                    detailPanel.setBorder(new LineBorder(JBColor.RED, 1));
                    detailLabel = new JLabel(weapon.getName());
                    detailPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            NotifyUtils.info(GAME_NAME, "点击了" + weapon.getName());
                        }
                    });
                    detailPanel.add(detailLabel, BorderLayout.CENTER);
                    detailRowPanel.add(detailPanel);
                }
                propsPanel.add(detailRowPanel);
            }

            log.info("当前的数据 {}", partition);
        });
    }

    /**
     * boss
     */
    private void loadBossTab() {

    }

    private void loadInstanceNpc(InstanceVo instance) {
        JPanel instancePanel = masterGame.getInstancePanel();
        instancePanel.removeAll();
        instancePanel.setLayout(FLOW_LEFT_LAYOUT);
        pvpTab = new PvpTab();
        playerArea = pvpTab.getPlayerArea();
        fightArea = pvpTab.getFightArea();
        JButton clearBtn = pvpTab.getClearBtn();
        clearBtn.addActionListener(e -> {
            fightArea.setText(Const.CLEAR_MSG);
            updateUI(tab, box, centerPanel);
        });
        JButton refreshBtn = pvpTab.getRefreshBtn();
        invoke(() -> refreshInstanceNpcList(instance));
        refreshBtn.addActionListener(e -> invoke(() -> refreshInstanceNpcList(instance)));
        instancePanel.add(pvpTab.getPvpPanel());
        updateUI(tab, instancePanel);
    }

    private void refreshInstanceNpcList(InstanceVo instance) {
        Result result = HttpSendUtil.post(INSTANCE_JOIN, new JoinInstanceDto(currentPlayer.getId(), instance.getId()));
        ChallengeInstanceVo challengeInstanceVo = ResultUtil.convertObjData(result, ChallengeInstanceVo.class);
        log.info("返回结果 {}", challengeInstanceVo);

        if (Const.ERROR_CODE.equals(result.getCode())) {
            fightArea.setText(result.toString());
        } else {
            // 防止出现战斗后列表紊乱的问题
            playerArea.removeAll();
            List<InstanceNpc> npcList = challengeInstanceVo.getNpcList();
            JPanel playerPanel = null;
            JButton battleBtn = null;
            JLabel playerInfoLabel = null;
            int size = npcList.size();
            playerArea.setLayout(new GridLayout(size, 1));
            pvpTab.getPlayerLabel().setText("NPC列表");
            Integer currentFloor = challengeInstanceVo.getRecord().getCurrentFloor();
            for (InstanceNpc instanceNpc : npcList) {
                playerPanel = new JPanel(FLOW_LEFT_LAYOUT);
                playerInfoLabel = new JLabel(String.format("[第%d层] [%s]", instanceNpc.getFloor(), instanceNpc.getNpcName()));
                boolean showBtnFlag = currentFloor.equals(instanceNpc.getFloor());
                Color labelColor = Color.GRAY;
                if (showBtnFlag) {
                    labelColor = Color.GREEN;
                }
                if (instanceNpc.getBossFlag()) {
                    labelColor = Color.RED;
                }
                playerInfoLabel.setForeground(labelColor);
                playerInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
                playerPanel.add(playerInfoLabel);

                if (showBtnFlag) {
                    battleBtn = new JButton("战斗");
                    playerPanel.add(battleBtn);
                    battleBtn.addActionListener(e -> {
                        fightArea.setText(CLEAR_MSG);
                        if (challengeInstanceVo.getRecord().getCompleteFlag()) {
                            fightArea.setText(INSTANCE_OVER_MSG);
                            NotifyUtils.info(GAME_NAME, INSTANCE_OVER_MSG, false);
                            JButton refreshBtn = pvpTab.getRefreshBtn();
                            refreshBtn.setText("返回列表");
                            refreshBtn.addActionListener(re -> invoke(this::loadInstanceTab));
                            updateUI(refreshBtn, tab, box, centerPanel);
                            return;
                        }
                        invoke(() -> {
                            Result processResult = HttpSendUtil.post(NPC_CHALLENGE, new NpcFightDto(currentPlayer.getId(), instanceNpc));
                            BattleResult battleResult = ResultUtil.convertBattleResult(processResult, ProcessVo.class);
                            battleResult.getProcessList().forEach(this::addFightProcess);

                            playerArea.removeAll();
                            updateUI(playerArea);
                            refreshInstanceNpcList(instance);
                        });
                    });
                }
                playerArea.add(playerPanel, BorderLayout.WEST);
            }
            updateUI(playerArea, tab, box, centerPanel);
        }
    }

    private void updateUI(JComponent... panels) {
        Arrays.asList(panels).forEach(JComponent::updateUI);
    }

    private void refreshPlayerList() {
        PlayerDto dto = new PlayerDto(new Page<PlayerVo>());
        Result result = HttpSendUtil.post(Const.PLAYER_GET_ALL, dto);
        Object data = result.getData();
        if (Const.ERROR_CODE.equals(result.getCode())) {
            fightArea.setText(result.toString());
        } else {
            // 防止出现战斗后列表紊乱的问题
            playerArea.removeAll();
            Page<PlayerVo> playerPage = ResultUtil.convertPageData(data, PlayerVo.class);
            List<PlayerVo> records = playerPage.getRecords();
            JPanel playerPanel = null;
            JButton battleBtn = null;
            JLabel playerInfoLabel = null;
            User currentUser = DataCache.getCurrentUser();
            int size = records.size();
            log.info("当前人数{}", size);
            playerArea.setLayout(new GridLayout(size, 1));
            long onlineCount = records.stream()
                    .filter(PlayerVo::getOnline)
                    .count();
            pvpTab.getPlayerLabel().setText(String.format("玩家列表(%d/%d)", onlineCount, size));
            for (PlayerVo record : records) {
                playerPanel = new JPanel(FLOW_LEFT_LAYOUT);
                playerInfoLabel = new JLabel(String.format("%d [%s][Lv %d][%s]",
                        records.indexOf(record) + 1,
                        record.getRegion(),
                        record.getLevel(),
                        record.getNickname()
                ));
                playerInfoLabel.setForeground(record.getOnline() ? Color.GREEN : Color.GRAY);
                playerInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
                playerPanel.add(playerInfoLabel);

                if (!StrUtil.equalsIgnoreCase(record.getMac(), currentUser.getUuid())) {
                    battleBtn = new JButton("挑战");
                    playerPanel.add(battleBtn);
                    battleBtn.addActionListener(e -> {
                        fightArea.setText(CLEAR_MSG);
                        invoke(() -> {
                            Result processResult = HttpSendUtil.post(Const.BATTLE_DO, new BattleDto(record.getMac()));
                            BattleResult battleResult = ResultUtil.convertBattleResult(processResult, null);
                            List<ProcessVo> list = battleResult.getProcessList();
                            list.forEach(System.out::println);
                            list.forEach(this::addFightProcess);
                            refreshPlayerList();
                            if (battleResult.getSuccess()) {
                                boolean confirmResult = AlertMessagesUtil.showYesNoDialog(GAME_NAME, String.format("恭喜你战胜了【%s】，是否发送鱼塘嘲讽", record.getNickname()));
                                if (confirmResult) {
                                    log.info("你嘲讽了 {}", record.getNickname());
                                    HttpSendUtil.post(BATTLE_TAUNT, new TauntDto(currentUser.getUuid(), record.getMac()));
                                }
                            }
                        });
                    });
                } else {
                    currentPlayer = record;
                }
                playerArea.add(playerPanel, BorderLayout.WEST);
            }
            updateUI(playerArea, tab, box, centerPanel);
        }
    }

    private void addFightProcess(ProcessVo process) {
        String processStr = process.getProcess();
        for (int i = 0; i < processStr.length(); i += COLUMN_NUM) {
            int end = Math.min(i + COLUMN_NUM, processStr.length());
            String chunk = processStr.substring(i, end);
            fightArea.append(String.format("\n%s", chunk));
        }
    }

    /**
     * 刷新centerPanel
     *
     * @param panel
     */
    private void refreshCenterPanel(JPanel panel) {
        centerPanel.removeAll();
        box.removeAll();
        box.add(panel);
        centerPanel.add(box);
        centerPanel.updateUI();
    }

    private void loginByMac() {
        User currentUser = DataCache.getCurrentUser();
        if (currentUser == null || StrUtil.isBlank(currentUser.getUuid())) {
            AlertMessagesUtil.showErrorDialog(GAME_NAME, "请先登录插件再进行游戏");
            // TODO: 2023/10/24 自动执行 #over 9
            return;
        } else {
            LoginDto macLoginDto = LoginDto.macLogin();
            invoke(() -> {
                log.info("当前开始执行登录流程 地址 {}", Const.SYS_LOGIN);
                Result loginResult = HttpSendUtil.post(Const.SYS_LOGIN, macLoginDto);
                log.info("当前登录返回结果 -{}", loginResult);
                if (loginResult.getCode() == 200) {
                    DataCache.loginToken = String.format("Bearer %s", loginResult.getData().toString());
                    initMasterGame();
                } else {
                    AlertMessagesUtil.showErrorDialog(GAME_NAME, loginResult.getMessage());
                }
            });
        }
    }

    private void loginByAccount() {
        String accountStr = accountLogin.getAccount().getText();
        String pwdStr = accountLogin.getPassword().getText();
        if (StrUtil.isBlank(accountStr)) {
            NotifyUtils.error(GAME_NAME, "账号不允许为空");
            return;
        }

        if (StrUtil.isBlank(pwdStr)) {
            NotifyUtils.error(GAME_NAME, "密码不允许为空");
            return;
        }
        LoginDto accountDto = LoginDto.accountLogin(accountStr, pwdStr);
        invoke(() -> {
            log.info("当前开始执行登录流程 地址 {}", Const.SYS_LOGIN);
            Result loginResult = HttpSendUtil.post(Const.SYS_LOGIN, accountDto);
            log.info("当前登录返回结果 -{}", loginResult);
            if (loginResult.getCode() == 200) {
                DataCache.loginToken = String.format("Bearer %s", loginResult.getData().toString());
                initMasterGame();
            } else {
                AlertMessagesUtil.showErrorDialog(GAME_NAME, loginResult.getMessage());
            }
        });
    }

    private void loginByEmail() {
        AlertMessagesUtil.showInfoDialog(GAME_NAME, "功能暂未开发，请选择MAC登录");
    }
}
