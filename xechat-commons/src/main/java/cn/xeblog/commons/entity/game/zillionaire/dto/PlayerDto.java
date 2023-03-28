package cn.xeblog.commons.entity.game.zillionaire.dto;

import lombok.Data;

import java.util.List;

/**
 * @author eleven
 * @date 2023/3/20 11:25
 * @description
 */
@Data
public class PlayerDto {
    /**
     * 玩家id
     */
    private Integer id;

    private String username;

    /**
     * 玩家金额
     */
    private Integer money = 2 * (5000 + 2000 + 1000 + 500 + 200 + 100 + 50 + 10);

    /**
     * 玩家位置
     */
    private Integer position;

    /**
     * 玩家拥有的城市
     */
    private List<CityDto> cities;

    /**
     * 玩家状态
     */
    private Boolean status = true;
}
