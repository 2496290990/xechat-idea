package cn.xeblog.plugin.game.zillionaire.action;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xeblog.commons.entity.game.zillionaire.dto.CityDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.CompanyDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.StationDto;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;
import cn.xeblog.plugin.game.zillionaire.dto.PositionCost;
import cn.xeblog.plugin.game.zillionaire.utils.CalcUtil;

import javax.swing.text.Position;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author eleven
 * @date 2023/3/29 14:59
 * @description
 */
public class AiPlayerAction extends PlayerAction{

    public AiPlayerAction(PlayerNode playerNode) {
        super(playerNode);
    }

    /**
     * 按照地皮的过路费进行排序，然后从头开始计算
     * @return
     */
    @Override
    public List<Integer> saleBuild() {
        // 获取差额
        PlayerNode playerNode = getPlayerNode();
        int debt = Math.abs(playerNode.getCash());
        List<CityDto> cities = playerNode.getCities();
        List<StationDto> stations = playerNode.getStations();
        List<CompanyDto> companies = playerNode.getCompanies();
        List<PositionCost> costList = new ArrayList<>();
        // 如果公司不为空
        if (CollUtil.isNotEmpty(companies)) {
            if (companies.size() == 1) {
                costList.add(new PositionCost(120, companies.get(0)));
            } else {
                // 有两个公司的话转数翻100倍 最高能到达 1200
                companies.forEach(item -> costList.add(new PositionCost(1200, item)));
            }
        }
        // 火车站价值计算
        if (CollUtil.isNotEmpty(stations)) {
            stations.forEach(item -> costList.add(new PositionCost(CalcUtil.calcPositionToll(item), item)));
        }

        if (CollUtil.isNotEmpty(cities)) {
            cities.forEach(item -> costList.add(new PositionCost(CalcUtil.calcPositionToll(item), item)));
        }
        // =============点位价值计算完毕=======================
        //==============按照 点位价值排序======================
        costList.sort(Comparator.comparing(PositionCost::getCost));
        List<Integer> salePositionList = new ArrayList<>();
        for (PositionCost positionCost : costList) {
            PositionDto positionDto = positionCost.getPositionDto();
            // 点位售卖价格
            Integer salePrice = CalcUtil.calcPositionSalePrice(positionDto);
            debt -= salePrice;
            salePositionList.add(positionDto.getPosition());
            if (debt < 0 ) {
                break;
            }

        }
        return salePositionList;
    }

    /**
     * 投掷骰子获取点数
     * @return
     */
    @Override
    public Integer diceRoll() {
        return RandomUtil.randomInt(1, 12);
    }

    @Override
    public void gameOver(String msg) {
        super.gameOver(msg);
    }

    @Override
    public boolean whetherToBuy(Integer money) {
        // 给AI留2000块备用
        return playerNode.getCash() - money >= 2000;
    }

    /**
     * 人机被机会卡 房产最多的玩家免费拆一栋选中的逻辑
     * v1 :
     *     按照地皮的过路费正序排列，过路费最少的房产拆除
     *     todo 这里需要由金盏花来编写AI逻辑
     *     根据房产拆除房子之后减少的价值，
     * @return
     */
    @Override
    public Integer getPullDownBuilding() {
        List<CityDto> cities = playerNode.getCities();
        // 有房子的建筑
        List<CityDto> hasBuildingCities = cities.stream()
                .filter(item -> item.getLevel() > 0)
                .collect(Collectors.toList());
        hasBuildingCities.sort((prev, next) -> {
            Integer prevToll = prev.getToll();
            Integer nextToll = next.getToll();
            // 如果过路费相同不同的话，直接返回
            if (prevToll.intValue() != nextToll.intValue()) {
                return prevToll.compareTo(nextToll);
            } else {
                // 如果过路费不同的话则按照扣减一个房子的过路费大小排雷
                Integer prevSubOneToll = prev.getToll(prev.getLevel() - 1);
                Integer nextSubOneToll = next.getToll(prev.getLevel() - 1);
                return prevSubOneToll.compareTo(nextSubOneToll);
            }
        });
        return hasBuildingCities.get(0).getPosition();
    }

    @Override
    public Integer getFreeBuildingPosition() {
        List<CityDto> cities = playerNode.getCities();
        // 有房子的建筑
        List<CityDto> allowUpgradeCities = cities.stream()
                .filter(item -> item.getLevel() < 5)
                .collect(Collectors.toList());
        allowUpgradeCities.sort((prev, next) -> {
            Integer prevToll = prev.getToll();
            Integer nextToll = next.getToll();
            // 如果过路费相同不同的话，直接返回
            if (prevToll.intValue() != nextToll.intValue()) {
                return prevToll.compareTo(nextToll);
            } else {
                // 如果过路费不同的话则按照扣减一个房子的过路费大小排雷
                Integer prevSubOneToll = prev.getToll(prev.getLevel() + 1);
                Integer nextSubOneToll = next.getToll(prev.getLevel() + 1);
                return prevSubOneToll.compareTo(nextSubOneToll);
            }
        });
        return allowUpgradeCities.get(0).getPosition();
    }

}
