package cn.xeblog.plugin.game.zillionaire;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.zillionaire.dto.CityDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.CompanyDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.StationDto;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.zillionaire.action.AiPlayerAction;
import cn.xeblog.plugin.game.zillionaire.action.PlayerAction;
import cn.xeblog.plugin.game.zillionaire.dto.MonopolyGameDto;
import cn.xeblog.plugin.game.zillionaire.dto.Player;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;
import cn.xeblog.plugin.game.zillionaire.enums.GameMode;
import cn.xeblog.plugin.game.zillionaire.enums.MsgType;
import cn.xeblog.plugin.game.zillionaire.enums.WindowMode;
import cn.xeblog.plugin.game.zillionaire.ui.PositionUi;
import cn.xeblog.plugin.game.zillionaire.utils.ZillionaireUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static cn.xeblog.plugin.game.zillionaire.enums.MsgType.*;


/**
 * @author eleven
 * @date 2023/3/20 11:20
 * @apiNote  大富翁
 */
@DoGame(Game.ZILLIONAIRE)
public class Zillionaire extends AbstractGame<MonopolyGameDto>{
    public static WindowMode windowMode;

    /** 开始 */
    private JPanel startPanel;
    /** 游戏结束按钮 */
    private JButton gameOverButton;
    /** 标题 */
    private JLabel titleLabel;
    /** 返回游戏 */
    private JButton backButton;
    /** 游戏说明 */
    private JButton helpBtn;
    /** 作弊码 */
    private JTextField cheatCode;
    /** 作弊码按钮 */
    private JButton cheatCodeBtn;
    // 随机数标签
    private JLabel randomLabel = new JLabel();
    /**
     * 提示标签
     */
    private JLabel tipsLabel;
    /** 玩家主要的面板 */
    private JPanel playerMainPanel;
    /** 玩家头部面板 */
    private JPanel playerTopPanel;
    /**
     * 位置主面板
     */
    private JPanel positionMainPanel;
    /**
     * 职位面板
     */
    private JPanel positionsPanel;

    private JPanel userListPanel;
    /** 投掷骰子 */
    JButton randomBtn;
    /** 升级建筑 */
    JButton buildBtn;
    /** 购买地皮 */
    JButton buyBtn;
    /** 售卖 */
    JButton saleBtn;
    /** 赎回 */
    JButton redemptionBtn;
    /** 过按钮 */
    JButton passBtn;

    private Queue<String> tipsQueue = new ArrayDeque<>(5);
    /**
     * 当前玩家
     */
    private PlayerNode currentPlayer;

    /**
     * 玩家的建筑
     */
    private List<? extends PositionDto> userPosition;
    /**
     * 已选中的地皮
     */
    private List<? extends PositionDto> selectedPosition;
    /**
     * ai玩家行动地图
     */
    private Map<String, Player> aiPlayerMap;
    /**
     * 玩家键值对
     */
    private Map<String, Player> playerMap;
    /** 位置点位集合 */
    private Map<Integer, PositionDto> positionMap;
    /**
     * 用户列表
     */
    private List<String> userList;
    /**
     * ai玩家列表
     */
    private static List<String> aiPlayerList;
    /** ai处理类 */
    private Map<String, PlayerAction> aiPlayerActionMap;

    private PlayerAction helpPlayerAction;
    /** 总步数 40 */
    private static final Integer ALL_STEP = 40;

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
    /** 游戏状态 0 初始化 1游戏中  2 结束*/
    private Integer status;

    @Override
    protected void init() {
        initStartPanel();
    }

