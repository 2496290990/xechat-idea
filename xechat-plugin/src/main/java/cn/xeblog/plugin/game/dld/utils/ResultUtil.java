package cn.xeblog.plugin.game.dld.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.common.Page;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author eleven
 * @date 2023/10/13 12:26
 * @apiNote
 */
@Slf4j
public class ResultUtil {
    private static Gson gson = new Gson();

    public static <T> Page<T> convertPageData(Page page, Class<T> cls) {
        List<Map> records = page.getRecords();
        log.info("当前的records {}", records);
        List<T> convertList = new ArrayList<>(records.size());
        for (Map record : records) {
            convertList.add(BeanUtil.mapToBean(record, cls, true));
        }
        log.info("最终的records {}", convertList);
        page.setRecords(convertList);
        return page;
    }

    public static <T> List<T> convertListData(Result dataResult, Class<T> cls) {
        List<Map> data = (List) dataResult.getData();
        List<T> result = new ArrayList<>(data.size());
        for (Map item : data) {
            result.add(BeanUtil.mapToBean(item, cls, true));
        }
        return result;
    }

    public static <T> T convertObjData(Result result, Class<T> cls) {
        return convertObjData(result.getData(), cls);
    }

    public static <T> T convertObjData(Object data, Class<T> cls) {
        return gson.fromJson(gson.toJson(data), cls);
    }

    public static <T> Page<T> convertPageData(Object data, Class<T> cls) {
        return convertPageData(convertObjData(data, Page.class), cls);
    }

}
