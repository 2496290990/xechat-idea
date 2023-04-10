package cn.xeblog.plugin.game.uno;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.uno.action.AiPlayerAction;
import cn.xeblog.plugin.game.uno.action.PlayerAction;
import cn.xeblog.plugin.game.uno.entity.Player;
import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import cn.xeblog.plugin.game.uno.entity.UNOGameDto;
import cn.xeblog.plugin.game.uno.enums.GameMode;
import cn.xeblog.plugin.game.uno.enums.MsgType;
import cn.xeblog.plugin.game.uno.enums.PlayerMode;
import com.intellij.openapi.ui.ComboBox;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static cn.xeblog.plugin.game.uno.enums.MsgType.*;


/**
 * @author eleven
 * @date 2023/4/8 22:24
 * @apiNote
 */
@DoGame(Game.UNO)
@Slf4j
public class UNO extends AbstractGame<UNOGameDto> {

    /**
     * 游戏模式
     */
    private GameMode gameMode;

    /**
     * 玩家模式
     */
    private PlayerMode playerMode;
    /**
     * 逆时针方向
     */
    private Boolean anticlockwise;
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
    /**
     * 出btn
     */
    private JButton outBtn;
    /**
     * 提示btn
     */
    private JButton tipsBtn;
    /**
     * uno btn
     */
    private JButton unoBtn;
    /**
     * 抓住btn
     */
    private JButton catchBtn;
    /**
     * 提示标签
     */
    private JTextArea tipsArea;
    /**
     * 玩家卡牌主面板
     */
    private JPanel cardPanel;
    /**
     * 队友卡牌面板
     */
    private JPanel teammateCardsPanel;
    /**
     * 当前玩家
     */
    private PlayerNode currentPlayer;

    /**
     * 玩家键值对
     */
    private Map<String, Player> playerMap;
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

    /**
     * 状态 -1 游戏结束 0初始化 1游戏中
     */
    private Integer status;

    private Integer tipsRows;

    private Map<String, String> teamMap;

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

    @Override
    protected void allPlayersGameStarted() {
        if (isHomeowner()) {
            int usersTotal = userList.size();
            // 指定最多4名玩家
            int roomUsers = 4;
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
                    sendMsg(JOIN_ROBOTS, GameAction.getNickname(), new ArrayList<>(aiList));
                    initUserPanel();
                }
                showGamePanel();
            }, 500);
        }
    }



    private void showGamePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(490, 350));
        mainPanel.add(new JTextField("上"), BorderLayout.NORTH);
        mainPanel.add(new JTextField("右"), BorderLayout.EAST);
        mainPanel.add(new JTextField("下"), BorderLayout.SOUTH);
        mainPanel.add(new JTextField("左"), BorderLayout.WEST);
        mainPanel.add(initCenter(), BorderLayout.CENTER);
    }

    private JPanel initCenter() {
        JPanel centerPanel = new JPanel();
        if (playerMode.equals(PlayerMode.DOUBLE)) {
        }
        tipsArea = new JTextArea("游戏开始");
        tipsArea.setRows(Math.max(tipsRows, 10));
        centerPanel.add(tipsArea);
        return centerPanel;
    }

    private void initUserPanel() {
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
            node.setPlayerName(roomUserList.get(i));
            node.setAlias("Machine 0" + (i + 1));
            playerMap.put(node.getPlayerName(), new Player(node, new JPanel()));

            if (GameAction.getNickname().equals(node.getPlayerName())) {
                currentPlayer = node;
                helpPlayerAction = new AiPlayerAction(currentPlayer);
            }

            if (aiPlayerActionMap.containsKey(node.getPlayerName())) {
                aiPlayerActionMap.put(node.getPlayerName(), new AiPlayerAction(node));
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


    /**
     * 获取返回游戏按钮
     */
    private JButton getBackButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

    @Override
    protected void start() {
        initValue();
        GameRoom gameRoom = getRoom();
        if (gameRoom != null) {
            userList.addAll(gameRoom.getUsers().keySet());
        } else {
            userList.add(GameAction.getNickname());
        }
        System.out.println(gameMode);
        System.out.println(playerMode);

        buildPlayerNode();
        showGamePanel();
        status = 1;
        showTips("请等待...");

        if (userList.size() < 2) {
            showTips("正在加入机器人...");
        } else {
            showTips("等待开始...");
        }

        // 玩家不够4名的时候或者房间为空直接开始游戏的
        if (gameRoom == null || userList.size() < 4) {
            allPlayersGameStarted();
        }
    }

    private void showTips(String tips) {
        tipsArea.setRows(Math.max(++tipsRows, 10));
        tipsArea.append(tips + "\n");
        tipsArea.updateUI();
    }
    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(400, 400));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 260);
        mainPanel.add(startPanel);

        // 添加游戏标题
        JLabel title = new JLabel("UNO！");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);
        // 游戏模式
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
        gameMode = GameMode.CLASSIC;
        gameModeBox.setSelectedItem(gameMode.getName());
        gameModeBox.addActionListener(l -> {
            GameMode selectedGameMode = GameMode.getMode(gameModeBox.getSelectedItem().toString());
            if (selectedGameMode != null) {
                gameMode = selectedGameMode;
            }
        });
        vBox.add(gameModeBox);
        // 玩家模式
        vBox.add(Box.createVerticalStrut(5));
        JLabel playerLabel = new JLabel("玩家模式：");
        playerLabel.setFont(new Font("", 1, 13));
        vBox.add(playerLabel);

        vBox.add(Box.createVerticalStrut(5));
        ComboBox playerModeBox = new ComboBox();
        playerModeBox.setPreferredSize(new Dimension(40, 30));
        for (PlayerMode value : PlayerMode.values()) {
            playerModeBox.addItem(value.getName());
        }
        playerMode = PlayerMode.SINGLE;
        playerModeBox.setSelectedItem(playerMode.getName());
        playerModeBox.addActionListener(l -> {
            PlayerMode selectedPlayerMode = PlayerMode.getMode(playerModeBox.getSelectedItem().toString());
            if (selectedPlayerMode != null) {
                playerMode = selectedPlayerMode;
            }
        });
        vBox.add(playerModeBox);
        vBox.add(Box.createVerticalStrut(5));
        vBox.add(getStartGameButton());
        if (DataCache.isOnline) {
            List<Integer> numsList = new ArrayList();
            Collections.addAll(numsList, 2, 3, 4);

            vBox.add(getCreateRoomButton(numsList));
        }
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    @Override
    public void handle(UNOGameDto body) {

    }

    private void initValue() {
        anticlockwise = true;
        userList = new ArrayList<>();
        currentPlayer = null;
        playerMap = null;
        userList = new ArrayList<>();
        aiPlayerActionMap = new HashMap<>();
        outBtn = new JButton("出牌");
        tipsBtn = new JButton("提示");
        unoBtn = new JButton("UNO");
        catchBtn = new JButton("抓");
        teamMap = new HashMap<>(4);
    }

    private void sendMsg(MsgType msgType, String player, Object data) {
        sendMsg(msgType, player, null, data);
    }

    private void sendMsg(MsgType msgType, String player, Integer action , Object data) {
        UNOGameDto dto = new UNOGameDto();
        dto.setMsgType(msgType);
        dto.setData(data);
        dto.setActionId(action);
        dto.setCurrentPlayer(currentPlayer);
        sendMsg(dto);
    }

    @Override
    protected void sendMsg(GameDTO body) {
        if (getRoom() != null) {
            super.sendMsg(body);
        }
        invoke(() -> handle((UNOGameDto) body));
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
