package cn.xeblog.plugin.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/4/19 16:30
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionCost {
    private Integer cost;

    private PositionDto positionDto;
}
