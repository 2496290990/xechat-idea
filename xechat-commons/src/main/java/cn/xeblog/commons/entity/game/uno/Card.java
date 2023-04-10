package cn.xeblog.commons.entity.game.uno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

/**
 * @author eleven
 * @date 2023/4/8 22:20
 * @apiNote
 *  红黄蓝绿
 *  0 一张
 *  1 2 3 4 5 6 7 8 9 禁止 reverse +2 两张
 *  黑色
 *  换颜色  +4 各四张
 *  欢乐模式
 *  CLEAR 每种颜色两张
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card implements Comparable<Card>{
    /**
     * 评分
     */
    private Integer score;

    /**
     * 值
     */
    private String value;

    /**
     * 颜色
     */
    private Color color;

    /**
     * 是否功能牌
     */
    private Boolean isFunctionCard;

    @Override
    public int compareTo(Card card) {
        if (!color.equals(card.getColor())) {
            return colorSort(color).compareTo(colorSort(card.getColor()));
        }
        return card.getScore().compareTo(score);
    }

    private Integer colorSort(Color color) {
        if (color.equals(Color.RED)) {
            return 1;
        }
        if (color.equals(Color.YELLOW)) {
            return 2;
        }
        if (color.equals(Color.GREEN)) {
            return 3;
        }
        if (color.equals(Color.BLUE)) {
            return 4;
        }

        return 0;
    }
}
