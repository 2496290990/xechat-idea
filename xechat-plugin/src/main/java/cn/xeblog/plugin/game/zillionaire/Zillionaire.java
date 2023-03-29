package cn.xeblog.plugin.game.zillionaire;

import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.zillionaire.dto.CityDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.PlayerDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.StationDto;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.landlords.PlayerNode;
import cn.xeblog.plugin.game.zillionaire.enums.WindowMode;
import cn.xeblog.plugin.game.zillionaire.ui.PositionUi;
import com.intellij.openapi.ui.ComboBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * @author eleven
 * @date 2023/3/20 11:20
 * @apiNote  大富翁
 */
@DoGame(Game.ZILLIONAIRE)
public class Zillionaire extends AbstractGame<PositionDto>{
    public static WindowMode windowMode;

    /** 开始 */
    private JPanel startPanel;
    /** 游戏结束按钮 */
    private JButton gameOverButton;
    /** 标题 */
    private JLabel titleLabel;
    /** 返回游戏 */
    private JButton backButton;
    /**
     * 提示标签
     */
    private JLabel tipsLabel;
    /**
     * 用户列表
     */
    private List<String> userList;
    /**
     * ai玩家列表
     */
    private static List<String> aiPlayerList;

    /**
     * 当前玩家
     */
    private PlayerNode currentPlayer;
    /**
     * ai玩家行动地图
     */
    private Map<String, PlayerDto> aiPlayerMap;
    private Map<String, PlayerDto> playerMap;
    /**
     * 当前游戏模式
     */
    private GameMode gameMode;

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

    private JButton getBackButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

    @Override
    protected void allPlayersGameStarted() {
        if (isHomeowner()) {
            showGamePanel();
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
        showTips("请等待...");

        if (userList.size() < 2) {
            showTips("正在加入机器人...");
        } else {
            showTips("等待发牌...");
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

        JPanel textPanel = new JPanel();
        tipsLabel = new JLabel("测试刷新游戏页面");
        tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tipsLabel.setPreferredSize(new Dimension(200, 40));
        textPanel.add(tipsLabel);
        centerPanel.add(textPanel);
        // 初始化游戏区域
        initPlayAreaCenterPanel(centerPanel);
        initPlayAreaRightPanel(rightPanel);
        initPlayAreaBottomPanel(bottomPanel);
        initPlayAreaLeftPanel(leftPanel);
        initPlayAreaTopPanel(topPanel);
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
            mainBottomPanel.add(backButton);
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
        //JPanel placeholderPanel = new JPanel();
        //placeholderPanel.setMinimumSize(new Dimension(5, 0));
        //mainPanel.add(placeholderPanel, BorderLayout.WEST);
        //mainPanel.add(placeholderPanel, BorderLayout.EAST);
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

    private void initPlayAreaCenterPanel(JPanel jPanel) {
        JLabel label = new JLabel("添加中央的游戏面板");
        jPanel.add(label);
        jPanel.setBorder(new LineBorder(new Color(255, 100, 0), 1));
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
        JPanel userPanel = new JPanel();
        userPanel.setMinimumSize(new Dimension(5, 0));
        JPanel userItem = new JPanel();
        userItem.add(new JLabel("玩家1"));
        userItem.add(new JLabel("献祭1000"));
        userItem.add(new JLabel("现金1000"));
        for (int i = 0; i < 6; i++) {
            userItem.setBorder(new LineBorder(new Color(255, 100, 0), 4));
            userPanel.add(userItem);
        }
        userPanel.setBorder(new LineBorder(new Color(255, 100, 0), 4));
        return userPanel;
    }

    private JPanel flushUserPanel(){
        return null;
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
        PlayerNode startNode = null;
        PlayerNode playerNode = null;
        playerMap = new HashMap<>();
        List<String> roomUserList = userList;
        int usersTotal = roomUserList.size();

        for (int i = 0; i < usersTotal; i++) {
            PlayerNode node = new PlayerNode();
            node.setPlayer(roomUserList.get(i));
            node.setPokerTotal(17);
            node.setAlias("Machine 0" + (i + 1));
            //playerMap.put(node.getPlayer(), new PlayerDto(node, null));

            if (GameAction.getNickname().equals(node.getPlayer())) {
                currentPlayer = node;
                //helpPlayerAction = new AIPlayerAction(currentPlayer);
            }

            if (aiPlayerMap.containsKey(node.getPlayer())) {
                //aiPlayerMap.put(node.getPlayer(), new AIPlayerAction(node));
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

    @AllArgsConstructor
    @Getter
    private enum GameMode {
        BROKE_EXIT("破产结束"),
        ONLY_ONE("一人胜出");

        private String name;

        public static Zillionaire.GameMode getMode(String name) {
            for (Zillionaire.GameMode model : values()) {
                if (model.name.equals(name)) {
                    return model;
                }
            }

            return Zillionaire.GameMode.BROKE_EXIT;
        }
    }
}
