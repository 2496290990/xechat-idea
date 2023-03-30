package cn.xeblog.plugin.game.zillionaire.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author eleven
 * @date 2023/3/30 8:52
 * @apiNote 游戏模式
 */
@AllArgsConstructor
@Getter
public enum GameMode {
    /** 一人破产结束 */
    BROKE_EXIT("破产结束"),
    /** 一人胜出 */
    ONLY_ONE("一人胜出");

    private String name;

    public static GameMode getMode(String name) {
        for (GameMode model : values()) {
            if (model.name.equals(name)) {
                return model;
            }
        }

        return GameMode.BROKE_EXIT;
    }
}
