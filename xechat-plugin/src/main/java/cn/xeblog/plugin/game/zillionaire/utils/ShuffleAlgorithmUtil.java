package cn.xeblog.plugin.game.zillionaire.utils;

import cn.xeblog.commons.entity.game.zillionaire.dto.LuckEntity;

import java.util.*;

/**
 * @author eleven
 * @date 2023/3/30 8:15
 * @apiNote  洗牌工具类
 */
public class ShuffleAlgorithmUtil {
    public static <T> List<T> shuffle(Object[] arr){
        Random random = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Object temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return (List<T>)Arrays.asList(arr);
    }

    // 测试通用洗牌算法
    public static void main(String[] args) {
        List<LuckEntity> chanceCards = ZillionaireUtil.chanceCards;
        chanceCards.forEach(System.out::println);
        System.out.println("===============");
        List<LuckEntity> shuffle = shuffle(chanceCards.toArray());
        shuffle.forEach(System.out::println);
    }
}
