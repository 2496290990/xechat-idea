package cn.xeblog.plugin.game.uno.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.entity.Player;
import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import cn.xeblog.plugin.game.uno.enums.GameMode;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author eleven
 * @date 2023/4/8 22:25
 * @apiNote
 */
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
        return map.get(countList.get(countList.size() - 1));
    }


    public static Boolean hasCanOutCards(PlayerNode playerNode, ArrayDeque<Card> judgeDeque) {
        return hasCanOutCards(playerNode.getCards(), judgeDeque);
    }

    /**
     * 判断玩家当前是否有可以出的牌
     * @param playerCards   玩家手牌
     * @param judgeDeque    判断牌堆
     * @return
     */
    public static Boolean hasCanOutCards(List<Card> playerCards, ArrayDeque<Card> judgeDeque) {
        Card judgeCard = judgeDeque.peekLast();
        // 如果玩家手牌存在 +4 CHANGE 黑色牌 或者有同颜色 同数字的牌就可以出
        long count = playerCards.stream()
                .filter(item -> item.getColor().equals(Color.BLACK) ||
                        item.getColor().equals(judgeCard.getColor()) ||
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

    /**
     * 获取提示
     * @param cards         玩家手牌
     * @param judgeDeque    判断牌堆
     * @param gameMode      游戏模式
     * @return List         提示牌面
     *
     * 首先，如果是经典模式的话
     *      1. 获取牌堆最后一张牌的值
     *          1.1 如果是 +4 或者是 CHANGE的话 走判断颜色逻辑
     *          1.2 获取有相同值的颜色卡牌数量
     *              1.2.1 如果颜色卡牌数量相同
     *                  1.2.1.1 判断颜色分值
     *                      1.2.1.1 如果颜色分值还一样，那就按照颜色排序然后取出来 与判断牌值相同的牌
     *                      1.2.1.2 不一样则从分值最高的里边取出来与判断牌值相同的牌
     *              1.2.2 返回颜色卡牌最多的中牌值相同的
     * 如果是欢乐模式
     *      要判断 +2 之后能出+4但是+4之后不能出+2
     *      判断 CLEAR 能出CLEAR
     *
     */
    public static List<Card> showTips(List<Card> cards, ArrayDeque<Card> judgeDeque, GameMode gameMode) {
        Card judgeCard = judgeDeque.peekLast();
        Color judgeCardColor = judgeCard.getColor();
        // 相同颜色的牌
        List<Card> sameColorCards = cards.stream()
                .filter(item -> item.getColor().equals(judgeCardColor))
                .collect(Collectors.toList());
        // 相同值的牌
        Set<Card> sameValueCards = cards.stream()
                .filter(item -> StrUtil.equalsIgnoreCase(item.getValue(), judgeCard.getValue()))
                .collect(Collectors.toSet());
        // 如果有值相同的卡牌的话
        if (CollUtil.isNotEmpty(sameValueCards)) {
            Map<Color, List<Card>> colorMap = cards.stream()
                    .collect(Collectors.groupingBy(Card::getColor));
            Map<Integer, List<Color>> colorCountMap = new HashMap<>();
            //
            for (Card sameColorCard : sameColorCards) {
                Color color = sameColorCard.getColor();
                // 相同颜色牌的数量
                int sameColorCount = colorMap.get(color).size();
                List<Color> colorList = colorCountMap.get(sameColorCount);
                if (CollUtil.isEmpty(colorList)) {
                    colorList = new ArrayList<>();
                }
                colorList.add(color);
            }
            ArrayList<Integer> countList = new ArrayList<>(colorCountMap.keySet());
            countList.sort(Integer::compareTo);
            Integer maxCount = countList.get(countList.size() - 1);
            List<Color> maxColorList = colorCountMap.get(maxCount);
            // 如果存在同样值的对应颜色卡牌数量一样的话
            if (maxColorList.size() > 1) {
                return sameValueAndMaxColorCountMoreThanOne(maxColorList, colorMap);
            } else {

            }
        }
        return null;
    }
    /**
     * 存在于判断牌相同值的颜色剩余卡牌数量一致，且数量大于一个
     * @return
     */
    private static List<Card> sameValueAndMaxColorCountMoreThanOne(List<Color> maxColorList, Map<Color, List<Card>> colorMap) {

        for (Color color : maxColorList) {
            Integer colorCount = colorCount(colorMap.get(color), color, 0);
        }
        return null;
    }

    private static void test(List<Card> cards, Card card){

    }

    /**
     * 计算颜色值类型
     * @param cards     卡牌组
     * @param color     颜色
     * @param type      类型 0计算数量 1计算分值
     * @return
     */
    private static Integer colorCount(List<Card> cards, Color color, Integer type) {
        int sum = 0;
        for (Card card : cards) {
            if (card.getColor().equals(color)) {
                if (type == 0) {
                    sum++;
                }

                if (type == 2) {
                    sum += card.getScore();
                }
            }
        }
        return sum;
    }


    public static void main(String[] args) {
        List<Card> cards = CardUtil.initCards(GameMode.HAPPY);
    }
}
