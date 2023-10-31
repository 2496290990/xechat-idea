package cn.xeblog.plugin.game.dld.model.entity;

import lombok.Data;

import java.util.List;

/**
 * @author eleven
 * @date 2023/10/19 16:06
 * @apiNote
 */
@Data
public class InstanceNpc {
    private Integer instanceId;

    private String npcName;

    private Integer playerId;

    private Boolean bossFlag;

    private Double increaseRatio;

    private Integer floor;

    private String dropIds;

    private List<String> dropIdList;

    //public List<String> getDropIdList() {
    //    if (StrUtil.isNotBlank(dropIds)) {
    //        return Arrays.asList(dropIds.split(","));
    //    }
    //    return new ArrayList<>();
    //}
}
