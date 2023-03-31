package cn.xeblog.plugin.game.zillionaire.dto;

import cn.hutool.core.collection.CollUtil;
import cn.xeblog.commons.entity.game.zillionaire.dto.CityDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.CompanyDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.commons.entity.game.zillionaire.dto.StationDto;
import javafx.geometry.Pos;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author eleven
 * @date 2023/3/29 15:00
 * @description
 */
@Data
@ToString(exclude = {"prevPlayer", "nextPlayer"})
public class PlayerNode {
    /**
     * 当前玩家
     */
    private String player;

    /**
     * 玩家别名
     */
    private String alias;
    /**
     * 状态
     */
    private Boolean status;
    /**
     * 位置
     */
    private Integer position;
    /**
     * 现金
     */
    private Integer cash;

    /**
     * 财产
     */
    private Integer property;

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

    private List<PositionDto> positions;


    /**
     * 前一位玩家
     */
    private PlayerNode prevPlayer;

    /**
     * 后一位玩家
     */
    private PlayerNode nextPlayer;

    public PlayerNode() {
        int initMoney = 2 * (5000 + 2000 + 1000 + 500 + 200 + 100 + 50 + 10);
        this.cash = initMoney;
        this.property = initMoney;
        this.position = 0;
        this.status = true;
        this.cities = new ArrayList<>();
        this.companies = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.positions = new ArrayList<>();
    }

    public PlayerNode(String player) {
        this.player = player;
        int initMoney = 2 * (5000 + 2000 + 1000 + 500 + 200 + 100 + 50 + 10);
        this.cash = initMoney;
        this.property = initMoney;
        this.position = 0;
        this.status = true;
        this.cities = new ArrayList<>();
        this.companies = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.positions = new ArrayList<>();
    }

    public Integer getPosition() {
        return position % 40;
    }

}