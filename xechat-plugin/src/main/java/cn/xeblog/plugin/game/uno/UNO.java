package cn.xeblog.plugin.game.uno;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.uno.Card;
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
import cn.xeblog.plugin.game.uno.utils.CalcUtil;
import cn.xeblog.plugin.game.uno.utils.CardUtil;
import com.intellij.openapi.ui.ComboBox;
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
    private JTextArea tipsArea;
    /**
     * 玩家卡牌主面板
     */
    private JPanel cardsPanel;
    /**
     * 出牌面板卡板
     */
    private JPanel outCardsPanel;
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
     * uno
     */
    private Map<String, Boolean> unoMap;

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
                }
                showGamePanel();
            }, 500);
        }
    }

    /**
     * 初始化玩家团队
     */
    private void initPlayerTeam() {
        Collections.shuffle(userList);
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
        mainPanel.setMinimumSize(new Dimension(490, 350));
        if (userList.size() == 4) {
            mainPanel.add(initUserPanel(0), BorderLayout.SOUTH);
            mainPanel.add(initUserPanel(1), BorderLayout.EAST);
            mainPanel.add(initUserPanel(2), BorderLayout.NORTH);
            mainPanel.add(initUserPanel(3), BorderLayout.WEST);
            initAllocCards();
            initPlayerTeam();
        }
        mainPanel.add(initCenter(), BorderLayout.CENTER);
    }

    private JPanel initCenter() {
        JPanel centerPanel = new JPanel();
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
        JBScrollPane outCardsScroll = new JBScrollPane(outCardsPanel);
        outCardsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outCardsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        centerPanel.add(outCardsScroll);

        // 玩家手牌
        JBScrollPane cardsScroll = new JBScrollPane(cardsPanel);
        cardsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardsScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        centerPanel.add(cardsScroll);


        return centerPanel;
    }

    private JPanel initBtnPanel() {
        JPanel btnPanel = new JPanel();
        allocBtn = new JButton("摸牌");
        outBtn = new JButton("出牌");
        tipsBtn = new JButton("提示");
        catchBtn = new JButton("抓");

        allocBtn.addActionListener(e -> {
            // 摸排之后不允许出牌
            sendMsg(ALLOC_CARDS, GameAction.getNickname(), 0,1);
            refreshBtnStatus(outBtn, false);
        });

        outBtn.addActionListener(e -> {
            if(CollUtil.isEmpty(selectedCards)) {
                JOptionPane.showMessageDialog(null, "请选择要出的卡牌", "游戏提示", JOptionPane.YES_OPTION);
                return;
            }
            sendMsg(OUT_CARDS, GameAction.getNickname(), selectedCards);
            // 清空已选中的牌面
            selectedCards.clear();
        });

        return btnPanel;
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


    private JPanel initTipsArea() {
        //JPanel textPanel = new JPanel();
        ////textPanel.setPreferredSize(new Dimension(300, 80));
        //tipsArea = new JTextArea("游戏开始\n");
        //tipsArea.setRows(10);
        //tipsArea.setColumns(50);
        //tipsArea.setLineWrap(true);
        //JBScrollPane scrollPane = new JBScrollPane(tipsArea);//创建滚动条面板
        //scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        ////scrollPane.setBounds(0,0,300,80);
        //textPanel.add(scrollPane);
        return null;
    }



    private JPanel initUserPanel(Integer index) {
        String playerName = userList.get(index);
        Player player = playerMap.get(playerName);
        JPanel panel = player.getPanel();
        JLabel tipsLabel = new JLabel(String.format("【%s】: 手牌 %d", playerName, player.getPlayerNode().getCardsTotal()));
        panel.add(tipsLabel);
        player.setTipsLabel(tipsLabel);
        return panel;
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
        if (status == 0){
            return;
        }
        String playerName = body.getPlayerName();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();

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
            default:
                break;
        }
    }

    private void allocCards(UNOGameDto body) {
        Integer nums = (Integer) body.getData();
        String playerName = body.getPlayerName();
        // 剩余数量
        int size = allCards.size();
        if (nums > size) {
            addCards(playerName, allCards);
            allCards.addAll(discardPile);
            discardPile.clear();
            List<Card> addCards = CalcUtil.randomCard(allCards, nums - size);
            addCards(playerName, addCards);
            allCards.removeAll(addCards);
        } else if (nums == size) {
            addCards(playerName, allCards);
            allCards.clear();
            allCards.addAll(discardPile);
            allCards.sort(Card::compareTo);
            discardPile.clear();
        } else {
            List<Card> addCards = CalcUtil.randomCard(allCards, nums);
            addCards(playerName, addCards);
            allCards.removeAll(addCards);
        }
    }

    /**
     * 分牌
     * @param body
     */
    private void initAllocCards(UNOGameDto body) {
        // 获取分牌结果
        Map<String, List<Card>> initCard = (Map<String, List<Card>>) body.getData();
        initCard.forEach((k,v) -> addCards(k, v));
    }

    private void addCards(String playerName, List<Card> addCards) {
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        List<Card> playerCards = playerNode.getCards();
        playerCards.addAll(addCards);
        // 添加卡牌之后进行重新排序
        playerCards.sort(Card::compareTo);
        playerNode.setCards(playerCards);
        player.setPlayerNode(playerNode);
        player.refreshTips();
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
                teammateCardsPanel.removeAll();
                if (CollUtil.isNotEmpty(cards)) {
                    cards.forEach(item -> {
                        JPanel cardPanel = getCardPanel(item);
                        teammateCardsPanel.add(cardPanel);
                    });
                }
                teammateCardsPanel.updateUI();
            }
        }
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

                    JPanel cardPanel = getCardPanel(item);
                    if (selectedCards.contains(item)) {
                        cardPanel.setBorder(new LineBorder(Color.WHITE, 2));
                    }
                    cardsPanel.add(cardPanel);
                    cardPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            selectedCards.clear();
                            if (item.getValue().equalsIgnoreCase("CLEAR")) {
                                if (item.getValue().equalsIgnoreCase("CLEAR")) {
                                    // 同颜色的卡牌
                                    List<Card> sameColorCards = cards.stream()
                                            .filter(c -> c.getColor().equals(item.getColor()))
                                            .collect(Collectors.toList());
                                    selectedCards.addAll(sameColorCards);
                                } else {
                                    selectedCards.add(item);
                                }
                            } else {
                                selectedCards.add(item);
                                cardPanel.setBorder(new LineBorder(Color.WHITE, 2));
                            }
                            refreshPlayerCards(player);
                        }
                    });
                });
            }
            cardsPanel.updateUI();
        }
    }

    Color ideaLineColor = new Color(0x47, 0x57, 0x65);
    Color ideaEditorColor = new Color(0x2b, 0x2b, 0x2b);
    Color ideaGrayColor = new Color(0xAF, 0xB1, 0xB3);
    Font functionFont = new Font("", 1, 9);
    Font numFont = new Font("", 1, 14);

    private JPanel getCardPanel(Card card) {
        JPanel cardPanel = new JPanel();
        cardPanel.setToolTipText(card.getToolTipText());
        cardPanel.setBorder(new LineBorder(card.getColor(), 1));
        cardPanel.setBackground(ideaLineColor);
        cardPanel.setPreferredSize(new Dimension(30, 40));
        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel();
        nameLabel.setText(card.getValue());
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setVerticalAlignment(JLabel.CENTER);
        nameLabel.setForeground(card.getColor());
        nameLabel.setFont(card.getIsFunctionCard() ? functionFont : numFont);
        nameLabel.setPreferredSize(new Dimension(30, 40));
        cardPanel.add(nameLabel);
        return cardPanel;
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
    }

    private void uno(UNOGameDto body) {
    }

    /**
     * 初牌
     * @param body
     */
    private void outCards(UNOGameDto body) {
        // 获取出牌的数据
        List<Card> outCards = (List<Card>) body.getData();
        List<Card> clearList = outCards.stream()
                .filter(item -> item.getValue().equalsIgnoreCase("CLEAR"))
                .collect(Collectors.toList());
        // 新的弃牌
        Card newDiscard = CollUtil.isNotEmpty(clearList) ? clearList.get(0) : outCards.get(0);
        // 拉出来第一个弃牌
        judgeDeque.poll();
        // 添加新的弃牌
        judgeDeque.add(newDiscard);
        // 将出牌放置到弃牌堆之中
        discardPile.addAll(outCards);
        String playerName = body.getPlayerName();
        Player player = playerMap.get(playerName);
        PlayerNode playerNode = player.getPlayerNode();
        refreshPlayerCards(player);
        refreshTeammateCards(player);
        if (CollUtil.isEmpty(playerNode.getCards())) {
            status = -1;
            gameOverButton.setEnabled(true);
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
        cardsPanel = new JPanel();
        outCardsPanel = new JPanel();
        teammateCardsPanel = new JPanel();
        selectedCards = new ArrayList<>();
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
