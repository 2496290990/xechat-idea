package cn.xeblog.plugin.game.uno.enums;

import lombok.Getter;

/**
 * @author eleven
 * @date 2023/4/14 16:11
 * @apiNote
 */
@Getter
public enum PlayerStatus {
    /**
     * 等待
     */
    WAITING("等待中"),
    /**
     * 思考
     */
    THINKING("思考中"),
    /**
     * 跳过
     */
    SKIP("禁止出牌"),
    /**
     * 下一个
     */
    NEXT("下家"),
    /**
     * 上家
     */
    PREV("上家");

    private String status;

    private PlayerStatus(String status) {
        this.status = status;
    }
}
