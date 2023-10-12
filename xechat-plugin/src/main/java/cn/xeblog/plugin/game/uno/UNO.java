package cn.xeblog.plugin.game.uno;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.commons.entity.game.uno.MsgType;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.uno.action.AiPlayerAction;
import cn.xeblog.plugin.game.uno.action.PlayerAction;
import cn.xeblog.plugin.game.uno.entity.Player;
import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import cn.xeblog.commons.entity.game.uno.UNOGameDto;
import cn.xeblog.plugin.game.uno.enums.*;
import cn.xeblog.plugin.game.uno.ui.CardPanel;
import cn.xeblog.plugin.game.uno.utils.CalcUtil;
import cn.xeblog.plugin.game.uno.utils.CardUtil;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static cn.xeblog.commons.entity.game.uno.MsgType.*;


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

    private JButton allocBtn;
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
    private JLabel tipsArea;
    /**
     * 玩家卡牌主面板
     */
    private JPanel cardsPanel;

    /**
     * 提示标签
     */
    private JLabel tipsLabel;
    /**
     * 出牌面板卡板
     */
    private JPanel outCardsPanel;
    /**
     * 推断类型
     */
    private JPanel judgeCardsPanel;
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

    private List<Card> allCards;
    /**
     * 弃牌堆
     */
    private List<Card> discardPile;
    /**
     * 判断队列
     */
    private ArrayDeque<Card> judgeDeque;

    /**
     * 状态 -1 游戏结束 0初始化 1游戏中
     */
    private Integer status;

    private Integer tipsRows;

    private Map<String, String> teamMap;

    private List<Card> selectedCards;

    /**
     * 出牌
     */
    private List<Card> outCards;
    /**
     * uno
     */
    private Map<String, Boolean> unoMap;

    private Integer addNums;

    private WindowMode windowMode;

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

    private static final long aiThinkingTime = 1000L;
    // 玩家展示卡牌区域
    private JPanel showCardsPanel;

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
                }
                showGamePanel();
            }, 500);
        }
    }

    /**
     * 初始化玩家团队
     */
    private void initPlayerTeam() {
        if (playerMode.equals(PlayerMode.DOUBLE) && userList.size() == 4) {
            // 如果是双人模式的话就要加载玩家队伍了
            teamMap.put(userList.get(0), userList.get(2));
            teamMap.put(userList.get(1), userList.get(3));
            teamMap.put(userList.get(2), userList.get(0));
            teamMap.put(userList.get(3), userList.get(1));
        }
    }


    private void showGamePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMaximumSize(new Dimension(400, 300));
        mainPanel.setBounds(0, 0, 400, 300);
        tipsLabel = new JLabel("UNO!");
        tipsLabel.setHorizontalTextPosition(JLabel.CENTER);
        mainPanel.add(tipsLabel, BorderLayout.NORTH);
        if (userList.size() == 4) {
            mainPanel.add(initUserPanel(), BorderLayout.EAST);
            initAllocCards();
        }
        mainPanel.add(initCenter(), BorderLayout.CENTER);
        // 游戏底部
        JPanel mainBottomPanel = new JPanel();
        if (getRoom() == null) {
            backButton = getBackButton();
            mainBottomPanel.add(backButton);
        }
        gameOverButton = getGameOverButton();
        gameOverButton.setVisible(false);
        mainBottomPanel.add(gameOverButton);
        mainPanel.add(mainBottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 玩家离开
     * @param player
     */
    @Override
    public void playerLeft(User player) {
        super.playerLeft(player);
        if (status > 0 && status == -1) {
            String msg = "游戏结束！" + player.getUsername() + "逃跑了~";
            String tips = "溜了~";
            gameOverButton.setVisible(true);
        }
        // TODO: 2023/4/18 玩家离开
    }

    /**
     * 初始化牌堆
     */
    private void initDisCard() {
        Card disCard = null;
        do {
            disCard = CalcUtil.randomOneCard(allCards);
            log.info("当前初始化牌堆卡片为 - {}", disCard);
        } while (disCard.getIsFunctionCard());
        sendMsg(INIT_DISCARD, GameAction.getNickname(), disCard);
    }

    private JPanel initCenter() {
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(320, 200));
        centerPanel.setBorder(new LineBorder(JBColor.RED, 2));
        if (playerMode.equals(PlayerMode.DOUBLE)) {
            centerPanel.setLayout(new GridLayout(4, 1));
            // 队友卡牌
            JBScrollPane teammateCardsScroll = new JBScrollPane(teammateCardsPanel);
            teammateCardsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            teammateCardsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            centerPanel.add(teammateCardsScroll);
        } else {
            centerPanel.setLayout(new GridLayout(3, 1));
        }

        // 出牌区域
        showCardsPanel = new JPanel(new BorderLayout());
        JBScrollPane outCardsScroll = new JBScrollPane(outCardsPanel);
        outCardsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outCardsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        showCardsPanel.add(outCardsScroll, BorderLayout.CENTER);
        centerPanel.add(showCardsPanel);
        // 玩家手牌
        JBScrollPane cardsScroll = new JBScrollPane(cardsPanel);
        cardsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        centerPanel.add(cardsScroll);
        centerPanel.add(initBtnPanel());

        return centerPanel;
    }

    private JPanel initBtnPanel() {
        JPanel btnPanel = new JPanel();
        btnPanel.setPreferredSize(new Dimension(320, 50));
        allocBtn = new JButton("摸牌");
        outBtn = new JButton("出牌");
        tipsBtn = new JButton("提示");
        unoBtn = new JButton("UNO");
        catchBtn = new JButton("抓");

        String nickname = GameAction.getNickname();
        allocBtn.addActionListener(e -> {
            // 摸排之后不允许出牌
            sendMsg(ALLOC_CARDS, nickname, 0,randomAllocCards(1));
            refreshBtnStatus(outBtn, false);
            refreshBtnStatus(allocBtn, false);
        });

        outBtn.addActionListener(e -> {
            if(CollUtil.isEmpty(selectedCards)) {
                JOptionPane.showMessageDialog(null, "请选择要出的卡牌", "游戏提示", JOptionPane.YES_OPTION);
                return;
            }
            boolean canOut = CalcUtil.canOut(selectedCards, judgeDeque);
            if (canOut) {
                sendMsg(OUT_CARDS, nickname, selectedCards);
            } else {
                JOptionPane.showMessageDialog(null, "请选择正确的卡牌，同颜色或同数值或黑色功能牌", "游戏提示", JOptionPane.YES_OPTION);
                return;
            }
            refreshBtnStatus(outBtn, false);
            refreshBtnStatus(allocBtn, false);
        });
        // UNO
        unoBtn.addActionListener(e -> {
            Player player = getPlayer(nickname);
            PlayerNode playerNode = player.getPlayerNode();
            playerNode.setUno(true);
            player.refreshUserPanel();
        });

        tipsBtn.addActionListener(e -> {
            List<Card> tips = CalcUtil.tips(getCardsByPlayerName(nickname), judgeDeque, gameMode);
            selectedCards.clear();
            selectedCards.addAll(tips);
            refreshPlayerCards(getPlayer(nickname));
        });

        catchBtn.addActionListener(e -> {
            sendMsg(PASS, GameAction.getNickname(), null);
        });

        addAll(btnPanel, allocBtn, outBtn, tipsBtn, unoBtn, catchBtn);
        return btnPanel;
    }

    private Player getPlayer(String playerName){
        return playerMap.get(playerName);
    }

    private PlayerNode getPlayerNode(String playerName) {
        return getPlayer(playerName).getPlayerNode();
    }

    private List<Card> getCardsByPlayerName(String playerName){
        Player player = playerMap.get(playerName);
        return player.getPlayerNode().getCards();
    }


    public void initBtnStatus() {
        boolean actionFlag = StrUtil.equalsAnyIgnoreCase(GameAction.getNickname(), currentPlayer.getPlayerName());
        allocBtn.setEnabled(actionFlag);
        outBtn.setEnabled(actionFlag);
        tipsBtn.setEnabled(actionFlag);
    }

    public void refreshBtnStatus(JButton btn, Boolean btnStatus) {
        btn.setEnabled(btnStatus);
    }


    private JPanel initUserPanel(){
        JPanel usersPanel = new JPanel();
        usersPanel.setPreferredSize(new Dimension(140, 80));
        for (String playerName : userList) {
            Player player = playerMap.get(playerName);
            JPanel userPanel = getUserUI(player);
            player.setPanel(userPanel);
            usersPanel.add(userPanel);
        }
        return usersPanel;
    }

    private JPanel getUserUI(Player player) {
        return player.getUserUI(e -> {
            sendMsg(CATCH, player.getPlayerNode().getPlayerName(), randomAllocCards(2));
        });
    }

    private JPanel getUserPanel(PlayerNode playerNode){
        JPanel userPanel = new JPanel();
        userPanel.setPreferredSize(new Dimension(140, 90));
        JLabel nameLabel = new JLabel(String.format("%s 手牌 %d", playerNode.getPlayerName(), playerNode.getCardsTotal()));
        nameLabel.setPreferredSize(new Dimension(140, 20));
        String format = String.format("状态 %s", playerNode.getPlayerStatus().name());
        if (playerNode.getUno()) {
            format += " UNO";
        }
        JLabel statueLabel = new JLabel(format);
        statueLabel.setPreferredSize(new Dimension(140, 20));
        JButton catchPlayerBtn = new JButton("抓住你啦");
        catchPlayerBtn.setPreferredSize(new Dimension(110, 30));
        catchPlayerBtn.addActionListener(e -> {
            sendMsg(ALLOC_CARDS, playerNode.getPlayerName(), randomAllocCards(2));
        });
        addAll(userPanel, nameLabel, statueLabel, catchPlayerBtn);
        return userPanel;
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
        // 创建节点的时候分好组
        Collections.shuffle(userList);
        initPlayerTeam();
        // 当前房间玩家总数
        int usersTotal = roomUserList.size();
        // 节点放在外边防止循环创建玩家节点
        PlayerNode node = null;
        // 创建玩家节点
        for (int i = 0; i < usersTotal; i++) {
            node =  new PlayerNode();
            node.setPlayerName(roomUserList.get(i));
            node.setAlias("Machine 0" + (i + 1));
            node.setCardsTotal(0);
            node.setCards(new ArrayList<>());
            playerMap.put(node.getPlayerName(), new Player(node, new JPanel()));
            unoMap.put(node.getPlayerName(), false);
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
        // 根据游戏模式获取卡牌
        allCards = CardUtil.initCards(gameMode);
        allCards.forEach(item -> item.setId(allCards.indexOf(item)));
        allCards.forEach(item -> item.setId(allCards.indexOf(item)));
        buildPlayerNode();
        showGamePanel();
        status = 1;
        // 玩家不够4名的时候或者房间为空直接开始游戏的
        if (gameRoom == null || userList.size() < 4) {
            allPlayersGameStarted();
        }
    }

    /**
     * 初始化分牌
     */
    private void initAllocCards(){
        if (userList.size() == 4) {
            Map<String, List<Card>> initCard = new HashMap<>();
            for (String playerName : userList) {
                List<Card> randomCards = CalcUtil.randomCard(allCards, 7);
                allCards.removeAll(randomCards);
                initCard.put(playerName, randomCards);
            }
            sendMsg(INIT_ALLOC_CARDS, GameAction.getNickname(), initCard);
        }
    }

    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(300, 275));
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

    private void changePlayerStatus(String playerName, PlayerStatus playerStatus){
        Player player = getPlayer(playerName);
        player.getPlayerNode().setPlayerStatus(playerStatus);
        player.refreshUserPanel();

        Player nextPlayer = CalcUtil.getOtherPlayer(anticlockwise, playerMap, true, playerName);
        nextPlayer.getPlayerNode().setPlayerStatus(PlayerStatus.NEXT);
        nextPlayer.refreshUserPanel();

        Player prevPlayer = CalcUtil.getOtherPlayer(anticlockwise, playerMap, false, playerName);
        prevPlayer.getPlayerNode().setPlayerStatus(PlayerStatus.PREV);
        prevPlayer.refreshUserPanel();

        List<String> userNameSet = new ArrayList<>(playerMap.keySet());
        userNameSet.remove(playerName);
        userNameSet.remove(nextPlayer.getPlayerNode().getPlayerName());
        userNameSet.remove(prevPlayer.getPlayerNode().getPlayerName());
        Player otherPlayer = playerMap.get(userNameSet.get(0));
        otherPlayer.getPlayerNode().setPlayerStatus(PlayerStatus.WAITING);
        otherPlayer.refreshUserPanel();
    }

    @Override
    public void handle(UNOGameDto body) {
        String playerName = body.getPlayerName();
        if (status <= 0 || !StrUtil.equalsIgnoreCase(playerName, currentPlayer.getPlayerName())) {
            return;
        }
        PlayerNode playerNode = getPlayerNode(playerName);
        if (playerNode.getPlayerStatus().equals(PlayerStatus.SKIP)) {
            return;
        }

        switch (body.getMsgType()) {
            case REFRESH_TIPS_MSG:
                refreshTipsMsg(body);
                break;
            case JOIN_ROBOTS:
                joinRobots(body);
                break;
            case OUT_CARDS:
                outCards(body);
                break;
            case UNO:
                uno(body);
                break;
            case CATCH:
                catchUnUNO(body);
                break;
            case QUESTION:
                question(body);
                break;
            case INIT_ALLOC_CARDS:
                initAllocCards(body);
                break;
            case ALLOC_CARDS:
                allocCards(body);
                break;
            case INIT_DISCARD:
                initDisCard(body);
                break;
            case CHANGE_COLOR:
                changeColor(body);
                break;
            case PASS:
                pass(body);
                break;
            default:
                break;
        }
    }

    /**
     * 换人
     * @param body
     */
    private void pass(UNOGameDto body) {
        String playerName = body.getPlayerName();
        changePlayerStatus(playerName, PlayerStatus.WAITING);
        // 获取下一个人是谁
        currentPlayer = CalcUtil.getOtherPlayerNode(anticlockwise, playerMap, true, playerName);
        //refreshTipsArea("%s跳过，下家%s", playerName, currentPlayer.getPlayerName());
        // 获取
        String currentPlayerName = currentPlayer.getPlayerName();
        changePlayerStatus(currentPlayerName, PlayerStatus.THINKING);
        // 有能出的牌
        Boolean hasCanOut = CalcUtil.hasCanOutCards(currentPlayer, judgeDeque, gameMode);
        // 是否AI
        Boolean robotControl = robotControl(currentPlayerName);
        // 如果能出牌
        if (hasCanOut) {
            // 如果是AI的话自动出牌
            if (robotControl) {
                ThreadUtils.spinMoment(aiThinkingTime);
                List<Card> currentPlayerCards = currentPlayer.getCards();
                List<Card> aiOutCards = CalcUtil.tips(currentPlayerCards, judgeDeque, gameMode);
                Boolean sayUNo = CalcUtil.sayUNo(currentPlayerCards, aiOutCards);
                if (sayUNo) {
                    sendMsg(UNO, currentPlayerName, null);
                }
                // 双重判断ai没有能出的牌就摸牌
                if (CollUtil.isEmpty(aiOutCards)) {
                    sendMsg(ALLOC_CARDS, currentPlayerName, randomAllocCards(1));
                } else {
                    sendMsg(OUT_CARDS, currentPlayerName, aiOutCards);
                }
            } else if (StrUtil.equalsIgnoreCase(currentPlayerName, GameAction.getNickname())) {
                // 如果是玩家的话就展示按钮，玩家选择出什么牌
                refreshBtnStatus(outBtn, true);
                refreshBtnStatus(allocBtn, true);
            }
        } else {
            //refreshTipsArea("%s暂无可出的牌，自动摸牌", playerName);
            sendMsg(ALLOC_CARDS, currentPlayerName, randomAllocCards(1));
            // 摸牌之后返回
            return;
        }
    }


    /**
     * 是否AI控制
     * @param playerName
     * @return
     */
    private Boolean robotControl(String playerName) {
        return isHomeowner() && aiPlayerActionMap.get(playerName) != null;
    }

    /**
     * 改变颜色
     * @param body
     */
    private void changeColor(UNOGameDto body) {
        Color color = (Color) body.getData();
        // 暂时性修复一个bug，不能让AI一直出黑色的牌
        if (color.equals(Color.BLACK)) {
            color = Color.RED;
            Color changeColor = CalcUtil.getChangeColor(getPlayerNode(body.getPlayerName()).getCards());
        }
        outCards.get(0).setChangeColor(color);
        refreshOtherCardPanel(outCardsPanel, outCards, color);
    }

    /**
     * 初始化分牌
     * @param body
     */
    private void initDisCard(UNOGameDto body) {
        Card disCard = (Card) body.getData();
        addToJudgeDeque(disCard);
        addToOutCards(Collections.singletonList(disCard));
    }

    /**
     * 刷新出牌区域
     * @param disCard
     */
    private void addToOutCards(List<Card> disCard) {
        outCards.clear();
        outCards.addAll(disCard);
        discardPile.addAll(disCard);
        refreshOtherCardPanel(outCardsPanel, outCards);
    }

    /**
     * 将弃牌添加到判断牌堆中
     * @param card  弃牌
     */
    private void addToJudgeDeque(Card card) {
        // 如果当前判断牌堆的数量是2的话，抛弃第一个
        if (judgeDeque.size() == 2) {
            Card pollCard = judgeDeque.poll();
            pollCard.setChangeColor(null);
        }
        // 将新的弃牌防止到判断牌堆当中
        judgeDeque.add(card);
        // 刷新弃牌堆
        refreshOtherCardPanel(judgeCardsPanel, judgeDeque);
    }

    /**
     * 发送
     * @param nums
     * @return
     */
    private Map<String, List<Card>> randomAllocCards(Integer nums) {
        Map<String, List<Card>> resultMap = new HashMap<>(3);
        List<Card> addCards = new ArrayList<>();
        // 剩余数量
        int size = allCards.size();
        if (nums > size) {
            allCards.addAll(discardPile);
            discardPile.clear();
            addCards = CalcUtil.randomCard(allCards, nums - size);
            allCards.removeAll(addCards);
        } else if (nums == size) {
            addCards = allCards;
            allCards.clear();
            allCards.addAll(discardPile);
            allCards.sort(Card::compareTo);
            discardPile.clear();
        } else {
            addCards = CalcUtil.randomCard(allCards, nums);
            allCards.removeAll(addCards);
        }
        resultMap.put("allocCards", addCards);
        resultMap.put("allCards", allCards);
        resultMap.put("discardPile", discardPile);
        return resultMap;
    }


    private void allocCards(UNOGameDto body) {
        List<Card> addCards = handleAllocMap(body);
        String playerName = body.getPlayerName();
        Integer actionId = body.getActionId();
        // 给玩家添加上当前摸牌
        addCards(playerName, addCards);
        Boolean canOut = CalcUtil.canOut(addCards, judgeDeque);
        // 是否机器人控制
        Boolean robotControl = robotControl(playerName);
        // 只摸一张牌
        if (addCards.size() == 1) {
            // 新摸出来的牌能出
            if (canOut) {
                // 是机器人直接出
                if (robotControl) {
                    sendMsg(OUT_CARDS, playerName, addCards);
                } else if (StrUtil.equalsIgnoreCase(GameAction.getNickname(), playerName)) {
                    // 如果是玩家的话则提示 并且当前玩家和本机的用户名一样 的话提示是否出牌
                    String[] choice = {"保留", "打出"};
                    Card card = addCards.get(0);
                    String message = String.format("你摸到了一张【%s - %s】卡牌", card.getColorStr(), card.getValue());
                    int res = JOptionPane.showOptionDialog(null, message, "选项对话框",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, choice, "打出");
                    if (res == 1){
                        sendMsg(OUT_CARDS, playerName, addCards);
                        return;
                    }
                }
                sendMsg(PASS, playerName, null);
            } else {
                // 不能出 或者是玩家选择了保留卡牌就自动跳过
                sendMsg(PASS, playerName, null);
                return;
            }
        }
        // 如果不为空则要跳过当前玩家
        if (null != actionId) {
            sendMsg(PASS, playerName, null);
        }
    }

    /**
     * 处理随机的增加卡牌数据
     * @param body
     * @return
     */
    private List<Card> handleAllocMap(UNOGameDto body) {
        Map<String, List<Card>> bodyData = (Map<String, List<Card>>) body.getData();
        List<Card> addCards = bodyData.get("allocCards");
        allCards = bodyData.get("allCards");
        discardPile = bodyData.get("discardPile");
        return addCards;
    }

    /**
     * 分牌
     * @param body
     */
    private void initAllocCards(UNOGameDto body) {
        // 获取分牌结果
        Map<String, List<Card>> initCard = (Map<String, List<Card>>) body.getData();
        log.info("=====初始化分牌，每人七张=====");
        initCard.forEach((k,v) -> addCards(k, v));
        log.info("=====初始化分牌结束=====");
        if(isHomeowner()) {
            initDisCard();
            changePlayerStatus(currentPlayer.getPlayerName(), PlayerStatus.THINKING);
        }
    }

    private void addCards(String playerName, List<Card> addCards) {
        log.info("当前【{}】摸牌 - {}", playerName, addCards);
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        List<Card> playerCards = playerNode.getCards();
        playerCards.addAll(addCards);
        // 添加卡牌之后进行重新排序
        playerCards.sort(Card::compareTo);
        playerNode.setCards(playerCards);
        player.setPlayerNode(playerNode);
        player.refreshUserPanel();
        if (StrUtil.equalsIgnoreCase(playerName, GameAction.getNickname())) {
            refreshPlayerCards(player);
        }
        refreshTeammateCards(player);
    }

    /**
     * 刷新队友的卡牌
     * @param player
     */
    private void refreshTeammateCards(Player player) {
        // 双人模式进行操作
        if (playerMode.equals(PlayerMode.DOUBLE)) {
            String teammateName = player.getPlayerNode().getPlayerName();
            String playerName = teamMap.get(teammateName);
            // 如果与传入人员是队友的话
            if (StrUtil.equalsIgnoreCase(playerName, GameAction.getNickname())) {
                PlayerNode playerNode = player.getPlayerNode();
                List<Card> cards = playerNode.getCards();
                cards.sort(Card::compareTo);
                refreshOtherCardPanel(teammateCardsPanel, cards);
            }
        }
    }

    /**
     * 刷新其他的游戏面板
     * @param jPanel        要刷新的卡牌面板
     * @param cardList      卡牌集合
     */
    private void refreshOtherCardPanel(JPanel jPanel, Collection<Card> cardList) {
        refreshOtherCardPanel(jPanel, cardList, null);
    }

    private void refreshOtherCardPanel(JPanel jPanel, Collection<Card> cardList, Color color) {
        jPanel.removeAll();
        cardList.forEach(item -> jPanel.add(getCardPanel(item, color)));
        jPanel.updateUI();
    }

    /**
     * 刷新玩家卡牌
     * @param player
     */
    private void refreshPlayerCards(Player player) {
        PlayerNode playerNode = player.getPlayerNode();
        List<Card> cards = playerNode.getCards();
        cards.sort(Card::compareTo);
        PlayerAction playerAction = aiPlayerActionMap.get(currentPlayer.getPlayerName());
        boolean robotControl = isHomeowner() && playerAction != null;
        boolean actionFlag = StrUtil.equalsIgnoreCase(playerNode.getPlayerName(), currentPlayer.getPlayerName());
        if (actionFlag && !robotControl) {
            cardsPanel.removeAll();
            if (CollUtil.isNotEmpty(cards)) {
                cards.forEach(item -> {

                    JPanel cardPanel = getCardPanel(item, null);
                    if (selectedCards.contains(item)) {
                        cardPanel.setBorder(new LineBorder(Color.WHITE, 2));
                    }
                    cardsPanel.add(cardPanel);
                    cardPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            selectedCards.clear();
                            if (item.getValue().equalsIgnoreCase(CardUtil.CLEAR)) {
                                    // 同颜色的卡牌
                                    List<Card> sameColorCards = cards.stream()
                                            .filter(c -> c.getColor().equals(item.getColor()))
                                            .collect(Collectors.toList());
                                    selectedCards.addAll(sameColorCards);
                            } else {
                                selectedCards.add(item);
                                cardPanel.setBorder(new LineBorder(Color.WHITE, 2));
                            }
                            // 剩余卡牌，在打出倒数第二张牌的时候记得含UNO
                            int surplus = cards.size() - selectedCards.size();
                            refreshBtnStatus(unoBtn, surplus == 1);
                            refreshPlayerCards(player);
                        }
                    });
                });
            }
            cardsPanel.updateUI();
        }
    }

    /**
     * 获取卡牌颜色
     * @param card      卡牌
     * @param color     颜色
     * @return  如果color为空则用卡牌自己的颜色，否则就用传入的颜色
     */
    private JPanel getCardPanel(Card card, Color color) {
        return new CardPanel().getCardPanel(card, color);
    }

    /**
     * 质疑 +4
     * @param body
     */
    private void question(UNOGameDto body) {
    }

    /**
     * 抓玩家没有uno
     * @param body
     */
    private void catchUnUNO(UNOGameDto body) {
        String playerName = body.getPlayerName();
        List<Card> cards = handleAllocMap(body);
        // 添加两张牌
        addCards(playerName, cards);
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        // 设置玩家面板是否展示catch
        playerNode.setCanCatch(false);
        // 刷新玩家面板
        player.refreshUserPanel();
    }

    private void uno(UNOGameDto body) {
        String playerName = body.getPlayerName();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        playerNode.setUno(true);
        player.refreshUserPanel();
    }

    /**
     * 初牌
     * @param body
     */
    private void outCards(UNOGameDto body) {
        // 获取出牌的数据
        List<Card> outCards = (List<Card>) body.getData();
        List<Card> clearList = outCards.stream()
                .filter(item -> item.getValue().equalsIgnoreCase(CardUtil.CLEAR))
                .collect(Collectors.toList());
        Card discard = CollUtil.isNotEmpty(clearList) ? clearList.get(0) : outCards.get(0);

        String playerName = body.getPlayerName();
        log.info("【{}】 出牌 - {}", playerName, outCards);
        //refreshTipsArea("%s玩家出牌", playerName);
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();

        List<Card> playerCards = playerNode.getCards();
        // 如果是当前用户操作并且出的是 +4 或者CHANGE 展示切换颜色的提示
        if (discard.getColor().equals(Color.BLACK)) {
            if (robotControl(playerName)) {
                // ai自动判断变更颜色
                Color changeColor = CalcUtil.getChangeColor(playerNode.getCards());
                sendMsg(CHANGE_COLOR, playerName, changeColor);
            }

            if (StrUtil.equalsIgnoreCase(playerName, GameAction.getNickname())) {
                showChangeColorDialog(playerName);
            }
            refreshCards(player, outCards, discard);
            return;
        }

        String discardValue = discard.getValue();
        // 如果是反转的话讲顺序变更
        if (StrUtil.equalsIgnoreCase(discardValue, CardUtil.REVERSE)) {
            anticlockwise = !anticlockwise;
            refreshCards(player, outCards, discard);
            return;
        }
        if (StrUtil.equalsIgnoreCase(discardValue, CardUtil.SKIP)) {
            String nextPlayerName = CalcUtil.getOtherPlayerName(anticlockwise, playerMap, true, playerName);
            changePlayerStatus(nextPlayerName, PlayerStatus.SKIP);
            refreshCards(player, outCards, discard);
            sendMsg(PASS, nextPlayerName, null);
            return;
        }

        // 经典模式
        if (gameMode.equals(GameMode.CLASSIC)) {
            if (StrUtil.equalsIgnoreCase(discardValue, CardUtil.ADD_2)) {
                functionCardAdd(playerName, 2);
            }
            if (StrUtil.equalsIgnoreCase(discardValue, CardUtil.ADD_4)) {
                functionCardAdd(playerName, 4);
            }
            refreshCards(player, outCards, discard);
            return;
        }
        log.info("【{}】 手牌{}, 出牌{}", playerName, playerCards, outCards);
        // 当前玩家剩下一张牌 并且没诶呦喊UNo 而且和当前玩家不是 队友 允许抓人
        if (playerCards.size() == 1 &&
                !playerNode.getUno() &&
                StrUtil.equalsIgnoreCase(teamMap.get(playerName), GameAction.getNickname())) {
            playerNode.setCanCatch(true);
            player.refreshUserPanel();
        }

        if (CollUtil.isEmpty(playerCards)) {
            status = -1;
            gameOver();
            return;
        }
        refreshCards(player, outCards, discard);
    }

    private void refreshCards(Player player, List<Card> outCards, Card discard){
        PlayerNode playerNode = player.getPlayerNode();
        List<Card> playerCards = playerNode.getCards();
        for (Card outCard : outCards) {
            int index = playerCards.indexOf(outCard);
            // 如果有的话就删除掉
            if (index != -1) {
                playerCards.remove(index);
            }
        }
        playerNode.setCards(playerCards);
        player.refreshUserPanel();
        addToOutCards(outCards);
        // 添加到弃牌堆
        addToJudgeDeque(discard);
        // 刷新自己的卡牌和队友的卡牌展示面板
        refreshPlayerCards(player);
        refreshTeammateCards(player);
        sendMsg(PASS, playerNode.getPlayerName(), null);
    }

    /**
     * 功能牌添加牌
     * @param playerName    玩家
     * @param nums          添加数量
     */
    private void functionCardAdd(String playerName, Integer nums) {
        if (nums == 0) {
            nums = addNums;
        }
        // 下家玩家名称
        String nextPlayerName = CalcUtil.getOtherPlayerName(anticlockwise, playerMap, false, playerName);
        sendMsg(ALLOC_CARDS, nextPlayerName, 1, randomAllocCards(nums));
        changePlayerStatus(nextPlayerName, PlayerStatus.SKIP);
        sendMsg(PASS, nextPlayerName, null);
    }

    /**
     * 游戏结束
     */
    private void gameOver() {
        status = -1;
        if (getRoom() == null) {
            backButton.setVisible(true);
        } else {
            gameOverButton.setVisible(true);
        }
    }

    /**
     * 展示变更颜色的弹窗
     * @param playerName
     */
    private void showChangeColorDialog(String playerName) {
        //选项对话框
        String[] colorArr = {"红色","黄色","绿色", "蓝色"};
        //返回值是数组下标,最后一个参数是默认选项
        int res = JOptionPane.showOptionDialog(null, "请选择改变的卡牌颜色", "选项对话框",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, colorArr, "红色");
        switch (res) {
            case 1 :
                sendMsg(CHANGE_COLOR, playerName, Color.YELLOW);
                break;
            case 2 :
                sendMsg(CHANGE_COLOR, playerName, Color.GREEN);
                break;
            case 3 :
                sendMsg(CHANGE_COLOR, playerName, Color.BLUE);
                break;
            default:
                sendMsg(CHANGE_COLOR, playerName, Color.RED);
                break;
        }
    }

    /**
     * 刷新数据
     * @param body
     */
    private void refreshTipsMsg(UNOGameDto body) {
    }

    /**
     * 加入机器人
     * @param body
     */
    private void joinRobots(UNOGameDto body) {
        List<String> robotList = (List<String>) body.getData();
        userList.addAll(robotList);
        buildPlayerNode();
        showGamePanel();
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
        discardPile = new ArrayList<>();
        judgeDeque = new ArrayDeque<>(2);
        unoMap = new HashMap<>(4);
        tipsRows = 0;
        tipsArea = new JLabel();
        cardsPanel = new JPanel();
        outCardsPanel = new JPanel();
        outCards = new ArrayList<>();
        judgeCardsPanel = new JPanel();
        teammateCardsPanel = new JPanel();
        selectedCards = new ArrayList<>();
        anticlockwise = true;
    }

    private void sendMsg(MsgType msgType, String player, Object data) {
        sendMsg(msgType, player, null, data);
    }

    private void sendMsg(MsgType msgType, String player, Integer action , Object data) {
        UNOGameDto dto = new UNOGameDto();
        dto.setMsgType(msgType);
        dto.setPlayerName(player);
        dto.setData(data);
        dto.setActionId(action);
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


    private void refreshWindowMode(){
        int width = 490;
        int topWidth = 200;
        if (windowMode == WindowMode.ADAPTIVE) {
            width = 45 * 9;
        }

        mainPanel.setMinimumSize(new Dimension(width, 350));
        tipsLabel.setPreferredSize(new Dimension(topWidth, 40));
        showCardsPanel.setPreferredSize(new Dimension(topWidth, 70));
        mainPanel.updateUI();
    }
}