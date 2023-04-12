package cn.xeblog.plugin.game.uno.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.entity.Player;
import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import cn.xeblog.plugin.game.uno.enums.GameMode;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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

    public static void main(String[] args) {
        List<Card> cards = CardUtil.initCards(GameMode.HAPPY);
        cards.forEach(System.out::println);
        System.out.println("============================");
        List<Card> randomCard = randomCard(cards, 7);
        randomCard.sort(Card::compareTo);
        randomCard.forEach(System.out::println);
    }
}
