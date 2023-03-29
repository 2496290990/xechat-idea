package cn.xeblog.plugin.game.zillionaire.ui;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author eleven
 * @date 2023/3/29 9:41
 * @description
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PositionUi extends JPanel {
    /** 名称 */
    private String name;
    /** 拥有者 */
    private String owner;
    /** 当前等级 */
    private Integer level;
    /** 购买费用 */
    private Integer pay;
    /** 建造费用 */
    private Integer buildMoney;
    /** 过路费 */
    private Integer toll;

    private Integer position;

    public PositionUi(Integer position, String name, String owner, Integer level, Integer pay, Integer buildMoney, Integer toll, Color color) {
        PositionUi positionUi = this;
        positionUi.setBorder(new LineBorder(color, 1));
        positionUi.setPreferredSize(new Dimension(40,40));
        //StringBuffer sb = new StringBuffer();
        //sb.append("<html>")
        //        .append("坐标:").append(position).append("<br/>")
        //        .append("名称:").append(name).append("<br/>")
        //        .append("拥有者:").append(StrUtil.isBlank(owner) ? "--" : owner).append("<br/>")
        //        .append("等级:").append(level).append("<br/>")
        //        .append("售价:").append(pay).append("<br/>")
        //        .append("升级:").append(buildMoney).append("<br/>")
        //        .append("过路费:").append(toll)
        //   .append("</html>");
        // 创建label
        JLabel positionLabel = new JLabel(name);
        // 添加组件
        positionUi.add(positionLabel);
        // 设置参数
        positionUi.setPosition(position);
        positionUi.setName(name);
        positionUi.setOwner(owner);
        positionUi.setLevel(level);
        positionUi.setPay(pay);
        positionUi.setBuildMoney(buildMoney);
        positionUi.setToll(toll);
    }

    public PositionUi(Integer position, String name, Color color) {
        PositionUi positionUi = this;
        positionUi.setBorder(new LineBorder(color, 1));
        //StringBuffer sb = new StringBuffer();
        //sb.append("<html>")
        //        .append("坐标:").append(position).append("<br/>")
        //        .append("名称:").append(name)
        //   .append("</html>");
        // 创建组件
        JLabel positionLabel = new JLabel(name);
        // 添加组件
        positionUi.add(positionLabel);
        // 设置参数
        positionUi.setPosition(position);
        positionUi.setName(name);
    }

}
