package cn.xeblog.plugin.game.dld;

/**
 * @author eleven
 * @date 2023/10/12 15:54
 * @apiNote
 */
public class Const {
    /**
     * 基础url
     */
    //public static final String BASE_URL = "http://103.153.101.174:9528/dld";
    public static final String BASE_URL = "http://localhost:9528/dld";
    /**
     * 系统控制器
     */
    public static final String SYS_CONTROLLER = BASE_URL + "/sys";
    /**
     * 玩家控制器
     */
    public static final String PLAYER_CONTROLLER = BASE_URL + "/player";
    /**
     * 战斗控制器
     */
    public static final String BATTLE_CONTROLLER = BASE_URL + "/battle";
    /**
     * 登录
     */
    public static final String SYS_LOGIN = SYS_CONTROLLER + "/login";
    /**
     * 注册接口
     */
    public static final String SYS_REG = SYS_CONTROLLER + "/register";
    /**
     * 获取所有玩家
     */
    public static final String PLAYER_GET_ALL = PLAYER_CONTROLLER + "/getAll";
    /**
     * 战斗
     */
    public static final String BATTLE_DO = BATTLE_CONTROLLER + "/doBattle";




}
