package cn.xeblog.plugin.game.zillionaire;

import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.zillionaire.dto.PositionDto;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;

import java.awt.*;
import java.util.List;


/**
 * @author eleven
 * @date 2023/3/20 11:20
 * @description 大富翁
 */
@DoGame(Game.ZILLIONAIRE)
public class Zillionaire extends AbstractGame<PositionDto>{
    private List<String> userList;
    @Override
    protected void start() {
        GameRoom gameRoom = getRoom();
        if (gameRoom != null) {
            userList.addAll(gameRoom.getUsers().keySet());
        } else {
            userList.add(GameAction.getNickname());
        }
    }

    @Override
    protected void init() {
        initStartPanel();
    }

    private void initStartPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(400, 400));


    }

    @Override
    protected void allPlayersGameStarted() {
        if (isHomeowner()) {

        }
    }
}
