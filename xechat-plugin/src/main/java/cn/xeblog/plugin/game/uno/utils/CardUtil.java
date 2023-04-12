package cn.xeblog.plugin.game.uno.utils;

import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.enums.GameMode;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author eleven
 * @date 2023/4/10 9:07
 * @apiNote 卡牌组工具类
 */
public class CardUtil {

    public static final String ICON_SKIP = "⊘";
    public static final String ICON_REVERSE_LEFT = "↺";
    public static final String ICON_REVERSE_RIGHT = "↻";
    public static final String ICON_CHANGE = "⊕ ";
    public static final String SKIP = "SKIP";
    public static final String REVERSE = "REVERSE";
    public static final String CHANGE = "CHANGE";
    public static final String CLEAR = "CLEAR";
    public static final String ADD_2 = "+2";
    public static final String ADD_4 = "+4";

    /**
     * 初始化卡牌组
     * @param gameMode 游戏模式
     * @return  List
     * 如果是经典模式，则返回 四种颜色的 0一张 1-9 SKIP +2 REVERSE 各两张 +4 CHANGE 各四张
     * 如果是欢乐模式  则返回双倍的经典卡牌加四张CLEAR各颜色卡牌
     *
     */
    public static List<Card> initCards(GameMode gameMode) {
        List<Card> classicCards = classicCards();
        if (gameMode.equals(GameMode.CLASSIC)) {
            return classicCards;
        }

        if (gameMode.equals(GameMode.HAPPY)) {
            List<Card> resultList = new ArrayList<>();
            List<Card> clearCards = clearCard();
            resultList.addAll(classicCards);
            resultList.addAll(classicCards);
            resultList.addAll(clearCards);
            resultList.addAll(clearCards);
            return resultList;
        }
        throw new RuntimeException("游戏模式错误");
    }

    private static List<Card> clearCard() {
        List<Color> colorList = colorList();
        List<Card> result = new ArrayList<>();
        for (Color color : colorList) {
            result.add(new Card(25, CLEAR, color, true));
            result.add(new Card(25, CLEAR, color, true));
        }
        return result;
    }

    /**
     * 经典卡皮
     * @return
     */
    private static List<Card> classicCards() {
        List<Card> resultList = new ArrayList<>();
        resultList.addAll(commonsCard());
        resultList.addAll(zeroAndFunctionCard());
        return resultList;
    }

    /**
     * 基础卡牌
     * @return
     */
    private static List<Card> commonsCard() {
        List<Card> commonsCards = new ArrayList<>();
        List<Color> colorList = colorList();
        List<String> functionCards = new ArrayList<>();
        Collections.addAll(functionCards, SKIP, REVERSE, ADD_2);
        Map<String, Integer> commonsMap = commonsMap();
        for (Color color : colorList) {
            for (String key : commonsMap.keySet()) {
                commonsCards.add(new Card(commonsMap.get(key), key, color, functionCards.contains(key)));
                commonsCards.add(new Card(commonsMap.get(key), key, color, functionCards.contains(key)));
            }
        }
        return commonsCards;
    }

    private static Map<String, Integer> commonsMap() {
        Map<String, Integer> map = new HashMap<>(13);
        map.put( "1", 1);
        map.put( "2", 2);
        map.put( "3", 3);
        map.put( "4", 4);
        map.put( "5", 5);
        map.put( "6", 6);
        map.put( "7", 7);
        map.put( "8", 8);
        map.put( "9", 9);
        map.put( SKIP, 20);
        map.put( REVERSE, 20);
        map.put( ADD_2, 20);
        return map;
    }

    /**
     * 颜色集合
     * @return
     */
    private static List<Color> colorList(){
        List<Color> colorList = new ArrayList<>();
        Collections.addAll(colorList, JBColor.RED, JBColor.YELLOW, JBColor.GREEN, JBColor.BLUE);
        return colorList;
    }

    /**
     * 0 和功能牌
     *
     * @return
     */
    private static List<Card> zeroAndFunctionCard() {
        List<Card> result = new ArrayList<>(12);
        Card changeColor = new Card(50, CHANGE, JBColor.BLACK, true);
        Card add4 = new Card(50, ADD_4, JBColor.BLACK, true);
        Collections.addAll(result,
                new Card(0, "0", JBColor.RED, false),
                new Card(0, "0", JBColor.YELLOW, false),
                new Card(0, "0", JBColor.GREEN, false),
                new Card(0, "0", JBColor.BLUE, false),
                changeColor, changeColor, changeColor, changeColor,
                add4, add4, add4, add4
        );
        return result;
    }

    public static void main(String[] args) {
        Card disCard = null;
        List<Card> cardList = initCards(GameMode.HAPPY);
        do {
            disCard = CalcUtil.randomOneCard(cardList);
            System.out.println(disCard);
        } while (disCard.getIsFunctionCard());
    }
}
