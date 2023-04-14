package cn.xeblog.plugin.game.uno.entity;

import cn.hutool.core.collection.CollUtil;
import cn.xeblog.commons.entity.game.uno.Card;
import cn.xeblog.plugin.game.uno.enums.PlayerStatus;
import lombok.Data;

import java.util.List;

/**
 * @author eleven
 * @date 2023/4/10 9:47
 * @apiNote
 */
@Data
public class PlayerNode {
    /**
     * 玩家名字
     */
    private String playerName;
    /**
     * 玩家别名
     */
    private String alias;
    /**
     * 状态 true 正常 false 休息一次
     */
    private Boolean status;
    /**
     * 是否喊UNO
     */
    private Boolean uno;
    /**
     * 玩家状态
     */
    private PlayerStatus playerStatus;

    /**
     * 卡片
     */
    private List<Card> cards;

    /**
     * 手牌数
     */
    private int cardsTotal;


    /**
     * 前一位玩家
     */
    private PlayerNode prevPlayer;

    /**
     * 后一位玩家
     */
    private PlayerNode nextPlayer;

    public int getCardsTotal() {
        return CollUtil.isEmpty(cards) ? 0 : cards.size();
    }

    public PlayerNode() {

    }

    @Override
    public String toString() {
        return "PlayerNode{" +
                "playerName='" + playerName + '\'' +
                ", alias='" + alias + '\'' +
                ", status=" + status +
                ", cards=" + cards +
                ", cardsTotal=" + cardsTotal +
                '}';
    }
}
