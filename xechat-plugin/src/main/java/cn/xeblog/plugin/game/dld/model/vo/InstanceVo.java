package cn.xeblog.plugin.game.dld.model.vo;

import lombok.Data;

/**
 * @author eleven
 * @date 2023/10/27 15:43
 * @apiNote
 */
@Data
public class InstanceVo {

    private Integer id;
    /**
     * 副本名称
     */
    private String instanceName;

    /**
     * 副本描述
     */
    private String instanceInfo;

    /**
     * 层数
     */
    private Integer floorNum;

    /**
     * 准入等级
     */
    private Integer accessLevel;
}
