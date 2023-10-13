package cn.xeblog.plugin.game.dld.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/10/13 10:35
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerVo {
    /**
     * id
     */
    private Integer id;
    /**
     * 账号id
     */
    private Integer accountId;
    /**
     * 网卡地址
     */
    private String mac;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 省份
     */
    private String region;

}
