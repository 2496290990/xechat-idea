package cn.xeblog.plugin.game.uno.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Administrator
 * @date 2023年4月8日22:34:46
 * @apiNote UNO游戏模式
 */
@Getter
@AllArgsConstructor
public enum GameMode {
    /**
     * 经典
     */
    CLASSIC("经典模式"),
    /**
     * 欢乐模式
     */
    HAPPY("欢乐模式");

    private String name;

    public static GameMode getMode(String name) {
        for (GameMode model : values()) {
            if (model.name.equals(name)) {
                return model;
            }
        }

        return GameMode.CLASSIC;
    }
}
