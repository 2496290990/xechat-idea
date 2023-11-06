package cn.xeblog.plugin.game.dld.model.vo;

import lombok.Data;

/**
 * @author eleven
 * @date 2023/10/26 11:02
 * @apiNote
 */
@Data
public class PlayerInfoVo {
    private Integer id;

    private String account;

    private Integer level;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 血量
     */
    private Integer hp;

    /**
     * 攻击力
     */
    private Integer attack;

    /**
     * 防御力
     */
    private Integer defence;

    /**
     * 命中率
     */
    private Double hitRate;

    /**
     * 闪避率
     */
    private Double flee;

    /**
     * 连击率
     */
    private Double comboRate;

    private Integer exp;

    private Double criticalChance;

    private Integer speed;

    private Integer nextLvExp;

    private Integer energy;

    private Integer maxEnergy;

}
