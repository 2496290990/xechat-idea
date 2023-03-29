package cn.xeblog.plugin.game.zillionaire.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eleven
 * @date 2023/3/29 9:00
 * @description
 */
@Getter
@AllArgsConstructor
public enum WindowMode {
    /** 默认 */
    DEFAULT("Default"),
    /** 自适应 */
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