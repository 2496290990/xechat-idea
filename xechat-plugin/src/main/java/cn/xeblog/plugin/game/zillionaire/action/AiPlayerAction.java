package cn.xeblog.plugin.game.zillionaire.action;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xeblog.commons.entity.game.zillionaire.dto.CityDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.CompanyDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.StationDto;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;

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

    @Override
    public void saleBuild(Integer toll) {
        // 获取差额
        PlayerNode playerNode = getPlayerNode();
        int debt = toll - playerNode.getCash();
        List<CityDto> cities = playerNode.getCities();
        List<StationDto> stations = playerNode.getStations();
        List<CompanyDto> companies = playerNode.getCompanies();
        List<CityDto> zeroCities = cities.stream()
                .filter(item -> item.getLevel() == 0)
                .collect(Collectors.toList());
        Map<Integer, List<Integer>> costPositionMap = new HashMap<>(23);
        List<Integer> positions = null;
        // 如果公司不为空
        if (CollUtil.isNotEmpty(companies)) {
            if (companies.size() == 1) {
                CompanyDto companyDto = companies.get(0);
                // 如果只有一个公司 电力或者水利公司的话，每次最高也只能赚 120
                Integer position = companyDto.getPosition();
                positions = Collections.singletonList(position);
                costPositionMap.put(120, positions);
            } else {
                // 有两个公司的话转数翻100倍 最高能到达 1200
                positions = new ArrayList<>(2);
                for (CompanyDto company : companies) {
                    Integer position = company.getPosition();
                    positions.add(position);
                }
                costPositionMap.put(1200, positions);
            }
        }
        // 火车站价值计算
        if (CollUtil.isNotEmpty(stations)) {
            positions = new ArrayList<>(stations.size());
            for (StationDto station : stations) {
                positions.add(station.getPosition());
            }
            costPositionMap.put(stations.get(0).getToll(), positions);
        }

        if (CollUtil.isNotEmpty(cities)) {

        }
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
}
