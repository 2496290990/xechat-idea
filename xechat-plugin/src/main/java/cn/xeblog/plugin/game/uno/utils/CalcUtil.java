package cn.xeblog.plugin.game.uno.utils;

import cn.hutool.core.collection.CollUtil;
import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.entity.Player;
import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import cn.xeblog.plugin.game.uno.enums.GameMode;

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

    public static void main(String[] args) {
        List<Card> cards = CardUtil.initCards(GameMode.HAPPY);
        cards.forEach(System.out::println);
        System.out.println("============================");
        List<Card> randomCard = randomCard(cards, 7);
        randomCard.sort(Card::compareTo);
        randomCard.forEach(System.out::println);
    }
}
