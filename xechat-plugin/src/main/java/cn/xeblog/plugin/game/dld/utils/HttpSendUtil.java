package cn.xeblog.plugin.game.dld.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.dld.Const;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.common.Page;
import cn.xeblog.plugin.util.NotifyUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author eleven
 * @date 2023/10/13 9:08
 * @apiNote
 */
@Slf4j
public class HttpSendUtil {
    private static Gson gson = new Gson();
    public static Result post(String url, Object data) {
        String dataJson = gson.toJson(data);
        String body = HttpRequest.post(url)
                .header("satoken", DataCache.loginToken)
                .body(dataJson)
                .execute()
                .body();
        log.info("\n当前接口请求 - {}\n, 当前参数 {}\n,返回结果: {}", url, dataJson, body);
        Result result = gson.fromJson(body, Result.class);
        if (Const.ERROR_CODE == result.getCode()) {
            NotifyUtils.error(Const.GAME_NAME, result.toString(), true);
        }
        return result;
    }

    public static <T> T postResult(String url, Object data, Class<T> cls) {
        Result post = post(url, data);
        String dataJson = gson.toJson(post.getData());
        log.info("当前返回的实体类 json字符串为 {}", dataJson);
        return gson.fromJson(dataJson, cls);
    }

}
