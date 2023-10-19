package cn.xeblog.plugin.game.dld.model.dto;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import lombok.Data;

/**
 * @author eleven
 * @date 2023/10/13 8:34
 * @apiNote
 */
@Data
public class LoginDto {
    /**
     * mac地址
     */
    private String mac;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     *登录类型 0mac 1账号 2邮箱
     */
    private Integer type;

    /**
     * mac快捷登录
     * @return LoginDto
     */
    public static LoginDto macLogin() {
        LoginDto dto = new LoginDto();
        String uuid = DataCache.getCurrentUser().getUuid();

        dto.setMac(uuid);
        dto.setType(0);
        return dto;
    }

    /**
     * 账号密码登录
     * @param account       账号
     * @param password      密码
     * @return LoginDto
     */
    private static LoginDto accountLogin(String account, String password) {
        LoginDto dto = new LoginDto();
        dto.setAccount(account);
        dto.setPassword(password);
        dto.setType(1);
        return dto;
    }

    /**
     * 邮箱验证码登录
     * @param email          邮箱
     * @param verifyCode     验证码
     * @return LoginDto
     */
    private static LoginDto emailLogin(String email, String verifyCode) {
        LoginDto dto = new LoginDto();
        dto.setEmail(email);
        dto.setVerifyCode(verifyCode);
        dto.setType(2);
        return dto;
    }
}
