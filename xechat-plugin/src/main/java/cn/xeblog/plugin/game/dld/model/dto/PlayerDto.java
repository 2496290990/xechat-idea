package cn.xeblog.plugin.game.dld.model.dto;

import cn.xeblog.plugin.game.dld.model.common.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eleven
 * @date 2023/10/13 10:24
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private Page page;

    private Integer accountId;

    private String nickname;

    /**
     * 网卡地址
     */
    private String mac;

    public PlayerDto(String mac) {
        this.mac = mac;
    }

    public PlayerDto(Page page) {
        this.page = page;
    }
}
