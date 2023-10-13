package cn.xeblog.plugin.game.dld.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.common.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author eleven
 * @date 2023/10/13 12:26
 * @apiNote
 */
public class ResultUtil {
    public static <T> Page<T> convertPageData(Page page, Class<T> cls) {
        List records = page.getRecords();
        List<T> convertList = new ArrayList<>(records.size());
        for (Object record : records) {
            convertList.add(BeanUtil.mapToBean((Map) record, cls, true));
        }
        page.setRecords(convertList);
        return page;
    }

    public static <T> List<T> convertListData(Result dataResult, Class<T> cls) {
        List data = (List) dataResult.getData();
        return data;
    }

}
