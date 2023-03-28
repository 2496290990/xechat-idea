package cn.xeblog.commons.entity.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.zillionaire.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author eleven
 * @date 2023/3/20 13:30
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PositionDto extends GameDTO {

    /**
     * 位置
     */
    private Integer position;

    /**
     * 是城市
     */
    private Boolean isCity;

    /**
     * 名字
     */
    private String name;

    /**
     * 颜色
     */
    private Color color;

    /**
     * 拥有者
     */
    private String owner;

    /**
     * 是否允许升级
     */
    private Boolean upgradeAllowed;

    /**
     * 行动
     */
    private Integer action;
}
