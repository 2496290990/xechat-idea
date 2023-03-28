package cn.xeblog.commons.entity.game.zillionaire.dto;

import cn.xeblog.commons.entity.game.zillionaire.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author eleven
 * @date 2023/3/20 12:01
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CityDto extends PositionDto{

    /**
     * 等级
     */
    private Integer level;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 空地过路费
     */
    private Integer zeroToll;

    /**
     * 一级过路费
     */
    private Integer firstToll;

    /**
     * 二级过路费
     */
    private Integer secondToll;

    /**
     * 三级过路费
     */
    private Integer thirdToll;

    /**
     * 四级过路费
     */
    private Integer fourthToll;
    /**
     * 五级过路费
     */
    private Integer fifthToll;

    /**
     * 拥有者id
     */
    private Integer userId;

    /**
     * 建造价格
     */
    private Integer buildMoney;

    public void superConstructor(Integer position, String name, Color color){
        super.setPosition(position);
        super.setIsCity(true);
        super.setUpgradeAllowed(true);
        super.setName(name);
        super.setColor(color);
    }
}
