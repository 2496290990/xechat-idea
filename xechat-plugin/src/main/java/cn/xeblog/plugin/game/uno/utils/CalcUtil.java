package cn.xeblog.plugin.game.uno.utils;

import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.enums.GameMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static void main(String[] args) {
        List<Card> cards = CardUtil.initCards(GameMode.HAPPY);
        cards.forEach(System.out::println);
        System.out.println("============================");
        List<Card> randomCard = randomCard(cards, 7);
        randomCard.sort(Card::compareTo);
        randomCard.forEach(System.out::println);
    }
}
