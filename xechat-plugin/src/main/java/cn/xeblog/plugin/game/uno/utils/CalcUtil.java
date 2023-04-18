package cn.xeblog.plugin.game.uno.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.entity.Player;
import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import cn.xeblog.plugin.game.uno.enums.GameMode;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author eleven
 * @date 2023/4/8 22:25
 * @apiNote
 */
@Slf4j
public class CalcUtil {
    /**
     * 获取卡牌
     * @param cards     卡牌组
     * @param num       随机卡牌数量
     * @return  List    随机卡牌
     */
    public static List<Card> randomCard(List<Card> cards, Integer num) {
        List<Card> result = new ArrayList<>(num);
        while (num > 0) {
            Collections.shuffle(cards);
            result.add(cards.get(0));
            cards.remove(0);
            num--;
        }
        return result;
    }

    public static Card randomOneCard(List<Card> cards) {
        Collections.shuffle(cards);
        Card card = cards.get(0);
        cards.remove(0);
        return card;
    }

    /**
     * 获取质疑结果
     * @param judgeDeque        判断牌堆
     * @param player            玩家
     * @return Boolean          质疑结果
     */
    public static Boolean question(ArrayDeque<Card> judgeDeque, Player player) {
        return question(judgeDeque, player.getPlayerNode());
    }

