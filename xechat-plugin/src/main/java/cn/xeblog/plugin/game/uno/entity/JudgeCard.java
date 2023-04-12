package cn.xeblog.plugin.game.uno.entity;

import cn.xeblog.commons.entity.game.uno.Card;
import lombok.Data;

import java.awt.*;
import java.util.List;


/**
 * @author eleven
 * @date 2023/4/12 15:22
 * @apiNote
 */
@Data
public class JudgeCard {

    private Color color;

    private Integer sum;

    private Integer score;

    private List<Card> cardList;
}
