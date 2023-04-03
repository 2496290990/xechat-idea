package cn.xeblog.plugin.game.zillionaire.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xeblog.commons.entity.game.zillionaire.dto.*;
import cn.xeblog.plugin.game.zillionaire.dto.Player;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;

import javax.swing.text.Position;
import java.util.*;

/**
 * @author eleven
 * @date 2023/3/27 14:50
 * @description
 */
public class CalcUtil {
    public static void randomChance(){
        LuckEntity luckEntity = RandomUtil.randomEle(ZillionaireUtil.chanceCards);
        System.out.println(luckEntity);
    }

    public static void randomDestinyCards() {
        LuckEntity luckEntity = RandomUtil.randomEle(ZillionaireUtil.destinyCards);
        System.out.println(luckEntity);
    }

    public static final Integer randomInt() {
        return RandomUtil.randomInt(1, 12);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(randomInt());
            randomChance();
            randomDestinyCards();
        }
    }

    /**
     * 计算用户与城市质检的距离
     * @param userList          用户列表
     * @param positionDto       城市
     * @return  List            距离最近的用户
     */
    public static List<String> calcUserDistance(List<PlayerDto> userList, PositionDto positionDto) {
        // 创建一个map用来存储距离和用户
        Map<Integer, List<String>> distanceMap = new HashMap<>(userList.size());
        // 计算用户并且将之放到map中
        for (PlayerDto player : userList) {
            // 用户与城市的距离
            Integer distance = calcDistance(player, positionDto);
            List<String> usernameList = distanceMap.get(distance);
            if (CollUtil.isEmpty(usernameList)) {
                usernameList = Collections.singletonList(player.getUsername());
            } else {
                usernameList.add(player.getUsername());
            }
        }
        // 将map的key排序拿取最小的记录
        List<Integer> distanceList = new ArrayList<>(distanceMap.keySet());
        distanceList.sort(Integer::compareTo);
        return distanceMap.get(distanceList.get(0));
    }

    /**
     * 计算用户与城市质检的位置
     * @param player        用户
     * @param position      城市位置
     * @return  Integer     距离
     */
    private static Integer calcDistance(PlayerDto player, PositionDto position) {
        // 玩家位置
        Integer userPosition = player.getPosition();
        // 城市位置
        Integer cityPosition = position.getPosition();
        // 超过20 的话就
        if (cityPosition > 20) {
            return Math.min(Math.abs(cityPosition - userPosition), Math.abs(40 + userPosition - cityPosition));
        } else {
            return Math.min(Math.abs(cityPosition - userPosition), Math.abs(40 + cityPosition - userPosition));
        }
    }

    public static Integer calcPositionToll(PositionDto position) {
        if (position instanceof CityDto) {
            return ((CityDto) position).getToll();
        }

        if (position instanceof StationDto) {
            return ((StationDto) position).getToll();
        }

        return 0;
    }

    public static Integer calcPositionLevel(PositionDto position) {
        if (position instanceof CityDto) {
            return ((CityDto) position).getLevel();
        }

        if (position instanceof StationDto) {
            return ((StationDto) position).getLevel();
        }

        return 0;
    }

    /**
     * 计算点位的价格
     * @param position  点位地皮
     * @return  Integer
     */
    public static Integer calcPositionPrice(PositionDto position) {
        if (position instanceof CityDto) {
            return ((CityDto) position).getPrice();
        }

        if (position instanceof StationDto) {
            return ((StationDto) position).getPrice();
        }

        if (position instanceof CompanyDto) {
            return ((CompanyDto) position).getPrice();
        }
        return 0;
    }

    /**
     * 玩家购买地皮
     * @param player        玩家
     * @param position      地皮
     * @return  player      变更后的玩家
     */
    public static Player buyPosition(Player player, PositionDto position) {
        PlayerNode playerNode = player.getPlayerNode();
        // 重置地皮集合
        List<PositionDto> positions = playerNode.getPositions();
        positions.add(position);
        playerNode.setPositions(positions);

        if (position instanceof CityDto) {
            List<CityDto> cities = playerNode.getCities();
            cities.add((CityDto) position);
            playerNode.setCities(cities);
        }

        if (position instanceof StationDto) {
            List<StationDto> stations = playerNode.getStations();
            stations.add((StationDto)position);
            playerNode.setStations(stations);
        }

        if (position instanceof CompanyDto) {
            List<CompanyDto> companies = playerNode.getCompanies();
            companies.add((CompanyDto)position);
            playerNode.setCompanies(companies);
        }
        player.setPlayerNode(playerNode);
        return player;
    }

    public static List<PositionDto> getUserPositionRefreshList(PlayerNode playerNode) {
        int index = 0;
        List<CompanyDto> companies = playerNode.getCompanies();
        List<StationDto> stations = playerNode.getStations();
        List<CityDto> cities = playerNode.getCities();
        int size = companies.size() + stations.size() + cities.size();
        if (size > 0) {
            PositionDto[] positions = new PositionDto[size];
            for (CompanyDto company : companies) {
                positions[index] = company;
                index++;
            }
            for (StationDto station : stations) {
                positions[index] = station;
                index++;
            }
            for (CityDto city : cities) {
                positions[index] = city;
                index++;
            }
            return Arrays.asList(positions);
        }
        return null;
    }
}