    /**
     * 返回质疑结果
     * @param judgeDeque        判断牌堆
     * @param playerNode        玩家节点
     * @return  Boolean         质疑结果
     */
    public static Boolean question(ArrayDeque<Card> judgeDeque, PlayerNode playerNode) {
        Card card = judgeDeque.peekFirst();
        List<Card> cards = playerNode.getCards();
        cards = cards.stream()
                .filter(item -> !item.getIsFunctionCard())
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(cards)) {
            return false;
        }
        long count = cards.stream()
                .filter(item -> card.getColor().equals(item.getColor()) || card.getValue().equalsIgnoreCase(item.getValue()))
                .count();
        return count > 0;
    }

    /**
     * 获取要切换的颜色
     * @param playerCards   玩家卡牌
     * @return
     */
    public static Color getChangeColor(List<Card> playerCards) {
        // 如果只剩下 +4 Change了就直接边红色
        playerCards = playerCards.stream()
                .filter(item -> item.getColor().equals(Color.BLACK))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(playerCards)) {
            return Color.RED;
        }

        Map<Color, List<Card>> colorListMap = playerCards.stream()
                .collect(Collectors.groupingBy(Card::getColor));
        Map<Integer, Color> map = new HashMap<>();
        colorListMap.forEach((k,v) -> map.put(v.size(), k));
        List<Integer> countList = new ArrayList<>(map.keySet());
        countList.sort(Integer::compareTo);
        Color color = map.get(countList.get(countList.size() - 1));
        return color.equals(Color.BLACK) ? Color.RED : color;
    }


    public static Boolean hasCanOutCards(PlayerNode playerNode, ArrayDeque<Card> judgeDeque, GameMode gameMode) {
        return hasCanOutCards(playerNode.getCards(), judgeDeque, gameMode);
    }

    /**
     * 判断玩家当前是否有可以出的牌
     * @param playerCards   玩家手牌
     * @param judgeDeque    判断牌堆
     * @return
     */
    public static Boolean hasCanOutCards(List<Card> playerCards, ArrayDeque<Card> judgeDeque, GameMode gameMode) {
        // 欢乐模式
        if (gameMode.equals(GameMode.HAPPY)) {

        }
        Card judgeCard = judgeDeque.peekLast();
        // 如果玩家手牌存在 +4 CHANGE 黑色牌 或者有同颜色 同数字的牌就可以出
        long count = playerCards.stream()
                .filter(item -> item.getColor().equals(Color.BLACK) ||
                        item.getColor().equals(judgeCard.getColor()) ||
                        item.getColor().equals(judgeCard.getChangeColor()) ||
                        StrUtil.equalsIgnoreCase(item.getValue(), judgeCard.getValue()))
                .count();
        return count > 0;

    }

    public static Boolean canOut(List<Card> selectedCards, ArrayDeque<Card> judgeDeque) {
        if (CollUtil.isEmpty(selectedCards)) {
            return false;
        }

        int size = selectedCards.size();
        Card peekLast = judgeDeque.peekLast();
        Color judgeColor = peekLast.getColor();
        if (size == 1) {
            Card outCard = selectedCards.get(0);
            String outCardValue = outCard.getValue();
            // 如果是 +4或者是变换颜色的话允许出牌
            if (outCard.getColor().equals(Color.BLACK)) {
                return true;
            }
            // 如果要出的牌和最后一张的颜色相同的允许出牌
            if (outCard.getColor().equals(judgeColor)){
                return true;
            }

            // 如果值一样的花允许出牌
            if (outCard.getValue().equalsIgnoreCase(peekLast.getValue())) {
                return true;
            }
        }

        if (size > 1) {
            selectedCards.sort(Card::compareTo);
            return selectedCards.get(0).getColor().equals(judgeColor);
        }
        return false;
    }

    public static List<Card> tips(List<Card> cards, ArrayDeque<Card> judgeDeque, GameMode gameMode) {
        Card last = judgeDeque.peekLast();
        // 如果最后一张牌变更过颜色就用变更之后的，苟泽就用原本的颜色就行了
        Color lastColor = null == last.getChangeColor() ? last.getColor() : last.getChangeColor();
        // 欢乐模式
        if (gameMode.equals(GameMode.HAPPY)) {
            // 如果是换了模式并且是 +2 的话
            if (StrUtil.equalsIgnoreCase(CardUtil.ADD_2, last.getValue())) {
                List<Card> add2Cards = outSameValueCards(cards, CardUtil.ADD_2);
                // 如果有 +2 牌则出+2 否则出+4
                return CollUtil.isNotEmpty(add2Cards) ?
                    add2Cards :
                    outSameValueCards(cards, CardUtil.ADD_4);
            }
            // 欢乐模式并且当前牌是+4 则出+4
            if (StrUtil.equalsIgnoreCase(CardUtil.ADD_4, last.getValue())) {
                return outSameValueCards(cards, CardUtil.ADD_4);
            }
        }

        // 获取同颜色的牌
        List<Card> sameColorCards = filterCards(cards, (item) -> item.getColor().equals(lastColor));
        log.info("筛选同颜色的卡片，结果为 -{}", sameColorCards);
        boolean hasSameColorCards = CollUtil.isNotEmpty(sameColorCards);
        if (hasSameColorCards) {
            return outSameColorCards(sameColorCards);
        } else if (last.getColor().equals(Color.BLACK)) {
            return outBlackCards(cards);
        } else {
            return outSameValueCards(cards, last.getValue());
        }
        //// 首先判断黑色牌 +4 CHANGE
        //if (last.getColor().equals(Color.BLACK)) {
        //    // 如果有同颜色的牌则出同颜色牌 否则的话出黑色功能牌
        //    return hasSameColorCards ?
        //            outSameColorCards(sameColorCards) :
        //            outBlackCards(cards);
        //}
        //return hasSameColorCards ?
        //        outSameColorCards(sameColorCards) :
        //        outSameValueCards(cards, last.getValue());
    }

    /**
     * 出 一样值的卡牌
     * @param cards
     * @return
     */
    private static List<Card> outSameValueCards(Collection<Card> cards, String lastValue) {
        List<Card> sameValueCards = filterCards(cards, (item) -> StrUtil.equalsIgnoreCase(item.getValue(), lastValue));
        if (CollUtil.isEmpty(sameValueCards)) {
            return new ArrayList<>();
        }
        log.info("筛选同值卡牌,结果为-{}", sameValueCards);
        List<Color> colorList = sameValueCards.stream()
                .map(Card::getColor)
                .collect(Collectors.toList());
        int max = 0;
        List<Card> maxColorCards = new ArrayList<>();
        for (Color color : colorList) {
            List<Card> colorCards = filterCards(cards, (item) -> item.getColor().equals(color));
            log.info("筛选同值同颜色卡牌,当前颜色{},结果为-{}", color, sameValueCards);
            if (colorCards.size() > max) {
                maxColorCards = colorCards;
            }
        }
        // 获取当前值在最多牌中的坐标
        int index = valueIndex(maxColorCards, lastValue);
        return singleCardToList(maxColorCards.get(index));
    }


    /**
     * 出同颜色的牌
     * @param sameColorCards
     * @return
     */
    private static List<Card> outSameColorCards(List<Card> sameColorCards) {
        // 如果同颜色的卡牌当中存在 CLEAR卡牌则全出
        int clearIndex = valueIndex(sameColorCards, CardUtil.CLEAR);
        if (clearIndex != -1) {
            return sameColorCards;
        }
        // 如果不包含CLEAR 就代表是经典模式 按照分值排序出第一张就可以
        sameColorCards.sort(Card::compareTo);
        return cardsByIndex(sameColorCards, 0);
    }

    /**
     * 出黑色的功能牌
     * @param cards     牌堆
     * @return
     */
    private static List<Card> outBlackCards(List<Card> cards) {
        // 不存在相同颜色的卡牌
        int changeIndex = valueIndex(cards, CardUtil.CHANGE);
        if (changeIndex != -1) {
            return cardsByIndex(cards, changeIndex);
        }
        int add4Index = valueIndex(cards, CardUtil.ADD_4);

        if (add4Index != -1) {
            return cardsByIndex(cards, add4Index);
        }
        return new ArrayList<>();
    }

    /**
     * 提示卡牌
     * @param cards
     * @param index
     * @return
     */
    private static List<Card> cardsByIndex(List<Card> cards, Integer index) {
        return singleCardToList(cards.get(index));
    }

    /**
     * 单个卡牌转换成集合
     * @param card      卡牌
     * @return  List    提示卡牌
     */
    private static List<Card> singleCardToList(Card card) {
        return new ArrayList<>(Collections.singletonList(card));
    }

    /**
     * 返回颜色的牌
     * @param   cards
     * @param  predicate    判断公式
     * @return
     */
    private static List<Card> filterCards(Collection<Card> cards, Predicate<? super Card> predicate) {
        List<Card> filterCards = cards.stream()
                .filter(predicate)
                .collect(Collectors.toList());
        return filterCards;
    }

    /**
     * 根据value获取位置
     * @param cards  筛选卡牌
     * @param value 值
     * @return
     */
    private static Integer valueIndex(List<Card> cards, String value) {
        List<Card> filterCards = filterCards(cards, (item) -> StrUtil.equalsIgnoreCase(item.getValue(), value));
        return CollUtil.isNotEmpty(filterCards) ? cards.indexOf(filterCards.get(0)) : -1;
    }

    /**
     * 判断是否要喊UNO
     * @param playerCards       玩家手牌
     * @param outCards          要出的牌
     * @return  Boolean         如果出了要出的牌之后就剩一张了则要喊UNO
     */
    public static Boolean sayUNo(List<Card> playerCards, List<Card> outCards) {
        return playerCards.size() - outCards.size() == 1;
    }

    /**
     * 获取上一名玩家
     * @param anticlockwise 逆时针
     * @param playerMap     玩家集合
     * @param playerName    玩家名称
     * @param nextFlag      是否查看下家
     * @return  Player      玩家
     */
    public static Player getOtherPlayer(Boolean anticlockwise, Map<String, Player> playerMap, Boolean nextFlag, String playerName) {
        Player currentPlayer = playerMap.get(playerName);
        PlayerNode playerNode = currentPlayer.getPlayerNode();
        PlayerNode otherPlayer;
        if (!nextFlag) {
            // 获取上一个 逆时针就上一个， 顺时针就下一个
            otherPlayer = anticlockwise ? playerNode.getPrevPlayer() : playerNode.getNextPlayer();
        } else {
            // 获取下一个人 逆时针就获取下下一个 顺时针就获取上一个
            otherPlayer = anticlockwise ? playerNode.getNextPlayer() : playerNode.getPrevPlayer();
        }
        return playerMap.get(otherPlayer.getPlayerName());
    }

    /**
     * 获取上一名玩家节点
     * @param anticlockwise 逆时针
     * @param playerMap     玩家集合
     * @param playerName    玩家名称
     * @param nextFlag      是否查看下家
     * @return  PlayerNode  玩家节点
     */
    public static PlayerNode getOtherPlayerNode(Boolean anticlockwise, Map<String, Player> playerMap,Boolean nextFlag, String playerName) {
        return getOtherPlayer(anticlockwise, playerMap, nextFlag, playerName).getPlayerNode();
    }

    /**
     * 获取上一名玩家名称
     * @param anticlockwise 逆时针
     * @param playerMap     玩家集合
     * @param playerName    玩家名称
     * @param nextFlag      是否查看下家
     * @return  String      玩家名称
     */
    public static String getOtherPlayerName(Boolean anticlockwise, Map<String, Player> playerMap,Boolean nextFlag, String playerName) {
        return getOtherPlayerNode(anticlockwise, playerMap, nextFlag, playerName).getPlayerName();
    }


    public static void main(String[] args) {
        List<Card> cards = CardUtil.initCards(GameMode.HAPPY);
    }
}
