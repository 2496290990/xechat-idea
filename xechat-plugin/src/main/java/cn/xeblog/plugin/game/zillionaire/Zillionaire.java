package cn.xeblog.plugin.game.zillionaire;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.zillionaire.dto.*;
import cn.xeblog.commons.entity.game.zillionaire.enums.MsgType;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.zillionaire.action.AiPlayerAction;
import cn.xeblog.plugin.game.zillionaire.action.PlayerAction;
import cn.xeblog.plugin.game.zillionaire.dto.Player;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;
import cn.xeblog.plugin.game.zillionaire.enums.GameMode;
import cn.xeblog.plugin.game.zillionaire.enums.WindowMode;
import cn.xeblog.plugin.game.zillionaire.ui.PlayerUI;
import cn.xeblog.plugin.game.zillionaire.ui.PositionUi;
import cn.xeblog.plugin.game.zillionaire.utils.CalcUtil;
import cn.xeblog.plugin.game.zillionaire.utils.ZillionaireUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.xeblog.commons.entity.game.zillionaire.enums.MsgType.*;


/**
 * @author eleven
 * @date 2023/3/20 11:20
 * @apiNote 大富翁
 */
@DoGame(Game.ZILLIONAIRE)
@Slf4j
public class Zillionaire extends AbstractGame<MonopolyGameDto> {
    public static WindowMode windowMode;

    /**
     * 开始
     */
    private JPanel startPanel;
    /**
     * 游戏结束按钮
     */
    private JButton gameOverButton;
    /**
     * 标题
     */
    private JLabel titleLabel;
    /**
     * 返回游戏
     */
    private JButton backButton;
    /**
     * 游戏说明
     */
    private JButton helpBtn;
    /**
     * 作弊码
     */
    private JTextField cheatCode;
    /**
     * 作弊码按钮
     */
    private JButton cheatCodeBtn;
    // 随机数标签
    private JLabel randomLabel = new JLabel();
    /**
     * 提示标签
     */
    private JTextArea tipsArea;
    /**
     * 位置主面板
     */
    private JPanel positionMainPanel;

    private JPanel userListPanel;
    /**
     * 投掷骰子
     */
    JButton randomBtn;
    /**
     * 升级建筑
     */
    JButton buildBtn;
    /**
     * 购买地皮
     */
    JButton buyBtn;
    /**
     * 售卖
     */
    JButton saleBtn;
    /**
     * 赎回
     */
    JButton redemptionBtn;
    /**
     * 过按钮
     */
    JButton passBtn;

    private Integer tipsRows = 10;
    /**
     * 当前玩家
     */
    private PlayerNode currentPlayer;

    /**
     * 玩家键值对
     */
    private Map<String, Player> playerMap;
    /**
     * 位置点位集合
     */
    private Map<Integer, PositionDto> positionMap;
    /**
     * 用户列表
     */
    private List<String> userList;
    /**
     * ai玩家列表
     */
    private static List<String> aiPlayerList;
    /**
     * ai处理类
     */
    private Map<String, PlayerAction> aiPlayerActionMap;

    private PlayerAction helpPlayerAction;
    /**
     * 总步数 40
     */
    private static final Integer ALL_STEP = 40;
    /**
     * 经过起点的奖金
     */
    private Integer startMoney = 2000;
    /**
     * 所得税
     */
    private Integer incomeTax = 2000;
    /**
     * 财产税
     */
    private Integer propertyTax = 1000;
    /**
     * 旅馆扣费
     */
    private Integer hotelPenalty = 600;
    /**
     * 房屋扣费
     */
    private Integer housePenalty = 200;
    /**
     * 出狱许可证玩家
     */
    private String outJailLicence;
    /**
     * 临时操作提权的玩家
     */
    private List<String> tempPlayerList;
    /**
     * Ai思考时长
     */
    private static final long aiThinkingTime = 500L;
    /**
     * 所有人重新投掷结果
     */
    private ConcurrentHashMap<String, Integer> againResultMap;
    /**
     * 是否选择升级
     */
    private Boolean chooseBuild;
    /**
     * 是否拆除
     */
    private Boolean pullDown;

    private PositionDto choosePosition;

    static {
        aiPlayerList = new ArrayList<>();
        Collections.addAll(aiPlayerList,
                "AI: 叶凡",
                "AI: 萧炎",
                "AI: 罗峰",
                "AI: 唐三",
                "AI: 陈平安",
                "AI: 寂寞",
                "AI: 迪迦",
                "AI: 阿古茹",
                "AI: 盖亚",
                "AI: 泰罗",
                "AI: 坚果",
                "AI: 林动"
        );
    }

    /**
     * 当前游戏模式
     */
    private GameMode gameMode;
    /**
     * 游戏状态 -1结束 0初始化 1游戏中 2玩家正在出售房产  3玩家正在思考免费升级房屋 4玩家在思考拆除房屋
     */
    private Integer status;

    @Override
    protected void init() {
        initStartPanel();
    }

    Font font = new Font("", 0, 10);

