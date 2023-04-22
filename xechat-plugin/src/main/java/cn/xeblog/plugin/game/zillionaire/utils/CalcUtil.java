package cn.xeblog.plugin.game.zillionaire.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xeblog.commons.entity.game.zillionaire.dto.*;
import cn.xeblog.plugin.game.zillionaire.dto.Player;
import cn.xeblog.plugin.game.zillionaire.dto.PlayerNode;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
     * 计算用户与城市质检的位置
     * @param player        用户
     * @param position      城市位置
     * @return  Integer     距离
     */
    private static Integer calcDistance(PlayerNode player, PositionDto position) {
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
            // 车站站点的话返回玩家有多少个车站
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

    public static Integer calcPositionSalePrice(PositionDto position) {
        if (position instanceof CityDto) {
            CityDto city = (CityDto) position;
            return calcPositionPrice(position) + city.getBuildMoney() * city.getLevel();
        } else {
            return calcPositionPrice(position);
        }
    }

    /**
     * 获取升级建筑费用
     * @param position
     * @return
     */
    public static Integer calcPositionUpgrade(PositionDto position) {
        if (position instanceof CityDto) {
            return ((CityDto) position).getBuildMoney();
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

        if (position instanceof CityDto) {
            List<CityDto> cities = playerNode.getCities();
            cities.add((CityDto) position);
            playerNode.setCities(cities);
        }

        if (position instanceof StationDto) {
            List<StationDto> stations = playerNode.getStations();
            stations.add((StationDto)position);
            // 给车站升级
            stations.forEach(item -> item.setLevel(stations.size()));
            playerNode.setStations(stations);
            positions.forEach(item -> {
                if (item instanceof StationDto) {
                    ((StationDto) item).setLevel(stations.size());
                }
            });
        }

        if (position instanceof CompanyDto) {
            List<CompanyDto> companies = playerNode.getCompanies();
            companies.add((CompanyDto)position);
            playerNode.setCompanies(companies);
        }
        playerNode.setPositions(positions);
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

    /**
     * 获取现金最多的玩家
     * @param playerMap     玩家集合
     * @return
     */
    public static List<Player> getMaxCashPlayer(Map<String, Player> playerMap) {
        Map<Integer, List<Player>> cashPlayerMap = new HashMap<>();
        for (String playerName : playerMap.keySet()) {
            Player player = playerMap.get(playerName);
            Integer cash = player.getPlayerNode().getCash();
            addToMap(cashPlayerMap, cash, player);
        }
        //  将所有玩家的房间数正序排列
        List<Integer> cashList = new ArrayList<>(cashPlayerMap.keySet());
        // 按照数量正序排列
        cashList.sort(Integer::compareTo);
        return cashPlayerMap.get(cashList.get(cashList.size() - 1));
    }

    /**
     * 获取房屋最少的玩家
     * @param playerMap 玩家集合
     * @param minBuilding true 获取建筑最少的玩家 false获取建筑最多的玩家
     * @return
     */
    public static List<Player> getPlayerBuildings(Map<String, Player> playerMap, Boolean minBuilding) {
        Map<Integer, List<Player>> buildingPlayerMap = new HashMap<>();
        for (String playerName : playerMap.keySet()) {
            Player player = playerMap.get(playerName);
            PlayerNode playerNode = player.getPlayerNode();
            List<CityDto> cities = playerNode.getCities();
            int buildingNum = 0;
            if (CollUtil.isNotEmpty(cities)) {
                for (CityDto city : cities) {
                    buildingNum += city.getLevel();
                }
            }
            addToMap(buildingPlayerMap, buildingNum, player);
        }
        //  将所有玩家的房间数正序排列
        List<Integer> buildingList = new ArrayList<>(buildingPlayerMap.keySet());
        // 按照数量正序排列
        buildingList.sort(Integer::compareTo);
        int index = minBuilding ? 0 : buildingList.size() - 1;
        return buildingPlayerMap.get(buildingList.get(index));
    }

    /**
     * 获取具体指定坐标点最近的玩家集合
     * @param playerMap 玩家集合
     * @param position  地址
     * @return  List    玩家集合
     */
    public static List<Player> getMinDistancePlayer(Map<String, Player> playerMap, PositionDto position) {
        Map<Integer, List<Player>> distancePlayerMap = new HashMap<>();
        for (String playerName : playerMap.keySet()) {
            Player player = playerMap.get(playerName);
            Integer distance = calcDistance(player.getPlayerNode(), position);
            addToMap(distancePlayerMap, distance, player);
        }
        List<Integer> distanceList = new ArrayList<>(distancePlayerMap.keySet());
        distanceList.sort(Integer::compareTo);
        return distancePlayerMap.get(distanceList.get(0));
    }

    /**
     * 添加到map中
     * @param map           存储数据的map
     * @param key           map的键
     * @param player        玩家
     */
    private static void addToMap(Map<Integer, List<Player>> map, Integer key,Player player) {
        List<Player> players = map.get(key);
        if (CollUtil.isEmpty(players)) {
            players = new ArrayList<>();
        }
        players.add(player);
        map.put(key, players);
    }

    /**
     * 是否允许升级
     * @param playerNode    玩家节点
     * @param position      点位数据
     * @return
     */
    public static boolean allowUpgrade(PlayerNode playerNode, PositionDto position) {
        // 点位自身是否允许升级
        Boolean upgradeAllowed = position.getUpgradeAllowed();
        // 点位自身不允许升级
        if (!upgradeAllowed) {
            return false;
        }
        Optional<CityDto> first = playerNode.getCities()
                .stream()
                .filter(item -> item.getPosition().equals(position.getPosition()))
                .findFirst();
        // 查找不到玩家有当前房产不允许升级
        if (first.isEmpty()) {
            return false;
        }
        // 当前房屋五级满级不允许升级
        CityDto city = first.get();
        if (city.getLevel() == 5) {
            return false;
        }
        return true;
    }

    /**
     * 获取有建筑的房子
     * @param cities
     * @return
     */
    public static List<CityDto> getHasBuildingCities(List<CityDto> cities) {
        return cities.stream()
                .filter(item -> item.getLevel() != 0)
                .collect(Collectors.toList());
    }

    /**
     * 当前地皮的拥有者是否有全部颜色的地皮 并且当前的地皮是
     *
     * @param playerNode 地皮拥有者
     * @param position   地皮
     * @return Boolean          是否拥有全部颜色的地皮
     */
    public static Boolean isDouble(PlayerNode playerNode, CityDto position, Map<Integer, PositionDto> positionMap) {
        List<CityDto> cities = playerNode.getCities();
        // 获取当前位置的颜色
        Color color = position.getColor();
        // 获取所有的地皮
        Collection<PositionDto> values = positionMap.values();
        // 获取初始化中房屋的颜色
        long colorInitCount = values.stream()
                .filter(item -> item.getColor().equals(color))
                .count();

        long userColorCount = cities.stream()
                .filter(item -> item.getColor().equals(color))
                .count();
        boolean hasAll = colorInitCount == userColorCount;
        boolean zero = position.getLevel() == 0;
        return hasAll && zero;
    }
}
