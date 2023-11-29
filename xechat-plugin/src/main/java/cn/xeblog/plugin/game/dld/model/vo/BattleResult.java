package cn.xeblog.plugin.game.dld.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author eleven
 * @date 2023/11/28 14:50
 * @apiNote
 */
@Data
public class BattleResult {
    /**
     * 是否成功
     */
    private Boolean success;

    private Integer currentFloor;

    private List<ProcessVo> processList;

    private String rivalMac;
}