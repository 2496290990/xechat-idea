package cn.xeblog.plugin.game.dld.model.entity;

import lombok.Data;

/**
 * @author eleven
 * @date 2023/11/23 15:45
 * @apiNote
 */
@Data
public class Weapon {

    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 最小攻击
     */
    private Integer minDamage;

    private Integer maxDamage;

    private String intro;
}
