package cn.xeblog.plugin.game.dld.model.dto;

import cn.xeblog.plugin.game.dld.model.common.Page;
import lombok.Data;

/**
 * @author eleven
 * @date 2023/11/21 15:48
 * @apiNote
 */
@Data
public class QueryWeaponDto {
    private Integer playerId;

    private String accountId;

    private String weaponId;

    private String weaponName;

    private Integer grade;

    private Page page;
}
