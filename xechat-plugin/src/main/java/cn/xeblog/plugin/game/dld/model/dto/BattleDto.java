package cn.xeblog.plugin.game.dld.model.dto;

import lombok.Data;

/**
 * @author eleven
 * @date 2023/10/13 15:51
 * @apiNote
 */
@Data
public class BattleDto {

    /**
     * 攻击者
     */
    private PlayerDto attacker;

    /**
     * 防御者
     */
    private PlayerDto defender;

    public BattleDto( String defenderMac) {
        this.attacker = new PlayerDto();
        this.defender = new PlayerDto(defenderMac);
    }
}
