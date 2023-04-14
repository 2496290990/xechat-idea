package cn.xeblog.plugin.game.uno.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eleven
 * @date 2023/4/14 9:04
 * @apiNote
 */
@Getter
@AllArgsConstructor
public enum WindowMode {
    DEFAULT("Default"),
    ADAPTIVE("Adaptive");

    private String name;

    public static WindowMode getMode(String name) {
        for (WindowMode mode : values()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }

        return null;
    }
}