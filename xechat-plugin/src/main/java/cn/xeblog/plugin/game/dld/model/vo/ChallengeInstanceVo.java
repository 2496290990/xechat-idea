package cn.xeblog.plugin.game.dld.model.vo;

import cn.xeblog.plugin.game.dld.model.entity.InstanceNpc;
import cn.xeblog.plugin.game.dld.model.entity.InstanceRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author eleven
 * @date 2023/10/31 9:37
 * @apiNote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeInstanceVo {

    private InstanceRecord record;

    private List<InstanceNpc> npcList;
}
