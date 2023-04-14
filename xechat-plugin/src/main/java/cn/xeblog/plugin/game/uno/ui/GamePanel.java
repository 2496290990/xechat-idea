package cn.xeblog.plugin.game.uno.ui;

import cn.xeblog.plugin.game.uno.entity.Player;
import lombok.Getter;

import javax.swing.*;
import java.util.Map;

/**
 * @author eleven
 * @date 2023/4/14 13:55
 * @apiNote
 */
@Getter
public class GamePanel {
    /**
     * 提示标签
     */
    private JLabel tipsLabel;
    /**
     * 游戏板
     */
    private JPanel gamePanel;
    /**
     * 中心面板
     */
    private JPanel centerPanel;
    /**
     * 用户面板
     */
    private JPanel usersPanel;
    /**
     * 团队卡板
     */
    private JPanel teamCardsPanel;
    /**
     * 从卡板
     */
    private JPanel outCardsPanel;
    /**
     * 团队滚动
     */
    private JScrollPane teamScroll;
    /**
     * 从滚动
     */
    private JScrollPane outScroll;
    /**
     * 卡板
     */
    private JPanel cardsPanel;
    /**
     * 卡片上滚动
     */
    private JScrollPane cardsScroll;
    /**
     * 底btn面板
     */
    private JPanel bottomBtnPanel;
    /**
     * alloc btn
     */
    private JButton allocBtn;
    /**
     * 出btn
     */
    private JButton outBtn;
    /**
     * 提示btn
     */
    private JButton tipsBtn;
    /**
     * btn面板
     */
    private JPanel btnPanel;
    /**
     * 页脚面板
     */
    private JPanel footerPanel;

    public static final GamePanel GAME_PANEL = new GamePanel();

    private GamePanel(){}

    public static GamePanel instance(){
        return GAME_PANEL;
    }

    public JPanel getGamePanel(Map<String, Player> playerMap) {
        usersPanel.removeAll();
        playerMap.forEach((k, v) -> {
            usersPanel.add(
                new UserPanel()
                        .getUserPanel(v.getPlayerNode(), e -> {
                        })
            );
        });
        gamePanel.add(tipsLabel);
        gamePanel.add(usersPanel);
        return gamePanel;
    }

    public void refreshBtn(JButton btn, Boolean enable){
        btn.setEnabled(enable);
    }

}