    private void initStartPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(250, 400));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 260);
        mainPanel.add(startPanel);

        // 添加游戏标题
        JLabel title = new JLabel("大富翁！");
        title.setFont(font);
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        JLabel modelLabel = new JLabel("游戏模式：");
        modelLabel.setFont(font);
        vBox.add(modelLabel);

        vBox.add(Box.createVerticalStrut(5));
        ComboBox gameModeBox = new ComboBox();
        gameModeBox.setPreferredSize(new Dimension(40, 30));
        for (GameMode value : GameMode.values()) {
            gameModeBox.addItem(value.getName());
        }

        gameMode = GameMode.BROKE_EXIT;
        gameModeBox.setSelectedItem(gameMode.getName());
        gameModeBox.addActionListener(l -> {
            GameMode selectedGameMode = GameMode.getMode(gameModeBox.getSelectedItem().toString());
            if (selectedGameMode != null) {
                gameMode = selectedGameMode;
            }
        });
        vBox.add(gameModeBox);
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(getStartGameButton());

        if (DataCache.isOnline) {
            List<Integer> numsList = new ArrayList();
            Collections.addAll(numsList, 2, 3, 4, 5, 6);

            List<String> gameModeList = new ArrayList<>();
            for (GameMode mode : GameMode.values()) {
                gameModeList.add(mode.getName());
            }

            vBox.add(getCreateRoomButton(numsList, gameModeList));
        }
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    private void initValue() {
        userList = new ArrayList<>();
        status = 0;
        currentPlayer = null;
        playerMap = null;
        userList = new ArrayList<>();
        aiPlayerActionMap = new HashMap<>();
        userListPanel = new JPanel();
        positionMainPanel = new JPanel();
        outJailLicence = null;
        tempPlayerList = new ArrayList<>();
        againResultMap = new ConcurrentHashMap<>();
        chooseBuild = false;
        pullDown = false;
    }

    private void initBtnStatus() {
        String nickname = GameAction.getNickname();
        // 获取当前玩家
        Player player = playerMap.get(nickname);
        PlayerNode playerNode = player.getPlayerNode();
        boolean actionFlag = StrUtil.equalsIgnoreCase(nickname, homeOwnerName());
        // 检测玩家是否有售出地皮
        List<PositionDto> collect = playerNode.getPositions().stream()
                .filter(item -> !item.getPositionStatus())
                .collect(Collectors.toList());
        long count = collect.size();
        // 当前点位是否有拥有者
        refreshBtnStatus(actionFlag, false, false, false, count > 0, false);
    }

    /**
     * 刷新按钮状态
     *
     * @param randomBtnStatus     投掷骰子按钮状态
     * @param buyBtnStatus        购买地皮按钮状态
     * @param buildBtnStatus      升级建筑按钮状态
     * @param saleBtnStatus       售卖建筑按钮状态
     * @param redemptionBtnStatus 赎回地皮按钮状态
     * @param passBtnStatus       过按钮状态
     */
    private void refreshBtnStatus(Boolean randomBtnStatus, Boolean buyBtnStatus, Boolean buildBtnStatus,
                                  Boolean saleBtnStatus, Boolean redemptionBtnStatus, Boolean passBtnStatus) {
        // 如果当前玩家是操作玩家的话
        if (currentPlayer != null && StrUtil.equalsIgnoreCase(GameAction.getNickname(), currentPlayer.getPlayer())) {
            randomBtn.setEnabled(null == randomBtnStatus ? randomBtn.isEnabled() : randomBtnStatus);
            buyBtn.setEnabled(null == buyBtnStatus ? buyBtn.isEnabled() : buyBtnStatus);
            buildBtn.setEnabled(null == buildBtnStatus ? buildBtn.isEnabled() : buildBtnStatus);
            saleBtn.setEnabled(null == saleBtnStatus ? saleBtn.isEnabled() : saleBtnStatus);
            redemptionBtn.setEnabled(null == redemptionBtnStatus ? redemptionBtn.isEnabled() : redemptionBtnStatus);
            passBtn.setEnabled(null == passBtnStatus ? passBtn.isEnabled() : passBtnStatus);
        } else {
            randomBtn.setEnabled(false);
            buyBtn.setEnabled(false);
            buildBtn.setEnabled(false);
            saleBtn.setEnabled(false);
            redemptionBtn.setEnabled(false);
            passBtn.setEnabled(false);
        }
    }

    /**
     * 刷新单个按钮的状态
     *
     * @param btn       按钮
     * @param btnStatus 按钮状态
     */
    private void refreshBtnStatus(JButton btn, Boolean btnStatus) {
        btn.setEnabled(null == btnStatus ? btn.isEnabled() : btnStatus);
    }

    /**
     * 获取开始游戏按钮
     *
     * @return JButton  返回开始游戏按钮
     */
    private JButton getStartGameButton() {
        JButton button = new JButton("开始游戏");
        button.addActionListener(e -> {
            button.setEnabled(false);
            invoke(() -> {
                setHomeowner(true);
                start();
                button.setEnabled(true);
            }, 100);
        });
        return button;
    }

    /**
     * 获取返回游戏按钮
     */
    private JButton getBackButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

    @Override
    protected void allPlayersGameStarted() {
        if (isHomeowner()) {
            int usersTotal = userList.size();
            // 指定最少三名玩家游玩
            int roomUsers = 3;
            GameRoom room = this.getRoom();
            if (null != room) {
                roomUsers = room.getNums();
            }
            // 最少三名玩家
            int nums = roomUsers - usersTotal;
            invoke(() -> {
                if (nums > 0) {
                    List<String> joinedAIList = new ArrayList<>(aiPlayerList);
                    joinedAIList.removeAll(userList);
                    // 对AI随机洗牌
                    Collections.shuffle(joinedAIList);
                    List<String> aiList = joinedAIList.subList(0, nums);
                    aiList.forEach(ai -> aiPlayerActionMap.put(ai, null));
                    sendMsg(JOIN_ROBOTS, GameAction.getNickname(), new ArrayList<>(aiList));
                }
                showGamePanel();
            }, 500);
        }
    }

    @Override
    protected void start() {
        initValue();
        GameRoom gameRoom = getRoom();
        if (gameRoom != null) {
            gameMode = GameMode.getMode(gameRoom.getGameMode());
            userList.addAll(gameRoom.getUsers().keySet());
        } else {
            userList.add(GameAction.getNickname());
        }

        buildPlayerNode();
        showGamePanel();
        status = 1;
        if (gameRoom == null) {
            allPlayersGameStarted();
        }
    }

    private void showGamePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        // 490 360
        mainPanel.setMinimumSize(new Dimension(350, 330));

        JPanel panel = new JPanel();
        // 480 300
        panel.setPreferredSize(new Dimension(330, 300));
        panel.setLayout(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel();
        // 400 30
        topPanel.setPreferredSize(new Dimension(330, 30));
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(60, 280));
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(60, 280));
        JPanel bottomPanel = new JPanel();
        // 400 30
        bottomPanel.setPreferredSize(new Dimension(330, 30));
        JPanel centerPanel = new JPanel();
        // 280 280
        centerPanel.setPreferredSize(new Dimension(200, 180));
        // 初始化游戏区域
        initPlayAreaCenterPanel(centerPanel);
        initPlayAreaRightPanel(rightPanel);
        initPlayAreaBottomPanel(bottomPanel);
        initPlayAreaLeftPanel(leftPanel);
        initPlayAreaTopPanel(topPanel);
        //添加按钮等
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(rightPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(leftPanel, BorderLayout.WEST);


        JPanel mainTopPanel = new JPanel();
        String title = "大富翁！";
        titleLabel = new JLabel(title);
        titleLabel.setFont(font);
        mainTopPanel.add(titleLabel);

        JPanel mainBottomPanel = new JPanel();
        backButton = getBackButton();
        helpBtn = new JButton("帮助说明");
        helpBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "游戏规则:\n" +
                            "1. 每次开始的时候先投骰子，然后移动玩家位置。\n" +
                            "2. 如果到达位置的地皮没有人买则可以买地皮，如果是自己的地皮就可以盖房子，别人的地皮就需要给她钱 \n" +
                            "3. 机会和命运需要抽卡牌，财产税和所得税会扣钱\n" +
                            "4. 到达起点的时候就会拿到2000块\n" +
                            "5. 坐牢会直接移动到监狱位置，并且拿不到起点的2000块，入狱会休息一轮\n" +
                            "6. 停车场也会休息一轮,路过监狱不会休息\n" +
                            "8. 现金不够的时候可以卖房卖地，半价出售\n" +
                            "9. 如果一名玩家拥有同颜色的所有地皮则该颜色的空地过路费翻倍\n" +
                            "10. 经过自来水公司和电力公司需要投掷骰子，一个公司则点数*10 两个则点数*100\n",
                    "游戏提示",
                    JOptionPane.YES_OPTION

            );
        });
        cheatCode = new JTextField();
        cheatCodeBtn = new JButton("激活作弊码");
        cheatCodeBtn.addActionListener(e -> {
            String cheatCodeStr = cheatCode.getText();
            if (StrUtil.isNotBlank(cheatCodeStr)) {
                if (StrUtil.equalsIgnoreCase("↑↑↓↓←←→→BABA", cheatCodeStr)) {
                    alertGameMessage("你这不对劲哦");
                }

                if (StrUtil.equalsIgnoreCase("PANZER", cheatCodeStr)) {
                    for (String playerName : playerMap.keySet()) {
                        Player player = playerMap.get(playerName);
                        PlayerNode playerNode = player.getPlayerNode();
                        playerNode.upgradeCashAndProperty(5000, 5000, true);
                        player.setPlayerNode(playerNode);
                        playerMap.put(playerName, player);
                        player.refreshTips(positionMap.get(playerNode.getPosition()));
                    }
                }

                if (StrUtil.equalsIgnoreCase("ALLIN", cheatCodeStr)) {
                    alertGameMessage("想啥呢小伙子，还梭哈");
                }

                if (cheatCodeStr.contains("clean cash")) {
                    alertGameMessage("过于影响游戏平衡暂未开发");
                }

                if (cheatCodeStr.contains("clean property")) {
                    alertGameMessage("过于影响游戏平衡暂未开发");
                }
                if (cheatCodeStr.contains("Occupy the house")) {
                    sendRefreshTipsMsg(GameAction.getName(), GameAction.getNickname() + "妄想随机清空一位玩家的房产，获得警告一次");
                    alertGameMessage("警告一次！");
                }
            }
        });

        gameOverButton = getGameOverButton();
        gameOverButton.setVisible(false);

        Box hBox = Box.createHorizontalBox();
        hBox.add(new JLabel("Window: "));
        hBox.add(getWindowModeComboBox());
        hBox.add(Box.createHorizontalStrut(5));
        addAll(mainBottomPanel, helpBtn, cheatCode, cheatCodeBtn, backButton, gameOverButton, hBox);

        mainPanel.add(mainTopPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(mainBottomPanel, BorderLayout.SOUTH);
        //mainPanel.add(initUserPanel(), BorderLayout.EAST);
        mainPanel.add(initUserPanelUI(), BorderLayout.EAST);
        mainPanel.updateUI();
        if (isHomeowner()) {
            refreshBtnStatus(true,false, false, false, false, false);
        } else {
            refreshBtnStatus(false,false, false, false, false, false);
        }
    }

    private void alertGameMessage(String str) {
        JOptionPane.showMessageDialog(null, str);
    }

    /**
     * 获取卡牌
     *
     * @param form 开始
     * @param to   结束
     * @return List    坐标点
     */
    private List<PositionUi> getPositions(Integer form, Integer to) {
        List<PositionDto> positions = ZillionaireUtil.positionDtoList;
        positionMap = positions.stream()
                .collect(Collectors.toMap(PositionDto::getPosition, p -> p));
        List<PositionDto> subPositions = positions.subList(form, to);
        PositionUi positionUi;
        CityDto city;
        StationDto station;
        List<PositionUi> positionUis = new ArrayList<>(to - form);
        for (PositionDto subPosition : subPositions) {
            if (subPosition instanceof CityDto) {
                city = (CityDto) subPosition;
                positionUi = new PositionUi(subPosition.getPosition(), subPosition.getName(), subPosition.getOwner(),
                        city.getLevel(), city.getPrice(), city.getBuildMoney(), city.getToll(), city.getColor());
            } else if (subPosition instanceof StationDto) {
                station = (StationDto) subPosition;
                positionUi = new PositionUi(subPosition.getPosition(), subPosition.getName(), subPosition.getOwner(),
                        station.getLevel(), station.getPrice(), 0, station.getToll(), station.getColor());
            } else {
                positionUi = new PositionUi(subPosition.getPosition(), subPosition.getName(), subPosition.getColor());
            }
            positionUis.add(positionUi);
        }
        return positionUis;
    }

    /**
     * 加载游戏区域头部地区
     *
     * @param jPanel JPanel 组件
     */
    private void initPlayAreaTopPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(1, 11));
        initPositionUi(jPanel, 0, 11, false);
    }

    private void initPlayAreaRightPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(9, 1));
        initPositionUi(jPanel, 11, 20, false);
    }

    private void initPlayAreaBottomPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(1, 11));
        initPositionUi(jPanel, 20, 31, true);
    }

    private void initPlayAreaLeftPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(9, 1));
        initPositionUi(jPanel, 31, 40, true);
    }
    private String homeOwnerName(){
        return currentPlayer == null ?
                GameAction.getAction().getRoom().getHomeowner().getUsername() :
                currentPlayer.getPlayer();
    }
    private void initPlayAreaCenterPanel(JPanel centerPanel) {
        centerPanel.setLayout(new GridLayout(4, 1));
        JPanel textPanel = new JPanel();
        //textPanel.setPreferredSize(new Dimension(300, 80));
        tipsArea = new JTextArea();
        tipsArea.append("游戏开始\n");
        tipsArea.append(String.format("当前操作玩家%s\n", homeOwnerName()));
        tipsArea.setRows(10);
        tipsArea.setColumns(50);
        tipsArea.setLineWrap(true);
        tipsArea.setPreferredSize(new Dimension(180, 80));
        JBScrollPane scrollPane = new JBScrollPane(tipsArea);//创建滚动条面板
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        //scrollPane.setBounds(0,0,300,80);
        textPanel.add(scrollPane);

        JBScrollPane positionScroll = new JBScrollPane(positionMainPanel);
        positionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        positionScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        centerPanel.setBorder(new LineBorder(JBColor.ORANGE, 1));
        addAll(centerPanel, new JScrollPane(tipsArea), positionScroll, randomLabel, centerGameButton());
    }

    private JPanel centerGameButton() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 6));
        randomBtn = new JButton("投掷");
        buildBtn = new JButton("升级");
        buyBtn = new JButton("购买");
        saleBtn = new JButton("售卖");
        redemptionBtn = new JButton("赎回");
        passBtn = new JButton("过");
        addAll(buttonPanel, randomBtn, buildBtn, buyBtn, saleBtn, redemptionBtn, passBtn);

        String nickname = GameAction.getNickname();
        randomBtn.addActionListener(e -> {
            int randomInt = RandomUtil.randomInt(2, 12);
            String text = cheatCode.getText();
            text = text.replaceAll("[^0-9]", "");
            log.info("随机数 {}", text);
            if (StrUtil.isNotBlank(text)) {
                try {
                    randomInt = Integer.parseInt(text);
                } catch (Exception ignored) {

                }
            }
            randomLabel.setText("当前投掷点数为: " + randomInt);
            sendMsg(DICE_ROLL, nickname, randomInt);
        });
        buildBtn.addActionListener(e -> {
            Player player = playerMap.get(nickname);
            PlayerNode playerNode = player.getPlayerNode();
            if (chooseBuild) {
                if (choosePosition == null) {
                    alertGameMessage("请选择一块地皮升级");
                    return;
                }

                if (!(choosePosition instanceof CityDto)) {
                    alertGameMessage("请选择允许升级的地皮，非公司/车站地皮");
                    return;
                }

                if (CalcUtil.calcPositionLevel(choosePosition) == 5) {
                    alertGameMessage("当前地皮已满级，请选择其他地皮");
                    return;
                }
                // 取反变为true
                chooseBuild = !chooseBuild;
                sendMsg(UPGRADE_BUILDING, playerNode.getPlayer(), playerNode.getPosition());
            } else {
                // 如果不是免费升级的话
                sendMsg(UPGRADE_BUILDING, playerNode.getPlayer(), playerNode.getPosition());
            }
        });

        buyBtn.addActionListener(e -> {
            Player player = playerMap.get(nickname);
            PlayerNode playerNode = player.getPlayerNode();
            // 获取玩家当前的位置
            Integer userPosition = playerNode.getPosition();
            PositionDto position = positionMap.get(userPosition);
            if (position.getAllowBuy()) {
                Integer positionPrice = CalcUtil.calcPositionPrice(position);
                Integer cash = playerNode.getCash();
                if (cash >= positionPrice) {
                    playerNode.setCash(cash - positionPrice);
                    playerNode.setProperty(playerNode.getProperty() - positionPrice / 2);
                    player.setPlayerNode(playerNode);
                    player.refreshTips(position);
                    sendMsg(BUY_POSITION, playerNode.getPlayer(), position);
                    // 购买之后禁止购买
                    refreshBtnStatus(buyBtn, false);
                }

            } else {
                showTips("当前位置不允许购买");
                refreshBtnStatus(buildBtn, false);
            }
        });

        saleBtn.addActionListener(e -> {
            Player player = playerMap.get(nickname);
            PlayerNode playerNode = player.getPlayerNode();
            // 房子最多的玩家拆一栋房子
            if (choosePosition == null) {
                alertGameMessage("请选择一块地皮");
                return;
            }
            int position = choosePosition.getPosition();
            if (pullDown) {
                if (choosePosition instanceof CityDto) {
                    CityDto chooseCity = (CityDto) choosePosition;
                    if (chooseCity.getLevel() > 0) {
                        int userChoose = JOptionPane.showConfirmDialog(null, "是否确认拆除一栋此处的房产", "游戏提示", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (userChoose == JOptionPane.YES_OPTION) {
                            pullDown = !pullDown;
                            sendMsg(PULL_DOWN, playerNode.getPlayer(), 0, chooseCity);
                        }
                    } else {
                        alertGameMessage("请选择一块有建筑的点位");
                    }
                } else {
                    alertGameMessage("请选择一块有房屋的地区点位");
                }
            } else {

                // 当前选中的是地区， 需要考虑房子的问题
                if (choosePosition instanceof CityDto) {
                    CityDto city = (CityDto) choosePosition;
                    List<String> options = new ArrayList<>();
                    options.add("取消");
                    options.add("全部");
                    Integer cityLevel = city.getLevel();
                    if (cityLevel > 0) {
                        for (int i = 1; i <= cityLevel; i++) {
                            options.add("卖" + i + "栋");
                        }
                    }
                    int res = JOptionPane.showOptionDialog(null, "请选择售卖方式", "售卖提示",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options.toArray(), "全部");
                    if (res == 0) {
                        return;
                    }
                    if (res == 1) {
                        sendMsg(PULL_DOWN, nickname, 1, position + ",-1");
                        return;
                    }
                    if (res > 1) {
                        sendMsg(PULL_DOWN, nickname, 1, position + "," + (res - 1));
                        return;
                    }
                }
                // 售卖公司和车站
                sendMsg(PULL_DOWN, nickname, 2, position);
            }
        });

        redemptionBtn.addActionListener(e -> {
            Player player = playerMap.get(nickname);
            PlayerNode playerNode = player.getPlayerNode();
            if (choosePosition == null) {
                alertGameMessage("请选择要赎回的地皮");
                return;
            }

            if (choosePosition.getPositionStatus()) {
                alertGameMessage("当前地皮未出售，请选择其他地皮");
                return;
            }
            int position = choosePosition.getPosition();
            // 赎回价格是买的价格的一半
            Integer price = CalcUtil.calcPositionPrice(choosePosition) / 2;
            if (playerNode.getCash() < price) {
                alertGameMessage("现金不足不允许赎回");
                return;
            }
            sendMsg(BUY_POSITION, nickname, 1, position);
        });
        passBtn.addActionListener(e -> {
            Player player = playerMap.get(nickname);
            PlayerNode playerNode = player.getPlayerNode();
            sendRefreshTipsMsg(nickname, "【%s】: 玩家跳过", nickname);
            if (playerNode.getCash() < 0) {
                sendRefreshTipsMsg(nickname, "【%s】: 玩家存在欠款，不允许跳过", nickname);
                return;
            }
            sendMsg(PASS, playerNode.getPlayer(), null);
            refreshBtnStatus(passBtn, false);
        });
        return buttonPanel;
    }

    /**
     * 初始化坐标点位UI
     *
     * @param jPanel  组件
     * @param from    开始
     * @param to      结束
     * @param reverse 是否反转
     */
    private void initPositionUi(JPanel jPanel, Integer from, Integer to, Boolean reverse) {
        List<PositionUi> positions = getPositions(from, to);
        if (reverse) {
            positions.sort((prev, next) -> next.getPosition().compareTo(prev.getPosition()));
        }
        for (PositionUi position : positions) {
            jPanel.add(position);
        }
    }

    private JPanel initUserPanel() {
        userListPanel.removeAll();
        userListPanel.setLayout(new GridLayout(userList.size(), 1));
        JLabel tipsLabel;
        StringBuffer sb;
        for (String username : playerMap.keySet()) {
            Player player = playerMap.get(username);
            JPanel panel = player.getPanel();
            panel.setLayout(new FlowLayout());
            panel.setMaximumSize(new Dimension(100, 80));
            int r = RandomUtil.randomInt(0, 255);
            int g = RandomUtil.randomInt(0, 255);
            int b = RandomUtil.randomInt(0, 255);
            panel.setBorder(new LineBorder(new Color(r, g, b), 1));
            PlayerNode playerNode = player.getPlayerNode();
            sb = new StringBuffer();
            String white = "  ";
            String br = "<br />";
            sb.append("<html>")
                    .append("玩家: ").append(playerNode.getPlayer()).append(white)
                    .append("状态: ").append(playerNode.getStatus() ? "正常" : "休息").append(br)
                    .append("位置: ").append(playerNode.getPosition()).append(white)
                    .append("名称: ").append(positionMap.get(playerNode.getPosition()).getName()).append(br)
                    .append("现金: ").append(playerNode.getCash()).append(white)
                    .append("资产: ").append(playerNode.getProperty()).append(br)
                    .append("</html>");
            tipsLabel = new JLabel(sb.toString());
            tipsLabel.setFont(font);
            panel.add(tipsLabel);
            player.setTipsLabel(tipsLabel);
            userListPanel.add(panel);
        }
        userListPanel.updateUI();
        initBtnStatus();
        return userListPanel;
    }

    private JPanel initUserPanelUI(){
        userListPanel.removeAll();
        userListPanel.setLayout(new GridLayout(userList.size(), 1));
        PlayerUI playerUI = null;
        for (String username : playerMap.keySet()) {
            Player player = playerMap.get(username);
            playerUI = new PlayerUI(player.getPlayerNode(), positionMap.get(0));
            player.setPlayerUI(playerUI);
            JPanel playerPanel = playerUI.getPlayerPanel();
            playerPanel.setLayout(new GridLayout(3,1));
            playerPanel.setMaximumSize(new Dimension(100, 80));
            userListPanel.add(playerPanel);
        }
        userListPanel.updateUI();
        initBtnStatus();
        return userListPanel;
    }

    private final Border positionSelectedBorder = BorderFactory.createLineBorder(JBColor.RED, 2);

    /**
     * 刷新地皮UI
     */
    private void refreshPositions(Player player) {
        PlayerNode playerNode = player.getPlayerNode();
        PlayerAction playerAction = aiPlayerActionMap.get(currentPlayer.getPlayer());
        boolean robotControl = isHomeowner() && playerAction != null;
        // 当前是活动玩家并且非机器人
        if (isActionPlayer() && !robotControl) {
            // 清空原来的所用数据
            positionMainPanel.removeAll();
            List<PositionDto> positionRefreshList = CalcUtil.getUserPositionRefreshList(playerNode);
            if (CollUtil.isNotEmpty(positionRefreshList)) {
                positionRefreshList.forEach(item -> {
                    JPanel positionPanel = getPositionPanel(item);
                    positionMainPanel.add(positionPanel);
                    if (choosePosition != null && choosePosition.getPosition().equals(item.getPosition())) {
                        positionPanel.setBorder(positionSelectedBorder);
                    } else {
                        positionPanel.setBorder(BorderFactory.createLineBorder(item.getColor()));
                    }

                    // 添加点击事件
                    positionPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // 选中的房产
                            choosePosition = item;
                            positionPanel.setBorder(positionSelectedBorder);
                            refreshPositions(player);
                        }
                    });

                });
                positionMainPanel.updateUI();
            }
        }
    }

    /**
     * 获取地皮
     *
     * @param position 坐标点位
     * @return JPanel   卡片面板
     */
    private JPanel getPositionPanel(PositionDto position) {
        JPanel positionPanel = new JPanel();
        positionPanel.setBorder(new LineBorder(position.getColor(), 1));
        positionPanel.setBackground(new Color(0x2B, 0x2B, 0x2B));
        int positionPanelWidth = 80;
        positionPanel.setPreferredSize(new Dimension(positionPanelWidth, 40));
        positionPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel();
        nameLabel.setText(String.format("地皮:%s", position.getName()));
        nameLabel.setForeground(position.getColor());
        nameLabel.setPreferredSize(new Dimension(positionPanelWidth, 13));
        nameLabel.setFont(font);

        JLabel levelLabel = new JLabel();
        Integer level = CalcUtil.calcPositionLevel(position);
        Integer toll = CalcUtil.calcPositionToll(position);
        levelLabel.setText(String.format("等级%s/¥ %d", level, toll));
        levelLabel.setForeground(position.getColor());
        levelLabel.setPreferredSize(new Dimension(positionPanelWidth, 13));
        levelLabel.setFont(font);

        positionPanel.setToolTipText(String.format("地皮:%s,等级%d,过路费%d", position.getName(), level, toll));
        addAll(positionPanel, nameLabel, levelLabel);
        return positionPanel;
    }

    /**
     * 获取窗口模式
     *
     * @return ComboBox 选择框
     */
    private ComboBox getWindowModeComboBox() {
        ComboBox comboBox = new ComboBox();
        for (WindowMode mode : WindowMode.values()) {
            comboBox.addItem(mode.getName());
            if (windowMode == mode) {
                comboBox.setSelectedItem(mode.getName());
            }
        }
        comboBox.addItemListener(e -> {
            windowMode = WindowMode.getMode(comboBox.getSelectedItem().toString());
            flushWindowMode();
        });
        return comboBox;
    }

    /**
     * 刷新窗口模式
     */
    private void flushWindowMode() {
        int width = 390;
        int topWidth = 200;
        if (windowMode == WindowMode.ADAPTIVE) {
        }

        mainPanel.setMinimumSize(new Dimension(width, 350));
        tipsArea.setPreferredSize(new Dimension(topWidth, 40));

        mainPanel.updateUI();
    }


    private void buildPlayerNode() {
        // 设置开始玩家
        PlayerNode startNode = null;
        // 创建玩家节点
        PlayerNode playerNode = null;
        // 玩家姓名集合
        playerMap = new HashMap<>();
        // 获取当前房间内的玩家
        List<String> roomUserList = userList;
        // 当前房间玩家总数
        int usersTotal = roomUserList.size();
        // 节点放在外边防止循环创建玩家节点
        PlayerNode node;
        // 创建玩家节点
        for (int i = 0; i < usersTotal; i++) {
            node = new PlayerNode();
            node.setPlayer(roomUserList.get(i));
            node.setAlias("Machine 0" + (i + 1));
            playerMap.put(node.getPlayer(), new Player(node, new JPanel()));
            // 固定房主是第一名玩家
            if (StrUtil.equalsIgnoreCase(homeOwnerName(), node.getPlayer())) {
                currentPlayer = node;
                helpPlayerAction = new AiPlayerAction(currentPlayer);
            }

            if (aiPlayerActionMap.containsKey(node.getPlayer())) {
                aiPlayerActionMap.put(node.getPlayer(), new AiPlayerAction(node));
            }

            if (playerNode == null) {
                playerNode = node;
                startNode = node;
                continue;
            }

            if (i == usersTotal - 1) {
                node.setNextPlayer(startNode);
                startNode.setPrevPlayer(node);
            }

            playerNode.setNextPlayer(node);
            node.setPrevPlayer(playerNode);
            playerNode = node;
        }
    }

    @Deprecated
    private void showTips(String formatStr, Object... data) {
        tipsArea.setRows(++tipsRows);
        tipsArea.append(String.format(formatStr, data) + "\n");
        tipsArea.updateUI();
    }

    private void showTips(String tips) {
        tipsArea.setRows(++tipsRows);
        tipsArea.append(tips + "\n");
        tipsArea.updateUI();
    }


    @Override
    public void handle(MonopolyGameDto body) {
        if (status == 0) {
            return;
        }
        if (status == -1) {
            alertGameMessage("当前游戏已结束，请重新开始游戏");
            return;
        }
        /** 游戏状态 -1结束 0初始化 1游戏中 2玩家正在出售房产  3玩家正在思考免费升级房屋 4玩家在思考拆除房屋 5玩家正在重新投掷骰子*/
        boolean hasTempUser = CollUtil.isNotEmpty(tempPlayerList);
        if (status == 2 && hasTempUser) {
            String join = String.join(",", tempPlayerList);
            refreshTips(String.format("%s 玩家正在出售房产，请等待", join));
        }

        if (status == 3 && hasTempUser) {
            String join = String.join(",", tempPlayerList);
            refreshTips(String.format("%s 玩家正在思考免费升级房屋，请等待", join));
        }

        if (status == 4 && hasTempUser) {
            String join = String.join(",", tempPlayerList);
            refreshTips(String.format("%s 玩家在思考拆除房屋，请等待", join));
        }

        if (status == 5 && hasTempUser) {
            String join = String.join(",", tempPlayerList);
            refreshTips(String.format("%s 玩家正在重新投掷骰子，请等待", join));
        }

        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = null;
        try {
            playerNode = player.getPlayerNode();
        } catch (NullPointerException e) {
            log.error("发生错误, - {}", body);
        }

        switch (body.getMsgType()) {
            case JOIN_ROBOTS:
                joinRobots(body);
                break;
            case DICE_ROLL:
                diceRoll(body);
                break;
            case REFRESH_TIPS:
                refreshTips(body);
                break;
            case TAX:
                tax(body);
                break;
            case BROKE_EXIT:
                gameOver();
                break;
            case CHANCE:
                randomChance(body);
                break;
            case DESTINY:
                randomDestiny(body);
                break;
            case TO_JAIL:
                toJail(playerName, player, playerNode);
                break;
            case REST:
                restInPark(playerName, player, playerNode);
                break;
            case PAY_TO_OTHERS:
                payToOthers(body);
                break;
            case PASS:
                pass();
                break;
            case BUY_POSITION:
                buyPosition(body);
                break;
            case PAY_TOLL:
                payToll(body);
                break;
            case UPGRADE_BUILDING:
                upgradeBuilding(body);
                break;
            case DICE_ROLL_AGAIN:
                allDiceRollAgain();
                break;
            case AGAIN_RESULT:
                againResult(body);
                break;
            case REMOVE_TEMP_PLAYER:
                removeTempPlayer(body);
                break;
            case PULL_DOWN:
                playerPullDown(body);
                break;
            default:
                break;
        }
    }

    /**
     * 玩家确认拆除
     * actionId
     * 0 房子最多的玩家拆一栋， 不给钱
     * 1 卖房子
     * 2 售卖公司或者自来水厂
     *
     * @param body 游戏消息实体类
     */
    private void playerPullDown(MonopolyGameDto body) {
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        Integer actionId = body.getActionId();
        // 房子最多的玩家拆一栋不给钱
        if (actionId == 0) {
            CityDto city = (CityDto) body.getData();
            for (CityDto item : playerNode.getCities()) {
                if (item.getPosition().equals(city.getPosition())) {
                    item.setLevel(item.getLevel() - 1);
                    positionMap.put(item.getPosition(), item);
                    break;
                }
            }
            player.refreshTips(positionMap);
            // 移除临时提权玩家
            removeTempPlayer(body);
        } else if (actionId == 1) {
            // 卖房子
            String[] split = body.getData().toString().split(",");
            Integer position = Integer.valueOf(split[0]);
            Integer saleNum = Integer.valueOf(split[1]);

            // 售卖
            for (CityDto item : playerNode.getCities()) {
                if (item.getPosition().equals(position)) {
                    if (saleNum == -1) {
                        saleNum = CalcUtil.calcPositionLevel(positionMap.get(position));
                        item.setPositionStatus(false);
                    } else {
                        item.setLevel(item.getLevel() - saleNum);
                    }
                    positionMap.put(item.getPosition(), item);
                    Integer price = CalcUtil.calcPositionPrice(item) / 2;
                    int buildPrice = item.getBuildMoney() * saleNum;
                    price += buildPrice;
                    playerNode.upgradeCashAndProperty(price, price, true);
                    break;
                }
            }

            if (playerNode.getCash() > 0) {
                if (StrUtil.equalsIgnoreCase(playerNode.getPlayer(), GameAction.getNickname())) {
                    refreshBtnStatus(saleBtn, false);
                    refreshBtnStatus(passBtn, false);
                }
                // 移除临时提权玩家
                removeTempPlayer(body);
            }

        } else if (actionId == 2) {
            Integer salePosition = (Integer) body.getData();
            // 售卖
            for (PositionDto item : playerNode.getPositions()) {
                if (item.getPosition().equals(salePosition)) {
                    item.setPositionStatus(false);
                    positionMap.put(item.getPosition(), item);
                    Integer price = CalcUtil.calcPositionPrice(item) / 2;
                    playerNode.upgradeCashAndProperty(price, price, true);
                    break;
                }
            }
        }
        updatePlayerMap(player, playerNode);
    }

    /**
     * 删除已临时提权玩家
     *
     * @param body 游戏消息实体类
     */
    private void removeTempPlayer(MonopolyGameDto body) {
        // 当前操作玩家
        String playerName = body.getPlayer();
        tempPlayerList.remove(playerName);
        sendRefreshTipsMsg(playerName, "【%s】: 玩家操作完成，临时提权结束", playerName);
        if (CollUtil.isEmpty(tempPlayerList)) {
            // 临时提权玩家为空，游戏状态改为 1
            status = 1;
            sendRefreshTipsMsg(playerName, "【系统提示】: 临时提权玩家为空，由【%s】控制游戏进程", currentPlayer.getPlayer());
        }
    }

    /**
     * 升级建筑
     *
     * @param body 游戏消息实体类
     */
    private void upgradeBuilding(MonopolyGameDto body) {
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        Integer userPosition = playerNode.getPosition();
        PositionDto position = positionMap.get(userPosition);
        if (!position.getUpgradeAllowed()) {
            showTips("当前地皮不允许升级");
            return;
        }
        String positionOwner = position.getOwner();
        if (!StrUtil.equalsIgnoreCase(positionOwner, playerName)) {
            showTips("当前地皮非本玩家所有，不允许升级");
            return;
        }
        // 查找对应的
        long count = playerNode.getCities().stream()
                .filter(item -> item.getPosition().equals(position.getPosition()))
                .count();
        Integer price = CalcUtil.calcPositionPrice(position);

        if (count == 0 && StrUtil.isBlank(positionOwner) && playerNode.getCash() >= price) {
            // 玩家未拥有该地皮 自动购买
            sendMsg(BUY_POSITION, playerName, position);
            return;
        }

        playerNode.getCities()
                .forEach(item -> {
                    if (item.getPosition().equals(position.getPosition())) {
                        if (item.getLevel() <= 4) {
                            item.setLevel(item.getLevel() + 1);
                            positionMap.put(position.getPosition(), item);
                            sendRefreshTipsMsg(playerName, "玩家升级了%s地皮的房屋，当前地皮等级%d,过路费%d",
                                    position.getName(), item.getLevel(), item.getToll());
                        } else {
                            item.setUpgradeAllowed(false);
                            positionMap.put(position.getPosition(), item);
                            sendRefreshTipsMsg(playerName, "当前地皮建筑已满级，禁止升级");
                        }
                    }
                });
        player.setPlayerNode(playerNode);
        player.refreshTips(position);
        refreshPositions(player);
    }

    private void payToll(MonopolyGameDto body) {
        PositionDto position = (PositionDto) body.getData();
        // 获取地皮拥有者及其他属性
        String owner = position.getOwner();
        Player ownerPlayer = playerMap.get(owner);
        PlayerNode ownerPlayerNode = ownerPlayer.getPlayerNode();
        int toll = 0;
        if (position instanceof CompanyDto) {
            int size = ownerPlayerNode.getCompanies().size();
            int multiple = size == 1 ? 10 : 100;
            JOptionPane.showMessageDialog(null,
                    String.format("当前位置为%s, 过路费为随机点数 * %d 倍", position.getName(), multiple));
            Integer random = CalcUtil.randomInt();
            toll = random * multiple;
        } else {
            toll = CalcUtil.calcPositionToll(position);
        }
        // 如果是地区地皮，并且当前地皮拥有者有同颜色所有的地皮且当前地皮是空地则双倍收租
        if (position instanceof CityDto && CalcUtil.isDouble(ownerPlayerNode, (CityDto) position, positionMap)) {
            toll *= 2;
        }
        // 获取当前玩家
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        subPlayerCash(player, playerNode, toll);
        sendRefreshTipsMsg(owner, "【%s】 获得 【%s】 缴纳的过路费 %d", owner, playerNode.getPlayer(), toll);
        Integer ownerCash = ownerPlayerNode.getCash();
        Integer property = ownerPlayerNode.getProperty();
        sendRefreshPropertyTipsMsg(playerName, ownerCash, ownerCash + toll, property, property + toll);
        sendRefreshPropertyTipsMsg(owner, ownerCash, ownerCash + toll, property, property + toll);
        ownerPlayerNode.upgradeCashAndProperty(toll, toll, true);
        ownerPlayer.setPlayerNode(ownerPlayerNode);
        ownerPlayer.refreshTips(positionMap.get(ownerPlayerNode.getPosition()));
    }

    private void buyPosition(MonopolyGameDto body) {
        String playerName = body.getPlayer();
        PositionDto position = (PositionDto) body.getData();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        Integer price = CalcUtil.calcPositionPrice(position);
        String buyStr = "购买";
        if (body.getActionId() != null) {
            buyStr = "赎回";
            // 赎回价格是购买价格的一半
            price /= 2;
        }
        if (playerNode.getCash() < price) {
            showTips("玩家现金不足以购买地皮");
            return;
        }
        // 修复重复购买bug
        long count = playerNode.getPositions().stream()
                .filter(item -> item.getPosition().equals(position.getPosition()))
                .count();
        if (count == 1) {
            return;
        }
        sendRefreshTipsMsg(playerName, "【%s】:  %s了【%s】 地皮", playerName, buyStr, position.getName());
        position.setOwner(playerName);
        // 更换map中的数据
        positionMap.put(position.getPosition(), position);
        Integer cash = playerNode.getCash();
        Integer property = playerNode.getProperty();
        player = CalcUtil.buyPosition(player, position);
        sendRefreshPropertyTipsMsg(playerName,
                cash,
                player.getPlayerNode().getCash(),
                property,
                player.getPlayerNode().getProperty());
        playerMap.put(playerName, player);
        // 如果是当前玩家在玩的话刷新当前
        if (StrUtil.equalsIgnoreCase(playerNode.getPlayer(), currentPlayer.getPlayer())) {
            refreshPositions(player);
        }
    }

    /**
     * 切换玩家
     */
    private void pass() {
        if (null != currentPlayer) {
            sendRefreshTipsMsg(currentPlayer.getPlayer(), "玩家回合结束");
            currentPlayer = currentPlayer.getNextPlayer();
            sendRefreshTipsMsg(currentPlayer.getPlayer(), "玩家回合开始");
        }
        PlayerAction playerAction = aiPlayerActionMap.get(currentPlayer.getPlayer());
        AiPlayerAction aiPlayerAction = (AiPlayerAction) playerAction;
        boolean robotControl = isHomeowner() && playerAction != null;
        String playerName = currentPlayer.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        if (playerNode.getStatus()) {
            // 人机玩家且没有休息
            if (robotControl) {
                ThreadUtils.spinMoment(aiThinkingTime);
                // 获取AI步数
                Integer step = aiPlayerAction.diceRoll();
                sendMsg(DICE_ROLL, currentPlayer.getPlayer(), step);
            } else {
                List<PositionDto> positions = playerNode.getPositions();
                if (CollUtil.isNotEmpty(positions)) {
                    // 已售出的地皮数量
                    long soldOutPositionCount = positions.stream()
                            .filter(item -> !item.getPositionStatus())
                            .count();
                    // 如果有已经售出的地皮，则将赎回按钮放开
                    if (soldOutPositionCount > 0) {
                        refreshBtnStatus(redemptionBtn, true);
                    }
                }
                initBtnStatus();
            }
        } else {
            playerNode.setStatus(!playerNode.getStatus());
            sendRefreshTipsMsg(currentPlayer.getPlayer(), "玩家当前回合休息一轮");
            sendMsg(PASS, playerNode.getPlayer(), null);
        }
    }

    /**
     * 支付给其他游戏钱
     *
     * @param body 发送的游戏消息
     */
    @SuppressWarnings("all")
    private void payToOthers(MonopolyGameDto body) {
        Set<String> data = (Set<String>) body.getData();
        for (String playerName : data) {
            Player player = playerMap.get(playerName);
            PlayerNode playerNode = player.getPlayerNode();
            playerNode.setCash(playerNode.getCash() + body.getActionId());
            player.refreshTips(positionMap.get(playerNode.getPosition()));
        }
    }

    /**
     * 在停车场休息一回合
     *
     * @param playerName 玩家名称
     * @param player     玩家
     * @param playerNode 玩家节点
     */
    private void restInPark(String playerName, Player player, PlayerNode playerNode) {
        sendRefreshTipsMsg(playerName, "玩家在停车场休息一轮");
        playerNode.setStatus(false);
        player.setPlayerNode(playerNode);
    }

    /**
     * 玩家入狱休息一轮
     *
     * @param playerName 玩家名称
     * @param player     玩家
     * @param playerNode 玩家节点
     */
    private void toJail(String playerName, Player player, PlayerNode playerNode) {
        playerNode.setStatus(false);
        playerNode.setPosition(10);
        player.setPlayerNode(playerNode);
        sendRefreshTipsMsg(playerName, "玩家移动到监狱，休息一轮");
    }

    /**
     * 随机机会卡片
     *
     * @param body 游戏消息
     */
    private void randomChance(MonopolyGameDto body) {
        // 获取机会卡牌
        LuckEntity chanceCard = (LuckEntity) body.getData();
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = null;
        try {
            playerNode = player.getPlayerNode();
        } catch (NullPointerException e) {
            log.error("发生错误, - {}", body);
        }
        switch (chanceCard.getId()) {
            case 0:
                // 玩家得600元
                playerNode.upgradeCashAndProperty(600, 600, true);
                break;
            case 1:
                // 花费1200元
                subPlayerCash(player, playerNode, 1200);
                break;
            case 2:
                // 获得出狱许可证
                outJailLicence = playerName;
                sendRefreshTipsMsg(playerName, "获得出狱许可证");
                break;
            case 3:
                // 损失800元
                subPlayerCash(player, playerNode, 800);
                break;
            case 4:
                // 马上回到起点并领取2000元
                playerNode.setPosition(0);
                playerNode.upgradeCashAndProperty(startMoney, startMoney, true);
                break;
            case 5:
                // 房子最多的人拆一栋房子
                List<Player> maxBuildingPlayers = CalcUtil.getPlayerBuildings(playerMap, false);
                pullDownBuilding(maxBuildingPlayers);
                break;
            case 6:
                // 最靠近美国的玩家罚700元
                List<Player> minDistanceAmericaPlayers = CalcUtil.getMinDistancePlayer(playerMap, positionMap.get(1));
                minDistanceAmericaPlayers.forEach(item -> subPlayerCash(item, item.getPlayerNode(), 700));
                break;
            case 7:
                // 立即坐牢
                playerNode.setPosition(10);
                playerNode.setStatus(false);
                break;
            case 8:
                // 大家转转盘，点数最大的人拿取点数X10的金额
                sendMsg(DICE_ROLL_AGAIN, playerName, null);
                break;
            case 9:
                // 玩家下回合暂停一次
                playerNode.setStatus(false);
                break;
            case 10:
                // 现金最多的玩家罚1000元
                List<Player> maxCashPlayers = CalcUtil.getMaxCashPlayer(playerMap);
                maxCashPlayers.forEach(item -> subPlayerCash(item, item.getPlayerNode(), 1000));
                break;
            case 11:
                // 捐200元再转转盘行动一次
                if (playerNode.getCash() > 200) {
                    int dialog = JOptionPane.showConfirmDialog(null, "是否花费200元重新投掷一次", "游戏提示", JOptionPane.OK_CANCEL_OPTION);
                    if (dialog == JOptionPane.YES_OPTION) {
                        sendMsg(DICE_ROLL, playerNode.getPlayer(), RandomUtil.randomInt(2, 12));
                    }
                }
                break;
            case 12:
                // 付600元看医生
                subPlayerCash(player, playerNode, 600);
                break;
            case 13:
                // 最靠近英国的玩家付500元
                List<Player> minDistancePlayer = CalcUtil.getMinDistancePlayer(playerMap, positionMap.get(21));
                minDistancePlayer.forEach(item -> subPlayerCash(item, item.getPlayerNode(), 500));
                break;
            case 14:
                // 房子最少的玩家免费盖一栋
                List<Player> minBuildingPlayers = CalcUtil.getPlayerBuildings(playerMap, true);
                freeNewBuilding(minBuildingPlayers);
                break;
            default:
                break;
        }
        player.setPlayerNode(playerNode);
        player.refreshTips(positionMap.get(playerNode.getPosition()));
    }

    /**
     * 所有玩家重新投掷
     */
    private void allDiceRollAgain() {
        status = 5;
        // 如果是人机的话自动投掷骰子
        if (isHomeowner()) {
            for (String aiPlayerName : aiPlayerActionMap.keySet()) {
                sendMsg(AGAIN_RESULT, aiPlayerName, RandomUtil.randomInt(1, 12));
            }
        }

        int yesOption = JOptionPane.showConfirmDialog(null, "点数最大的玩家获得点数 * 10的渐进", "游戏提示", JOptionPane.YES_OPTION);
        if (yesOption == JOptionPane.YES_OPTION) {
            sendMsg(AGAIN_RESULT, GameAction.getNickname(), RandomUtil.randomInt(1, 12));
        }
    }

    private void againResult(MonopolyGameDto body) {
        String player = body.getPlayer();
        Integer result = (Integer) body.getData();
        sendRefreshTipsMsg(player, "【%s】: 投掷结果为 %d", player, result);
        againResultMap.put(player, result);
        if (againResultMap.size() == userList.size()) {
            // 修改游戏状态为 游戏中
            status = 1;
            List<Integer> results = new ArrayList<>(againResultMap.values());
            results.sort(Integer::compareTo);
            int maxResult = results.get(results.size() - 1);
            List<String> maxPlayer = new ArrayList<>();
            againResultMap.forEach((k, v) -> {
                if (maxResult == v) {
                    maxPlayer.add(k);
                }
            });
            int addMoney = maxResult * 10;
            sendRefreshTipsMsg("", "玩家投掷最大点数为【%d】, 【%s】获得 %d 元",
                    maxResult, String.join(",", maxPlayer), addMoney);
            for (String item : maxPlayer) {
                Player addMoneyPlayer = playerMap.get(item);
                PlayerNode playerNode = addMoneyPlayer.getPlayerNode();
                playerNode.upgradeCashAndProperty(addMoney, addMoney, true);
                addMoneyPlayer.setPlayerNode(playerNode);
                addMoneyPlayer.refreshTips(positionMap.get(playerNode.getPosition()));
            }

        }
    }

    /**
     * 随机命运卡
     *
     * @param body 游戏消息
     */
    private void randomDestiny(MonopolyGameDto body) {
        // 获取机会卡牌
        LuckEntity destinyCard = (LuckEntity) body.getData();
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = null;
        try {
            playerNode = player.getPlayerNode();
        } catch (NullPointerException e) {
            log.error("发生错误, - {}", body);
        }
        switch (destinyCard.getId()) {
            case 0:
                // 从巴黎直达伦敦 过起点的话领2000
                Integer position = playerNode.getPosition();
                // 在英国或者超过英国就会经过起点可以领取等值于startMoney的钱
                if (position >= 21) {
                    playerNode.upgradeCashAndProperty(startMoney, startMoney, true);
                    player.setPlayerNode(playerNode);
                    sendRefreshTipsMsg(playerName, "玩家经过起点，领取%d块", startMoney);
                }
                break;
            case 1:
                // 免费盖一栋房子
                if (CollUtil.isEmpty(playerNode.getCities())) {
                    sendRefreshTipsMsg(playerName, "【%s】玩家暂无地皮获得2000元奖金", playerName);
                    playerNode.upgradeCashAndProperty(2000, 2000, true);
                } else {
                    freeNewBuilding(Collections.singletonList(player));
                }
                break;
            case 2:
                // 奖金1000元
                playerNode.upgradeCashAndProperty(1000, 1000, true);
                break;
            case 3:
                // 奖金900元
                playerNode.upgradeCashAndProperty(900, 900, true);
                break;
            case 4:
                // 罚款600元
                subPlayerCash(player, playerNode, 600);
                break;
            case 5:
                // 花费700元
                subPlayerCash(player, playerNode, 700);
                break;
            case 6:
                // 奖金800元
                playerNode.upgradeCashAndProperty(800, 800, true);
                break;
            case 7:
                // 奖金850元
                playerNode.upgradeCashAndProperty(850, 850, true);
                break;
            case 8:
                // 赔偿500元
                subPlayerCash(player, playerNode, 500);
                break;
            case 9:
                // 罚款房子每栋200元，旅馆600元
                List<CityDto> cities = playerNode.getCities();
                // 罚款
                int penaltyMoney = 0;
                // 房子总数
                int houseNum = 0;
                // 旅馆总数
                int hotelNum = 0;
                if (CollUtil.isNotEmpty(cities)) {
                    List<CityDto> hotelCity = cities.stream()
                            .filter(item -> item.getLevel() == 5)
                            .collect(Collectors.toList());
                    hotelNum = hotelCity.size();
                    // 获取不是满级的城市
                    cities.removeAll(hotelCity);
                    houseNum = (int) cities.stream()
                            .mapToInt(CityDto::getLevel)
                            .count();
                    penaltyMoney = hotelNum * hotelPenalty + hotelNum * housePenalty;
                }
                sendRefreshTipsMsg(playerName, "玩家拥有房屋%d间，旅馆%d间，扣款%d", houseNum, hotelNum, penaltyMoney);
                subPlayerCash(player, playerNode, penaltyMoney);
                break;
            case 10:
                // 休息一次
                playerNode.setStatus(false);
                break;
            case 11:
                // 奖金600元
                playerNode.upgradeCashAndProperty(600, 600, true);
                break;
            case 12:
                // 罚款400元
                subPlayerCash(player, playerNode, 400);
                break;
            case 13:
                // 给每人100元观光费
                Set<String> playerNameSet = playerMap.keySet();
                subPlayerCash(player, playerNode, 100 * playerNameSet.size() - 1);
                playerNameSet.remove(playerName);
                sendMsg(PAY_TO_OTHERS, playerName, 100, playerNameSet);
                break;
            case 14:
                // 奖金700元
                playerNode.upgradeCashAndProperty(700, 700, true);
                break;
            default:
                break;
        }
        // 更新右侧
        player.refreshTips(positionMap.get(playerNode.getPosition()));
    }

    /**
     * 免费升级一个建筑
     *
     * @param players 玩家集合
     */
    private void freeNewBuilding(List<Player> players) {
        List<String> playerNameList = getPlayerNamesByPlayersAndAddToTemp(players);
        if (playerNameList.contains(GameAction.getNickname())) {
            String str = String.format("%s玩家抽中机会卡,房子最少的玩家免费修建一栋房子", currentPlayer.getPlayer());
            alertGameMessage(str);
        }
        status = 3;
        for (Player player : players) {
            // 人机控制
            PlayerNode playerNode = player.getPlayerNode();
            String playerName = playerNode.getPlayer();
            List<CityDto> cities = playerNode.getCities();
            if (CollUtil.isEmpty(cities)) {
                sendRefreshTipsMsg(playerName, "【%s】: 玩家当前暂无可升级地皮，获得现金2000元", playerName);
                // 玩家不存在可升级的地皮，直接加2000块
                playerNode.upgradeCashAndProperty(2000, 2000, true);
                player.setPlayerNode(playerNode);
                player.refreshTips(positionMap.get(playerNode.getPosition()));
                sendMsg(REMOVE_TEMP_PLAYER, playerName, null);
            } else {
                if (robotController(playerName)) {
                    AiPlayerAction aiPlayerAction = new AiPlayerAction(playerNode);
                    Integer position = aiPlayerAction.getFreeBuildingPosition();
                    playerNode.getCities()
                            .forEach(item -> {
                                if (item.getPosition().equals(position)) {
                                    Integer level = item.getLevel();
                                    if (level <= 4) {
                                        item.setLevel(level + 1);
                                        positionMap.put(position, item);
                                    }
                                }
                            });
                    updatePlayerMap(player, playerNode);
                    sendMsg(REMOVE_TEMP_PLAYER, playerName, null);
                } else {
                    if (StrUtil.equalsIgnoreCase(playerName, GameAction.getNickname())) {
                        chooseBuild = true;
                        refreshBtnStatus(buildBtn, true);
                        break;
                    }
                }
            }
        }
    }

    private void updatePlayerMap(Player player, PlayerNode playerNode) {
        player.setPlayerNode(playerNode);
        String playerName = playerNode.getPlayer();
        playerMap.put(playerName, player);
        player.refreshTips(positionMap);
    }

    private List<String> getPlayerNamesByPlayersAndAddToTemp(List<Player> players) {
        List<String> playerNameList = players.stream()
                .map(Player::getPlayerNode)
                .map(PlayerNode::getPlayer)
                .collect(Collectors.toList());
        tempPlayerList.addAll(playerNameList);
        return playerNameList;
    }

    /**
     * 拆除建筑
     *
     * @param players 玩家集合
     */
    private void pullDownBuilding(List<Player> players) {
        List<String> playerNameList = getPlayerNamesByPlayersAndAddToTemp(players);
        if (playerNameList.contains(GameAction.getNickname())) {
            String str = String.format("%s玩家抽中机会卡,房子最少的玩家免费修建一栋房子", currentPlayer.getPlayer());
            alertGameMessage(str);
        }
        status = 4;
        for (Player player : players) {
            // 人机控制
            PlayerNode playerNode = player.getPlayerNode();
            String playerName = playerNode.getPlayer();
            List<CityDto> cities = playerNode.getCities();
            if (CollUtil.isEmpty(cities)) {
                sendRefreshTipsMsg(playerName, "【%s】: 玩家当前暂无房产，无需拆除", playerName);
                sendMsg(REMOVE_TEMP_PLAYER, playerName, null);
            } else {
                cities = CalcUtil.getHasBuildingCities(cities);
                if (CollUtil.isEmpty(cities)) {
                    sendRefreshTipsMsg(playerName, "【%s】: 玩家当前暂无房产，无需拆除", playerName);
                    sendMsg(REMOVE_TEMP_PLAYER, playerName, null);
                } else {
                    if (robotController(playerName)) {
                        AiPlayerAction aiPlayerAction = new AiPlayerAction(playerNode);
                        Integer position = aiPlayerAction.getPullDownBuilding();
                        playerNode.getCities()
                                .forEach(item -> {
                                    if (item.getPosition().equals(position)) {
                                        Integer level = item.getLevel();
                                        if (level != 0) {
                                            item.setLevel(level - 1);
                                            positionMap.put(position, item);
                                        }
                                    }
                                });
                        updatePlayerMap(player, playerNode);
                        sendMsg(REMOVE_TEMP_PLAYER, playerName, null);
                    } else {
                        if (StrUtil.equalsIgnoreCase(playerName, GameAction.getNickname())) {
                            pullDown = true;
                            saleBtn.setText("拆除建筑");
                            refreshBtnStatus(saleBtn, true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private Boolean robotController(String playerName) {
        return isHomeowner() && aiPlayerActionMap.get(playerName) != null;
    }


    /**
     * 罚款
     *
     * @param player     玩家
     * @param playerNode 玩家节点
     * @param money      扣款金额
     */
    private void subPlayerCash(Player player, PlayerNode playerNode, Integer money) {
        int property = playerNode.getProperty();
        int cash = playerNode.getCash();
        String playerName = playerNode.getPlayer();
        if (money > property) {
            sendRefreshTipsMsg(playerName, "玩家当前总资产%d,待支付%d,资产不足游戏结束", property, money);
            gameOver();
        }
        // 玩家总资产等于扣款金额 全部资产售卖，房产等级归零
        if (money == property) {
            playerNode.getCities().forEach(item -> {
                item.setPositionStatus(false);
                item.setLevel(0);
            });
            playerNode.getPositions().forEach(item -> item.setPositionStatus(false));
            playerNode.getCompanies().forEach(item -> item.setPositionStatus(false));
            playerNode.getStations().forEach(item -> {
                item.setPositionStatus(false);
                item.setLevel(0);
            });
            sendRefreshTipsMsg(playerName, "玩家当前总资产与待支付金额相同，地皮房产清空");
            sendRefreshPropertyTipsMsg(playerName, cash, 0, property, 0);
        }

        if (money == cash) {
            playerNode.setCash(0);
            sendRefreshTipsMsg(playerName, "玩家当前现金与待支付金额相同，现金清空");
            sendRefreshPropertyTipsMsg(playerName, cash, 0, property, property - cash);
        }
        // 待支付金额超过现金额度但是小于总资产数量
        if (money > cash && money < property) {
            playerNode.setCash(cash - money);
            playerNode.setProperty(property - cash);
            status = 2;
            // 更新出售按钮状态
            refreshBtnStatus(saleBtn, true);
            PlayerAction playerAction = aiPlayerActionMap.get(playerName);
            if (isHomeowner() && playerAction != null) {
                AiPlayerAction aiPlayerAction = new AiPlayerAction(playerNode);
                List<Integer> list = aiPlayerAction.saleBuild();
                for (Integer position : list) {
                    PositionDto positionDto = positionMap.get(position);
                    if (positionDto instanceof CityDto) {
                        sendMsg(PULL_DOWN, playerName, 1, position + ",-1");
                    } else {
                        sendMsg(PULL_DOWN, playerName, 2, position);
                    }
                }
            } else {
                tempPlayerList.add(playerName);
                if (StrUtil.equalsIgnoreCase(GameAction.getNickname(), playerName)) {
                    refreshBtnStatus(saleBtn, true);
                }
            }
        }
        if (money < cash) {
            playerNode.setCash(cash - money);
            playerNode.setProperty(property - money);
        }
        player.setPlayerNode(playerNode);
        // 刷新玩家面板
        player.refreshTips(positionMap.get(playerNode.getPosition()));
    }

    /**
     * 游戏结束
     */
    private void gameOver() {

    }

    /**
     * 税务
     *
     * @param body 发送的游戏消息
     */
    private void tax(MonopolyGameDto body) {
        Integer actionId = body.getActionId();
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        Integer tax = actionId == 4 ? incomeTax : propertyTax;
        sendRefreshTipsMsg(playerName, "【%s】:玩家缴纳 %s %d元", playerName, actionId == 4 ? "所得税" : "财产税", tax);
        subPlayerCash(player, player.getPlayerNode(), tax);
    }

    /**
     * 刷新提示
     *
     * @param body 游戏消息
     */
    private void refreshTips(MonopolyGameDto body) {
        refreshTips(String.valueOf(body.getData()));
    }

    private void refreshTips(String str) {
        tipsArea.setRows(Math.max(++tipsRows, 10));
        tipsArea.append(str + "\n");
        tipsArea.updateUI();
        tipsArea.selectAll();
    }

    /**
     * 加入人机玩家
     *
     * @param body 消息体
     */
    private void joinRobots(MonopolyGameDto body) {
        List<String> robotList = (List<String>) body.getData();
        userList.addAll(robotList);
        buildPlayerNode();
        showGamePanel();
    }

    private void diceRoll(MonopolyGameDto body) {
        Integer step = (Integer) body.getData();
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        // 获取上一次的位置
        Integer lastPosition = playerNode.getPosition();
        // 添加上步数的位置
        int currentPosition = lastPosition + step;
        // 棋盘一共有40个位置,超过40的话取余数就行了 获取在地图上的点位
        int userPosition = currentPosition % ALL_STEP;
        playerNode.setPosition(userPosition);
        // 获取点位的数据刷新玩家坐标点位
        PositionDto position = positionMap.get(userPosition);
        player.refreshTips(position);
        sendRefreshTipsMsg(playerName, "投掷了骰子");
        String name = position.getName();
        sendRefreshTipsMsg(playerName, "【%s】向前行走了%d步,当前位置%s", playerName, step, name);
        // 上一次的圈数
        int lastCylinderNumber = lastPosition / ALL_STEP;
        int currentCylinderNumber = currentPosition / ALL_STEP;
        // 如果走完一圈加2000块
        if (currentCylinderNumber - lastCylinderNumber > 0) {
            addStartMoney(player, false);
        }
        PlayerAction playerAction = aiPlayerActionMap.get(currentPlayer.getPlayer());
        Boolean robotControl = isHomeowner() && playerAction != null;
        // 如果当前点位允许购买并且当前没有拥有者
        String owner = position.getOwner();
        // 是否当前操作玩家
        Boolean actionFlag = isActionPlayer();
        if (robotControl) {
            if (StrUtil.isBlank(owner) && position.getAllowBuy()) {
                ThreadUtils.spinMoment(aiThinkingTime);
                boolean whetherToBuy = new AiPlayerAction(currentPlayer).whetherToBuy(CalcUtil.calcPositionPrice(position));
                if (whetherToBuy) {
                    sendMsg(BUY_POSITION, playerName, position);
                }
            }
            // 本人的地皮并且支持购买升级
            if (StrUtil.equalsIgnoreCase(owner, playerName) &&
                    position.getAllowBuy() &&
                    position.getUpgradeAllowed()) {
                ThreadUtils.spinMoment(aiThinkingTime);
                boolean upgradeFlag = new AiPlayerAction(currentPlayer).whetherToBuy(CalcUtil.calcPositionUpgrade(position));
                if (upgradeFlag) {
                    sendMsg(UPGRADE_BUILDING, currentPlayer.getPlayer(), position);
                }
            }
        } else if (actionFlag){
            refreshBtnStatus(randomBtn, false);
            // 当前玩家操作释放通过按钮
            if (StrUtil.equalsIgnoreCase(playerName, currentPlayer.getPlayer())) {
                refreshBtnStatus(passBtn, true);
            }
            if (StrUtil.isBlank(owner) && position.getAllowBuy()) {
                // 更新购买按钮状态
                refreshBtnStatus(buyBtn, true);
            }
            // 本人的地皮并且支持购买升级
            if (StrUtil.equalsIgnoreCase(owner, playerName) &&
                    position.getAllowBuy() &&
                    position.getUpgradeAllowed()) {
                refreshBtnStatus(buildBtn, true);
            }

        }

        // 如果是别人的房产
        if (StrUtil.isNotBlank(owner) && !StrUtil.equalsIgnoreCase(owner, playerName) && position.getAllowBuy()) {
            sendMsg(PAY_TOLL, playerName, position);
        }

        // 不允许买的位置有命运，机会，停车场，坐牢，财产税，所得税
        if (!position.getAllowBuy()) {
            // 地皮的位置
            Integer positionIndex = position.getPosition();
            // 所得税
            if (positionIndex == 4 && StrUtil.equalsIgnoreCase("所得税", name)) {
                sendMsg(TAX, playerName, 4, null);
            }
            // 财产税
            if (positionIndex == 39 && StrUtil.equalsIgnoreCase("财产税", name)) {
                sendMsg(TAX, playerName, 39, null);
            }
            // 机会
            if (StrUtil.equalsIgnoreCase("机会", name) && actionFlag) {
                List<LuckEntity> chanceCards = ZillionaireUtil.chanceCards;
                Collections.shuffle(chanceCards);
                LuckEntity chanceCard;
                if (StrUtil.isNotBlank(outJailLicence)) {
                    // 如果有人有出狱许可证的话则不允许随机到出狱许可证
                    do {
                        chanceCard = RandomUtil.randomEle(chanceCards);
                    } while (chanceCard.getId() != 2);
                } else {
                    chanceCard = RandomUtil.randomEle(chanceCards);
                }
                sendRefreshTipsMsg(playerName, "【机会】 %s,%s", chanceCard.getTitle(), chanceCard.getAction());
                sendMsg(CHANCE, playerName, chanceCard);
            }
            // 命运
            if (StrUtil.equalsIgnoreCase("命运", name) && actionFlag) {
                List<LuckEntity> destinyCards = ZillionaireUtil.destinyCards;
                Collections.shuffle(destinyCards);
                LuckEntity destinyCard =  RandomUtil.randomEle(destinyCards);;
                sendMsg(DESTINY, playerName, destinyCard);
            }
            // 入狱
            if (positionIndex == 30 && StrUtil.equalsIgnoreCase("入狱", name)) {
                sendMsg(TO_JAIL, playerName, null);
            }
            // 停车场
            if (positionIndex == 20 && StrUtil.equalsIgnoreCase("停车场", name)) {
                sendMsg(REST, playerName, null);
            }

            // 监狱
            if (positionIndex == 10 && StrUtil.equalsIgnoreCase("监狱", name)) {
                sendRefreshTipsMsg(playerName, "路过了监狱");
            }
        }

        if (robotControl) {
            sendMsg(PASS, playerName, null);
        }
    }

    /**
     * 拿取起点的200块
     *
     * @param player 玩家节点
     * @param toJail 是否入狱
     */
    private void addStartMoney(Player player, Boolean toJail) {
        PlayerNode playerNode = player.getPlayerNode();
        if (!toJail) {
            playerNode.setCash(playerNode.getCash() + startMoney);
            playerNode.setProperty(playerNode.getProperty() + startMoney);
            player.refreshTips(positionMap.get(playerNode.getPosition()));
        }
    }

    /**
     * 发送刷新提示消息
     *
     * @param playerName 玩家名称
     * @param data       消息
     */
    private void sendRefreshTipsMsg(String playerName, Object data) {
        sendMsg(REFRESH_TIPS, playerName, String.format("【%s】: %s", playerName, data));
    }

    /**
     * 刷新日志展示玩家金钱
     *
     * @param playerName      玩家姓名
     * @param lastCash        上一次的现金
     * @param currentCash     当前现金
     * @param lastProperty    上一次的资产
     * @param currentProperty 当前资产
     */
    private void sendRefreshPropertyTipsMsg(String playerName, Integer lastCash, Integer currentCash, Integer lastProperty, Integer currentProperty) {
        sendRefreshTipsMsg(playerName,
                "【%s】 现金:%d -> %d, 资产 %d -> %d",
                playerName, lastCash, currentCash, lastProperty, currentProperty);
    }

    /**
     * 发送刷新提示消息
     *
     * @param playerName 玩家姓名
     * @param formatStr  格式化字符串
     * @param data       模板替换内容
     */
    private void sendRefreshTipsMsg(String playerName, String formatStr, Object... data) {
        sendMsg(REFRESH_TIPS, playerName, String.format(formatStr, data));
    }

    private void sendMsg(MsgType msgType, String player, Object data) {
        sendMsg(msgType, player, null, data);
    }

    private void sendMsg(MsgType msgType, String player, Integer action, Object data) {
        if (status == 0) {
            return;
        }
        MonopolyGameDto dto = new MonopolyGameDto();
        dto.setMsgType(msgType);
        dto.setPlayer(player);
        dto.setData(data);
        dto.setActionId(action);
        if (getRoom() != null) {
            sendMsg(dto);
        }
        invoke(() -> handle(dto));
    }

    /**
     * 添加组件
     *
     * @param parent 父级组件
     * @param child  子组件
     */
    private void addAll(JComponent parent, Component... child) {
        if (child.length == 0) {
            return;
        }
        for (Component component : child) {
            parent.add(component);
        }
    }

    @Override
    public void playerLeft(User player) {
        super.playerLeft(player);
        if (status > 0 && status < 4) {
            status = 4;
            String msg = "游戏结束！" + player.getUsername() + "逃跑了~";
            String tips = "溜了~";
            showTips(msg);
            Player leftPlayer = playerMap.get(player.getUsername());
            gameOverButton.setVisible(true);
        }
    }

    /**
     * 获取是否是活动玩家
     * 如果当前的currentPlayer为空的话默认返回false
     * 否则判断当前玩家昵称是否与currentPlayer的昵称一直
     * @return Boolean 是否是活动玩家
     */
    private Boolean isActionPlayer(){
        if (null == currentPlayer) {
            return false;
        }
        return StrUtil.equalsIgnoreCase(currentPlayer.getPlayer(), GameAction.getNickname());
    }
}