    private void initStartPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(400, 400));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 260);
        mainPanel.add(startPanel);

        // 添加游戏标题
        JLabel title = new JLabel("大富翁！");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        JLabel modelLabel = new JLabel("游戏模式：");
        modelLabel.setFont(new Font("", 1, 13));
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
            for (GameMode mode :GameMode.values()) {
                gameModeList.add(mode.getName());
            }

            vBox.add(getCreateRoomButton(numsList, gameModeList));
        }
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    private void initValue() {
        userList = new ArrayList<>();
        aiPlayerMap = new HashMap<>();
        status = 0;
        currentPlayer = null;
        playerMap = null;
        userList = new ArrayList<>();
        aiPlayerActionMap = new HashMap<>();
        userListPanel = new JPanel();
    }

    private void initBtnStatus(){
        String nickname = GameAction.getNickname();
        // 获取当前玩家
        Player player = playerMap.get(nickname);
        PlayerNode playerNode = player.getPlayerNode();
        // 获取玩家当前的位置
        PositionDto position = positionMap.get(playerNode.getPosition());
        boolean actionFlag = StrUtil.equalsIgnoreCase(nickname, currentPlayer.getPlayer());
        // 检测玩家是否有售出地皮
        long count = playerNode.getPositions().stream()
                .filter(item -> !item.getPositionStatus())
                .count();
        // 当前点位是否有拥有者
        boolean buildBtnStatus = StrUtil.equalsIgnoreCase(position.getOwner(), nickname) && position.getAllowBuy() && position.getUpgradeAllowed();
        boolean buyBtnStatus = StrUtil.isBlank(position.getOwner()) && position.getAllowBuy();
        refreshBtnStatus(actionFlag, buyBtnStatus, buildBtnStatus, true, count > 0 ,false);
    }

    /**
     * 刷新按钮状态
     * @param randomBtnStatus           投掷骰子按钮状态
     * @param buyBtnStatus              购买地皮按钮状态
     * @param buildBtnStatus            升级建筑按钮状态
     * @param saleBtnStatus             售卖建筑按钮状态
     * @param redemptionBtnStatus       赎回地皮按钮状态
     * @param passBtnStatus             过按钮状态
     */
    private void refreshBtnStatus(Boolean randomBtnStatus, Boolean buyBtnStatus, Boolean buildBtnStatus,
                                  Boolean saleBtnStatus, Boolean redemptionBtnStatus, Boolean passBtnStatus) {
        // 如果当前玩家是操作玩家的话
        if (StrUtil.equalsIgnoreCase(GameAction.getNickname(), currentPlayer.getPlayer())) {
            randomBtn.setEnabled(null == randomBtnStatus ? randomBtn.isEnabled() : randomBtnStatus);
            buyBtn.setEnabled(null == buyBtnStatus ? buyBtn.isEnabled() : buyBtnStatus);
            buildBtn.setEnabled(null == buildBtnStatus ? buildBtn.isEnabled() : buildBtnStatus);
            saleBtn.setEnabled(null == saleBtnStatus ? saleBtn.isEnabled() : saleBtnStatus);
            redemptionBtn.setEnabled(null == redemptionBtnStatus ? redemptionBtn.isEnabled() : redemptionBtnStatus);
            passBtn.setEnabled(null == passBtnStatus ? passBtn.isEnabled() : passBtnStatus);
        }
    }

    /**
     * 刷新单个按钮的状态
     * @param btn           按钮
     * @param btnStatus     按钮状态
     */
    private void refreshBtnStatus(JButton btn, Boolean btnStatus){
        btn.setEnabled(null == btnStatus ? btn.isEnabled() : btnStatus);
    }

    /**
     * 获取开始游戏按钮
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
            int roomUsers = 6;
            GameRoom room = this.getRoom();
            if (null != room) {
                roomUsers = room.getNums();
            }
            int nums = roomUsers - usersTotal;
            invoke(() -> {
                if (nums > 0) {
                    List<String> joinedAIList = new ArrayList<>(aiPlayerList);
                    joinedAIList.removeAll(userList);
                    // 对AI随机洗牌
                    Collections.shuffle(joinedAIList);
                    List<String> aiList = joinedAIList.subList(0, nums);
                    aiList.forEach(ai -> aiPlayerActionMap.put(ai, null));
                    sendMsg(MsgType.JOIN_ROBOTS, GameAction.getNickname(), new ArrayList<>(aiList));
                    initUserPanel();
                } else {
                    showGamePanel();
                }
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
        showTips("请等待...");

        if (userList.size() < 2) {
            showTips("正在加入机器人...");
        } else {
            showTips("等待开始...");
        }

        if (gameRoom == null) {
            allPlayersGameStarted();
        }
    }

    private void showGamePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(490, 350));

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(480, 300));
        panel.setLayout(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(400,30));
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(60,280));
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(60,280));
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(400,30));
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(280,280));
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
        titleLabel.setFont(new Font("", 1, 14));
        mainTopPanel.add(titleLabel);

        JPanel mainBottomPanel = new JPanel();
        if (getRoom() == null) {
            backButton = getBackButton();
            helpBtn = new JButton("帮助说明");
            helpBtn.addActionListener(e -> {
                AlertMessagesUtil.showInfoDialog("游戏说明","游戏规则:\n" +
                        "1. 每次开始的时候先投骰子，然后移动玩家位置。\n" +
                        "2. 如果到达位置的地皮没有人买则可以买地皮，如果是自己的地皮就可以盖房子，别人的地皮就需要给她钱 \n" +
                        "3. 机会和命运需要抽卡牌，财产税和所得税会扣钱\n" +
                        "4. 到达起点的时候就会拿到2000块\n" +
                        "5. 坐牢会直接移动到监狱位置，并且拿不到起点的2000块，入狱会休息一轮\n" +
                        "6. 停车场也会休息一轮,路过监狱不会休息\n" +
                        "8. 现金不够的时候可以卖房卖地，半价出售\n" +
                        "9. 如果一名玩家拥有同颜色的所有地皮则该颜色的空地过路费翻倍\n" +
                        "10. 经过自来水公司和电力公司需要投掷骰子，一个公司则点数*10 两个则点数*100\n"

                );
            });
            cheatCode = new JTextField("作弊码");
            cheatCodeBtn = new JButton("激活作弊码");
            cheatCodeBtn.addActionListener(e -> {
                String cheatCodeStr = cheatCode.getText();
                if (StrUtil.isNotBlank(cheatCodeStr)) {
                    if (StrUtil.equalsIgnoreCase("↑↑↓↓←←→→BABA", cheatCodeStr)) {
                        AlertMessagesUtil.showInfoDialog("游戏提示", "你这不对劲哦");
                    }
                    
                    if (StrUtil.equalsIgnoreCase("PANZER", cheatCodeStr)) {
                        // TODO: 2023/3/31 所有玩家增加5000块 
                    }
                    
                    if (StrUtil.equalsIgnoreCase("ALLIN", cheatCodeStr)) {
                        // TODO: 2023/3/31 玩家直接胜利
                        AlertMessagesUtil.showInfoDialog("游戏提示", "想啥呢小伙子，还梭哈");
                    }

                    if (cheatCodeStr.contains("clean cash")) {
                        // TODO: 2023/3/31 随机清空一位玩家的现金
                    }

                    if (cheatCodeStr.contains("clean property")) {
                        // TODO: 2023/3/31 随机清空一位玩家的房产
                    }
                    if (cheatCodeStr.contains("Occupy the house")) {
                        // TODO: 2023/3/31 随机获取一位玩家的所有房子
                        AlertMessagesUtil.showInfoDialog("游戏提示", "警告一次！");
                    }
                }
            });
            addAll(mainBottomPanel, helpBtn, cheatCode, cheatCodeBtn , backButton);
        }
        gameOverButton = getGameOverButton();
        gameOverButton.setVisible(false);
        mainBottomPanel.add(gameOverButton);

        Box hBox = Box.createHorizontalBox();
        hBox.add(new JLabel("Window: "));
        hBox.add(getWindowModeComboBox());
        hBox.add(Box.createHorizontalStrut(5));
        mainBottomPanel.add(hBox);

        mainPanel.add(mainTopPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(mainBottomPanel, BorderLayout.SOUTH);
        mainPanel.add(initUserPanel(), BorderLayout.EAST);
        mainPanel.updateUI();

    }

    /**
     * 获取卡牌
     * @param form      开始
     * @param to        结束
     * @return  List    坐标点
     */
    private List<PositionUi> getPositions(Integer form, Integer to){
        List<PositionDto> positions = ZillionaireUtil.positionDtoList;
        positionMap= positions.stream()
                .collect(Collectors.toMap(PositionDto::getPosition, p -> p));
        List<PositionDto> subPositions = positions.subList(form, to);
        PositionUi positionUi = null;
        CityDto city = null;
        StationDto station = null;
        List<PositionUi> positionUis = new ArrayList<>(to - form);
        for (PositionDto subPosition : subPositions) {
            if (subPosition instanceof CityDto) {
                city = (CityDto) subPosition;
                positionUi = new PositionUi(subPosition.getPosition(),subPosition.getName(), subPosition.getOwner(),
                        city.getLevel(),city.getPrice(),city.getBuildMoney(), city.getToll(), city.getColor());
            } else if (subPosition instanceof StationDto) {
                station = (StationDto)subPosition;
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
     * @param jPanel
     */
    private void initPlayAreaTopPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(1, 11));
        initPositionUi(jPanel, 0, 11, false);
    }

    private void initPlayAreaRightPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(9, 1));
        initPositionUi(jPanel, 11, 20, false );
    }

    private void initPlayAreaBottomPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(1, 11));
        initPositionUi(jPanel, 20, 31, true);
    }
    private void initPlayAreaLeftPanel(JPanel jPanel) {
        jPanel.setLayout(new GridLayout(9, 1));
        initPositionUi(jPanel, 31, 40, true );
    }

    private void initPlayAreaCenterPanel(JPanel centerPanel) {
        centerPanel.setLayout(new GridLayout(3,1));
        JPanel textPanel = new JPanel();
        tipsLabel = new JLabel("测试刷新游戏页面");
        tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tipsLabel.setPreferredSize(new Dimension(200, 40));
        textPanel.add(tipsLabel);
        centerPanel.add(textPanel);
        centerPanel.add(randomLabel);
        centerPanel.add(centerGameButton());
        centerPanel.setBorder(new LineBorder(new Color(255, 100, 0), 1));
    }

    private JPanel centerGameButton() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,6));
        randomBtn = new JButton("投掷骰子");
        buildBtn = new JButton("升级建筑");
        buyBtn = new JButton("购买地皮");
        saleBtn = new JButton("售卖建筑");
        redemptionBtn = new JButton("赎回建筑");
        passBtn = new JButton("过");
        addAll(buttonPanel, randomBtn, buildBtn, buyBtn, saleBtn, redemptionBtn, passBtn);

        String nickname = GameAction.getNickname();
        Player player = playerMap.get(nickname);
        PlayerNode playerNode = player.getPlayerNode();
        randomBtn.addActionListener(e -> {
            int randomInt = RandomUtil.randomInt(2, 12);
            randomLabel.setText("当前投掷点数为: " + randomInt);
            MonopolyGameDto msgDto = new MonopolyGameDto();
            msgDto.setCurrentPlayer(currentPlayer);
            msgDto.setMsgType(DICE_ROLL);
            msgDto.setData(randomInt);
            msgDto.setPlayer(nickname);
            sendMsg(msgDto);

        });
        buildBtn.addActionListener(e -> {
            // 获取玩家当前的位置
            Integer userPosition = playerNode.getPosition();
            PositionDto position = positionMap.get(userPosition);
            // 只有城市允许升级
            if (position.getAllowBuy() && position.getUpgradeAllowed()) {
                Integer positionPrice = getPositionPrice(position);
                CityDto city = (CityDto)position;
                Integer buildMoney = city.getBuildMoney();
                Integer cash = playerNode.getCash();
                if (cash >= buildMoney) {
                    playerNode.setCash(cash - buildMoney);
                    playerNode.setProperty(playerNode.getProperty() - buildMoney / 2);
                    player.refreshTips(position);
                    sendRefreshTipsMsg(nickname,
                            String.format("%s: 升级了【%s】的房屋,当前等级 %d, 过路费 %d",
                                    nickname, position.getName(), city.getLevel(), city.getToll()
                            )
                    );
                    refreshUserPosition(playerNode, position);
                    // 购买之后禁止购买
                    refreshBtnStatus(buyBtn, playerNode.getCash() >= buildMoney);
                    // 刷新回合结束按钮
                    refreshBtnStatus(passBtn, true);
                }
            }
        });

        buyBtn.addActionListener(e -> {
            // 获取玩家当前的位置
            Integer userPosition = playerNode.getPosition();
            PositionDto position = positionMap.get(userPosition);
            if (position.getAllowBuy()) {
                Integer positionPrice = getPositionPrice(position);
                Integer cash = playerNode.getCash();
                if (cash >= positionPrice) {
                    playerNode.setCash(cash - positionPrice);
                    playerNode.setProperty(playerNode.getProperty() - positionPrice / 2);
                    player.refreshTips(position);
                    sendRefreshTipsMsg(nickname, String.format("%s: 购买了【%s】 地皮", nickname, position.getName()));
                    // 购买之后禁止购买
                    refreshBtnStatus(buyBtn, false);
                }

            }
        });

        saleBtn.addActionListener(e -> {
            // TODO: 2023/3/29 开发售卖建筑或地皮功能

        });

        redemptionBtn.addActionListener( e -> {
            // TODO: 2023/3/29 开发赎回功能
        });
        passBtn.addActionListener(e -> {
            AlertMessagesUtil.showInfoDialog("提示", "玩家跳过");
            if (null != currentPlayer) {
                currentPlayer = currentPlayer.getNextPlayer();
            }
        });
        return buttonPanel;
    }

    /**
     * 刷新用户的地皮
     * @param playerNode    玩家节点
     * @param position      地皮
     */
    private void refreshUserPosition(PlayerNode playerNode, PositionDto position) {
        // 重置地皮集合
        List<PositionDto> positions = playerNode.getPositions();
        positions.add(position);
        playerNode.setPositions(positions);

        if (position instanceof CityDto) {
            List<CityDto> cities = playerNode.getCities();
            cities.add((CityDto) position);
            playerNode.setCities(cities);
        }

        if (position instanceof StationDto) {
            List<StationDto> stations = playerNode.getStations();
            stations.add((StationDto)position);
            playerNode.setStations(stations);
        }

        if (position instanceof CompanyDto) {
            List<CompanyDto> companies = playerNode.getCompanies();
            companies.add((CompanyDto)position);
            playerNode.setCompanies(companies);
        }
        // TODO: 2023/3/31 刷新用户位置 
        
    }

    /**
     * 获取地皮的价格
     * @param position
     * @return
     */
    private Integer getPositionPrice(PositionDto position){
        if (position instanceof CityDto) {
            return ((CityDto) position).getPrice();
        }

        if (position instanceof StationDto) {
            return ((StationDto) position).getPrice();
        }

        if (position instanceof CompanyDto) {
            return ((CompanyDto) position).getPrice();
        }
        return null;
    }

    /**
     * 初始化坐标点位UI
     * @param jPanel    组件
     * @param from      开始
     * @param to        结束
     * @param reverse   是否反转
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

    private JPanel initUserPanel(){
        userListPanel.removeAll();
        userListPanel.setLayout(new GridLayout(userList.size(), 1));
        JLabel tipsLabel = null;
        StringBuffer sb = null;
        for (String username : playerMap.keySet()) {
            Player player = playerMap.get(username);
            JPanel panel = player.getPanel();
            panel.setLayout(new FlowLayout());
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
                    .append("状态: ").append(playerNode.getStatus() ? "正常" : "入狱").append(br)
                    .append("位置: ").append(0).append(white)
                    .append("名称: ").append("起点 ").append(br)
                    .append("现金: ").append(playerNode.getCash()).append(white)
                    .append("资产: ").append(playerNode.getProperty()).append(br)
             .append("</html>");
            tipsLabel = new JLabel(sb.toString());
            panel.add(tipsLabel);
            player.setTipsLabel(tipsLabel);
            userListPanel.add(panel);
        }
        userListPanel.updateUI();
        initBtnStatus();
        return userListPanel;
    }

    private Color playerTipsColor = new Color(241, 135, 135);

    private void flushPlayerPanel(Player player) {
        PlayerNode playerNode = player.getPlayerNode();
        JPanel panel = player.getPanel();
        panel.removeAll();

        String nickname = playerNode.getPlayer();
        JLabel nicknameLabel = new JLabel("<html>" + nickname + "</html>");
        JLabel cashLabel = new JLabel();

        if (playerNode == currentPlayer) {
            playerMainPanel = new JPanel();
            playerTopPanel = new JPanel();
            positionMainPanel = new JPanel(new BorderLayout());
            positionsPanel = new JPanel();

            panel.add(playerTopPanel);
            panel.add(playerMainPanel);

            flushWindowMode();
            flushPositions();

            JBScrollPane pokersScroll = new JBScrollPane(positionsPanel);
            pokersScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pokersScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            positionMainPanel.add(pokersScroll);
            playerMainPanel.add(positionMainPanel);
            nicknameLabel.setText(nickname);
            nicknameLabel.setPreferredSize(new Dimension(300, 30));
            playerMainPanel.add(nicknameLabel);
            playerMainPanel.add(cashLabel);
        } else {
            JPanel otherPlayerPanel = new JPanel();
            otherPlayerPanel.setLayout(new BorderLayout());
            otherPlayerPanel.setPreferredSize(new Dimension(120, 100));
            otherPlayerPanel.setBorder(BorderFactory.createEtchedBorder());
            nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nicknameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
            otherPlayerPanel.add(nicknameLabel, BorderLayout.NORTH);
            int alignment = 1;
            Border border = BorderFactory.createEmptyBorder(0, 5, 5, 0);
            cashLabel.setBorder(border);
            cashLabel.setHorizontalAlignment(alignment);
            otherPlayerPanel.add(cashLabel, BorderLayout.SOUTH);

            JLabel tipsLabel = new JLabel("");
            tipsLabel.setForeground(playerTipsColor);
            tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            player.setTipsLabel(tipsLabel);
            otherPlayerPanel.add(tipsLabel, BorderLayout.CENTER);

            panel.add(otherPlayerPanel);
        }
        panel.updateUI();
    }

    /**
     * 刷新地皮UI
     */
    private void flushPositions(){

    }

    private ComboBox getWindowModeComboBox() {
        ComboBox comboBox = new ComboBox();
        for (WindowMode mode : WindowMode.values()) {
            comboBox.addItem(mode.getName());
            if (windowMode == mode) {
                comboBox.setSelectedItem(mode.getName());
            }
        }
        comboBox.addItemListener(e -> {
            windowMode =WindowMode.getMode(comboBox.getSelectedItem().toString());
            flushWindowMode();
        });
        return comboBox;
    }

    /**
     * 刷新窗口模式
     */
    private void flushWindowMode() {
        int width = 490;
        int topWidth = 200;
        if (windowMode == WindowMode.ADAPTIVE) {
            // 重置卡牌宽度
            //int pokerSize = CollectionUtil.size(pokers);
            //pokerSize = Math.max(currentPlayer.getRole() == 2 ? 20 : 17, pokerSize);
            //width = pokerSize * 36;
            //if (pokerSize == 20) {
            //    topWidth = 300;
            //}
        }

        mainPanel.setMinimumSize(new Dimension(width, 350));
        tipsLabel.setPreferredSize(new Dimension(topWidth, 40));

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
        PlayerNode node = null;
        // 创建玩家节点
        for (int i = 0; i < usersTotal; i++) {
            node =  new PlayerNode();
            node.setPlayer(roomUserList.get(i));
            node.setAlias("Machine 0" + (i + 1));
            playerMap.put(node.getPlayer(), new Player(node, new JPanel()));

            if (GameAction.getNickname().equals(node.getPlayer())) {
                currentPlayer = node;
                helpPlayerAction = new AiPlayerAction(currentPlayer);
            }

            if (aiPlayerMap.containsKey(node.getPlayer())) {
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

    private void showTips(String tips) {
        tipsLabel.setText("<html>" + tips + "</html>");
        tipsLabel.updateUI();
    }


    @Override
    public void handle(MonopolyGameDto body) {
        if (status == 0){
            return;
        }
        String playerName = body.getPlayer();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        PlayerNode nextPlayerNode = playerNode.getNextPlayer();
        Player nextPlayer = null;
        PlayerAction aiPlayerAction = null;
        if (nextPlayerNode != null) {
            nextPlayer = playerMap.get(nextPlayerNode.getPlayer());
            aiPlayerAction = aiPlayerActionMap.get(nextPlayerNode.getPlayer());
        }

        boolean isHomeowner = isHomeowner();
        boolean isMe = playerNode == currentPlayer;
        boolean controlRobot = isHomeowner && aiPlayerAction != null;

        switch (body.getMsgType()) {
            case JOIN_ROBOTS:
                joinRobots(body, isHomeowner);
                break;
            case DICE_ROLL:
                diceRoll(body);
                break;
            case REFRESH_TIPS:
                refreshTips(body);
            default:
                break;
        }
    }

    /**
     * 刷新提示
     * @param body
     */
    private void refreshTips(MonopolyGameDto body){
        if (tipsQueue.size() == 5) {
            tipsQueue.poll();
        }
        tipsQueue.add(String.valueOf(body.getData()));
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        tipsQueue.forEach(item -> sb.append(item).append("<br />"));
        sb.append("</html>");
        tipsLabel.setText(sb.toString());
        tipsLabel.updateUI();
    }

    /**
     * 加入人机玩家
     * @param body          消息体
     * @param isHomeowner   是否房主
     */
    private void joinRobots(MonopolyGameDto body, Boolean isHomeowner){
        List<String> robotList = (List<String>) body.getData();
        userList.addAll(robotList);
        buildPlayerNode();
        showGamePanel();
        if (isHomeowner) {
            // todo 开始游戏
        }
    }

    private void diceRoll(MonopolyGameDto body){
        Integer step = (Integer)body.getData();
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
        sendRefreshTipsMsg(playerName, String.format("%s向前行走了%d步,当前位置%s", playerName, step, position.getName()));
        refreshBtnStatus(false, null, null, null, null, position.getAllowBuy());
        // 上一次的圈数
        int lastCylinderNumber = lastPosition / ALL_STEP;
        int currentCylinderNumber = currentPosition / ALL_STEP;
        // 如果走完一圈加2000块
        if (currentCylinderNumber - lastCylinderNumber > 0) {
            addStartMoney(player, false);
        }
        // 如果当前点位允许购买并且当前没有拥有者
        String owner = position.getOwner();
        if (StrUtil.isBlank(owner) && position.getAllowBuy()) {
            // 更新购买按钮状态
            refreshBtnStatus(null, true, null, null, null, null);
        }
        // 本人的地皮并且支持购买升级
        if (StrUtil.equalsIgnoreCase(owner, playerName) &&
                position.getAllowBuy() &&
                position.getUpgradeAllowed()) {
            refreshBtnStatus(null, true, null, null, null, null);
        }
        // 如果是别人的房产
        if (StrUtil.isNotBlank(owner) && !StrUtil.equalsIgnoreCase(owner, playerName)) {

        }


    }

    /**
     * 获取过路费
     * @param position  地皮
     * @return Integer  过路费
     */
    private Integer positionToll(PositionDto position) {
        String owner = position.getOwner();
        Player player = playerMap.get(owner);
        PlayerNode playerNode = player.getPlayerNode();
        if (position instanceof CityDto) {
            CityDto city = (CityDto) position;
            Boolean hasAll = isDouble(playerNode, city);
            // 如果改名玩家拥有当前颜色的全部房产并且当前地皮是空地则收取双倍过路费
            return hasAll ? 2 * city.getZeroToll() : city.getToll();
        }

        if (position instanceof StationDto) {
            return ((StationDto) position).getToll();
        }

        // 如果是公司的话需要重新投掷
        if (position instanceof CompanyDto) {
            int toll = RandomUtil.randomInt(1, 12);

            List<CompanyDto> companies = playerNode.getCompanies();
            // 如果有一个公司则十倍否则一百倍
            int multiple = companies.size() == 1 ? 10 : 100;
            return toll * multiple;
        }
        return 0;
    }

    /**
     * 当前地皮的拥有者是否有全部颜色的地皮 并且当前的地皮是
     * @param playerNode        地皮拥有者
     * @param position      地皮
     * @return Boolean          是否拥有全部颜色的地皮
     */
    private Boolean isDouble(PlayerNode playerNode, CityDto position) {
        List<CityDto> cities = playerNode.getCities();
        // 获取当前位置的颜色
        Color color = position.getColor();
        // 获取所有的地皮
        Collection<PositionDto> values = positionMap.values();
        // 获取初始化中房屋的颜色
        long colorInitCount = values.stream()
                .filter(item -> item.getColor().equals(color))
                .count();

        long userColorCount = cities.stream()
                .filter(item -> item.getColor().equals(color))
                .count();
        boolean hasAll = colorInitCount == userColorCount;
        boolean zero = position.getLevel() == 0;
        return hasAll && zero;
    }

    /**
     * 拿取起点的200块
     * @param player        玩家节点
     * @param toJail        是否入狱
     */
    private void addStartMoney(Player player, Boolean toJail){
        PlayerNode playerNode = player.getPlayerNode();
        if (!toJail) {
            playerNode.setCash(playerNode.getCash() + 2000);
            playerNode.setProperty(playerNode.getProperty() + 2000);
            player.refreshTips(positionMap.get(playerNode.getPosition()));
        }
    }

    /**
     * 发送刷新提示消息
     * @param playerName    玩家名称
     * @param data          消息
     */
    private void sendRefreshTipsMsg(String playerName, Object data){
        sendMsg(REFRESH_TIPS, playerName, playerName + ": " + data);
    }

    private void sendMsg(MsgType msgType, String player, Object data) {
        if (status == 0) {
            return;
        }
        MonopolyGameDto  dto = new MonopolyGameDto ();
        dto.setMsgType(msgType);
        dto.setPlayer(player);
        dto.setData(data);
        sendMsg(dto);
    }

    @Override
    protected void sendMsg(GameDTO body) {
        if (getRoom() != null) {
            super.sendMsg(body);
        }
        invoke(() -> handle((MonopolyGameDto) body));
    }

    /**
     * 添加组件
     * @param parent 父级组件
     * @param child  子组件
     */
    private void addAll(JComponent parent, Component... child){
        if (child.length == 0) {
            return;
        }
        for (Component component : child) {
            parent.add(component);
        }
    }
}
