package cn.xeblog.plugin.game.uno.action;

import cn.xeblog.plugin.game.uno.entity.PlayerNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/4/10 9:57
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PlayerAction {
    /**
     * 玩家节点
     */
    protected PlayerNode playerNode;


}
