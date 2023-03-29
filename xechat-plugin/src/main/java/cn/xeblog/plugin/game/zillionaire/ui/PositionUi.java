package cn.xeblog.plugin.game.zillionaire.ui;

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
        positionUi.setSize(50,70);
        positionUi.setLayout(new GridLayout(1, 7));
        // 创建label
        JLabel positionLabel = new JLabel(position.toString());
        JLabel nameLabel = new JLabel(name);
        JLabel ownerLabel = new JLabel(owner);
        JLabel levelLabel = new JLabel(level.toString());
        JLabel payLabel = new JLabel(pay.toString());
        JLabel buildMoneyLabel = new JLabel(buildMoney.toString());
        JLabel tollLabel = new JLabel(toll.toString());
        // 添加组件
        positionUi.add(nameLabel);
        positionUi.add(positionLabel);
        positionUi.add(ownerLabel);
        positionUi.add(levelLabel);
        positionUi.add(payLabel);
        positionUi.add(buildMoneyLabel);
        positionUi.add(tollLabel);
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
        positionUi.setSize(40,50);
        positionUi.setLayout(new GridLayout(1, 2));
        // 创建组件
        JLabel positionLabel = new JLabel(position.toString());
        JLabel nameLabel = new JLabel(name);
        // 添加组件
        positionUi.add(positionLabel);
        positionUi.add(nameLabel);
        // 设置参数
        positionUi.setPosition(position);
        positionUi.setName(name);
    }

}
