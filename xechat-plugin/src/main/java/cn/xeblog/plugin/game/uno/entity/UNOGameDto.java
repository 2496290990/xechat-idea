package cn.xeblog.plugin.game.uno.entity;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.plugin.game.uno.enums.MsgType;
import lombok.Data;

/**
 * @author eleven
 * @date 2023/4/8 22:25
 * @apiNote
 */
@Data
public class UNOGameDto extends GameDTO {
    private String playerName;

    private Object data;

    private MsgType msgType;
}
