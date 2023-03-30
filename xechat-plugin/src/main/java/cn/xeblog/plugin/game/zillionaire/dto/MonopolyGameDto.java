package cn.xeblog.plugin.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.plugin.game.zillionaire.enums.MsgType;
import lombok.Data;

/**
 * @author eleven
 * @date 2023/3/30 8:58
 * @apiNote
 */
@Data
public class MonopolyGameDto extends GameDTO {
    /**
     * msg类型
     */
    private MsgType msgType;

    /**
     * 玩家名称
     */
    private String player;

    /**
     * 当前玩家
     */
    private PlayerNode currentPlayer;

    /**
     * 动作id
     */
    private Integer actionId;
}
