package cn.xeblog.plugin.game.uno.entity;


import cn.xeblog.plugin.game.uno.ui.UserPanel;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author eleven
 * @date 2023/4/10 9:47
 * @apiNote
 */
@Data
@NoArgsConstructor
public class Player {
    /**
     * 玩家节点
     */
    private PlayerNode playerNode;

    private UserPanel userPanel;

    public Player(PlayerNode playerNode) {
        this.playerNode = playerNode;
    }


}
