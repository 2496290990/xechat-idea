package cn.xeblog.plugin.game.uno.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 玩家模式
 *
 * @author Administrator
 * @date 2023/04/08
 */
@Getter
@AllArgsConstructor
public enum PlayerMode {
    /**
     * 单人模式
     */
    SINGLE("单人模式"),
    /**
     * 双人模式
     */
    DOUBLE("双人模式");
    private String name;

    public static PlayerMode getMode(String name) {
        for (PlayerMode model : values()) {
            if (model.name.equals(name)) {
                return model;
            }
        }

        return PlayerMode.SINGLE;
    }
}
