package cn.xeblog.plugin.game.uno.entity;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.plugin.game.uno.enums.MsgType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author eleven
 * @date 2023/4/8 22:25
 * @apiNote
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UNOGameDto extends GameDTO {
    /**
     * 玩家名字
     */
    private String playerName;

    /**
     * msg类型
     */
    private MsgType msgType;

    /**
     * 数据
     */
    private Object data;

    /**
     * 行动id
     */
    private Integer actionId;

    private PlayerNode currentPlayer;
}
