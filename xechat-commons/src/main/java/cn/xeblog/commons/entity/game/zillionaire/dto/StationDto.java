package cn.xeblog.commons.entity.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.zillionaire.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/3/28 9:12
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDto extends PositionDto{


    /**
     * 一台价格
     */
    private Integer oneStationPrice;
    /**
     * 两个站价格
     */
    private Integer twoStationPrice;
    /**
     * 三个站价格
     */
    private Integer threeStationPrice;
    /**
     * 四站价格
     */
    private Integer fourStationPrice;


    public void superConstructor(Integer position, String name){
        this.oneStationPrice = 250;
        this.twoStationPrice = 500;
        this.threeStationPrice = 1000;
        this.fourStationPrice = 2000;
        super.setPosition(position);
        super.setName(name);
        super.setIsCity(false);
        super.setUpgradeAllowed(false);
        super.setColor(Color.NULL);
    }
}
