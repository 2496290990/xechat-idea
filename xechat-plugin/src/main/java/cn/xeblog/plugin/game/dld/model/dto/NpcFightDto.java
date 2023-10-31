package cn.xeblog.plugin.game.dld.model.dto;

import cn.xeblog.plugin.game.dld.model.entity.InstanceNpc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/10/31 10:00
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpcFightDto {

    private Integer playerId;

    private InstanceNpc npc;
}
