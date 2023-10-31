package cn.xeblog.plugin.game.dld.model.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author eleven
 * @date 2023/10/31 8:31
 * @apiNote
 */
@Data
public class InstanceRecord {
    /**
     * 玩家id
     */
    private Integer playerId;

    /**
     * 副本id
     */
    private Integer instanceId;

    /**
     * 当前层数
     */
    private Integer currentFloor;

    /**
     * 是否完成
     */
    private Boolean completeFlag;

    private Boolean successFlag;
}
