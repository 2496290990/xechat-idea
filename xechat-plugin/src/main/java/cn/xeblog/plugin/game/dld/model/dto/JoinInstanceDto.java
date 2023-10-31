package cn.xeblog.plugin.game.dld.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/10/31 9:29
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinInstanceDto {
    private Integer accountId;

    private Integer instanceId;
}
