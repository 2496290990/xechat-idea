package cn.xeblog.commons.entity.game.uno;

import lombok.Data;

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
public class Card {
    private Integer score;

    private String value;

    private Color color;
}
