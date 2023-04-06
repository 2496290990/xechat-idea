package cn.xeblog.plugin.game.zillionaire.action;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.zillionaire.dto.*;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;
import cn.xeblog.plugin.game.zillionaire.utils.ZillionaireUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author eleven
 * @date 2023/3/29 14:59
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class PlayerAction {
    /**
     * 城市
     */
    private List<CityDto> cities;

    /**
     * 站
     */
    private List<StationDto> stations;

    /**
     * 公司
     */
    private List<CompanyDto> companies;

    /**
     * 玩家节点
     */
    protected PlayerNode playerNode;

    public PlayerAction(PlayerNode playerNode) {
        this.playerNode = playerNode;
    }

    /**
     * 投掷骰子并且移动
     */
    public void diceRollAndMovePosition() {
        int randomInt = RandomUtil.randomInt(1, 12);
        int position = playerNode.getPosition() + randomInt;
        playerNode.setPosition(position % 40);
    }

    /**
     * 购买地铺
     * @param playerNode
     * @param position
     */
    public void buy(PlayerNode playerNode, PositionDto position) {
        if (StrUtil.isBlank(position.getOwner()) && position.getAllowBuy()) {
            // 获取用户的现金
            Integer cash = playerNode.getCash();
            Integer price;
            if (position instanceof CityDto) {
                CityDto city = (CityDto) position;
                price = city.getPrice();
                if ( cash >= price) {
                    playerNode.setCash(cash - price);
                    List<CityDto> cities = playerNode.getCities();
                    cities.add(city);
                    playerNode.setCities(cities);
                }
            }

            if (position instanceof StationDto) {
                StationDto station = (StationDto) position;
                price = station.getPrice();
                if ( cash >= price) {
                    playerNode.setCash(cash - price);
                    List<StationDto> stations = playerNode.getStations();
                    stations.add(station);
                    playerNode.setStations(stations);
                }
            }

            if (position instanceof CompanyDto) {
                CompanyDto company = (CompanyDto) position;
                price = company.getPrice();
                if ( cash >= price) {
                    playerNode.setCash(cash - price);
                    List<CompanyDto> companies = playerNode.getCompanies();
                    companies.add(company);
                    playerNode.setCompanies(companies);
                }
            }
        }
    }

    /**
     * 赎回地皮
     * @param playerNode    玩家节点
     * @param position      坐标点
     */
    public void redemption(PlayerNode playerNode, PositionDto position) {
        String owner = position.getOwner();
        if (StrUtil.isBlank(owner)) {
            buy(playerNode, position);
            return;
        }
        if (StrUtil.equalsIgnoreCase(owner, playerNode.getPlayer()) && position.getAllowBuy()) {
            // 获取用户的现金
            Integer cash = playerNode.getCash();
            Integer price;
            if (position instanceof CityDto) {
                CityDto city = (CityDto) position;
                price = city.getPrice();
                if ( cash >= price) {
                    playerNode.setCash(cash - price);
                    List<CityDto> cities = playerNode.getCities();
                    updatePositionStatus(cities, position, true);
                    playerNode.setCities(cities);
                }
            }

            if (position instanceof StationDto) {
                StationDto station = (StationDto) position;
                price = station.getPrice();
                if ( cash >= price) {
                    playerNode.setCash(cash - price);
                    List<StationDto> stations = playerNode.getStations();
                    updatePositionStatus(stations, position, true);
                    playerNode.setStations(stations);
                }
            }

            if (position instanceof CompanyDto) {
                CompanyDto company = (CompanyDto) position;
                price = company.getPrice();
                if ( cash >= price) {
                    playerNode.setCash(cash - price);
                    List<CompanyDto> companies = playerNode.getCompanies();
                    updatePositionStatus(companies, position, true);
                    playerNode.setCompanies(companies);
                }
            }
        }
    }

    /**
     * 更新地皮状态
     * @param list
     * @param position
     * @param status
     */
    private void updatePositionStatus(List<? extends PositionDto> list, PositionDto position, Boolean status){
        for (PositionDto item : list) {
            if (item.getPosition().equals(position.getPosition())) {
                item.setPositionStatus(status);
                break;
            }
        }
    }

    /**
     * 随机机会
     * @return
     */
    private LuckEntity randomChance(){
        return RandomUtil.randomEle(ZillionaireUtil.chanceCards);
    }

    /**
     * 随机命运
     * @return
     */
    private LuckEntity randomDestiny(){
        return RandomUtil.randomEle(ZillionaireUtil.destinyCards);
    }

    /**
     * 卖房子
     * Map  key 地皮坐标点  value 售卖的房子数量
     */
    public abstract Map<Integer, Integer> saleBuild();

    /**
     * 投掷色子
     * @return Integer 返回点数
     */
    public abstract Integer diceRoll();

    public void gameOver(String msg){
        AlertMessagesUtil.showInfoDialog("提示", msg);
    }

    /**
     * 是否购买
     * @param money 话费
     * @return boolean
     */
    public abstract boolean whetherToBuy(Integer money);

    /**
     * 获取要拆除的建的地皮坐标
     * @return
     */
    public abstract Integer getPullDownBuilding();

    /**
     * 获取免费升级的地皮坐标
     * @return
     */
    public abstract Integer getFreeBuildingPosition();
}
