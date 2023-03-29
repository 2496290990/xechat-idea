package cn.xeblog.plugin.game.zillionaire;

import cn.xeblog.commons.entity.game.zillionaire.dto.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author eleven
 * @date 2023/3/27 14:25
 * @description 工具类
 */
public class ZillionaireUtil {
    /**
     * 获取命运卡牌
     */
    public static final List<LuckEntity> destinyCards = initDestinyCard();

    /**
     * 机会卡牌
     */
    public static final List<LuckEntity> chanceCards = initDestinyCard();
    /**
     * 位置卡牌
     */
    public static final List<PositionDto> positionDtoList = initPosition();
    /**
     * 初始化定位卡牌
     * @return List
     */
    private static List<PositionDto> initPosition(){
        PositionDto[] positionArr = new PositionDto[40];
        // 起点 坐标 0
        PositionDto start = new PositionDto(0, false, "起点", Color.BLACK, null, false, 0);
        positionArr[0] = start;
        // 美国 坐标 1
        CityDto america = new CityDto(0, 3500, 350, 1750, 5000, 11000, 13000, 15000, null, 2000 );
        america.superConstructor(1, "美国", Color.PINK);
        positionArr[1] = america;
        // 命运 坐标 2
        LuckDto firstDestiny = new LuckDto();
        firstDestiny.superConstructor(2, "命运", Color.BLACK);
        positionArr[2] = firstDestiny;
        // 加拿大 坐标 3
        CityDto canada = new CityDto(0, 3500, 350, 1750, 5000, 11000, 13000, 15000, null, 2000 );
        canada.superConstructor(3, "加拿大", Color.PINK);
        positionArr[3] = canada;
        // 所得税 坐标 4
        PositionDto incomeTax = new PositionDto(4, false, "所得税", Color.BLACK, null, false , 4);
        positionArr[4] = incomeTax;
        // 纽约火车站 坐标 5
        StationDto newYorkStation = new StationDto();
        newYorkStation.superConstructor(5, "纽约火车站");
        positionArr[5] = newYorkStation;
        // 阿根廷 坐标 6
        CityDto argentina = new CityDto(0, 1000, 60, 300, 900, 2700, 4000, 5500, null, 500 );
        argentina.superConstructor(6, "阿根廷", Color.YELLOW);
        positionArr[6] = argentina;
        // 机会 坐标 7
        LuckDto firstChance = new LuckDto();
        firstChance.superConstructor(7, "机会", Color.BLACK);
        positionArr[7] = firstChance;
        // 墨西哥 坐标 8
        CityDto mexico = new CityDto(0, 1000, 60, 300, 900, 2700, 4000, 5500, null, 500 );
        mexico.superConstructor(8, "墨西哥", Color.YELLOW);
        positionArr[8] = mexico;
        // 古巴 坐标 9
        CityDto cubo = new CityDto(0, 3000, 260, 1300, 3900, 9000, 11000, 12750, null, 2000 );
        cubo.superConstructor(9, "古巴", Color.YELLOW);
        positionArr[9] = cubo;
        // 监狱 坐标 10
        PositionDto jail = new PositionDto(10, false, "监狱", Color.BLACK, null, false, 1);
        positionArr[10] = jail;
        // 法国 坐标 11
        CityDto france = new CityDto(0, 3000, 260, 1300, 3900, 9000, 11000, 12750, null, 2000 );
        france.superConstructor(11, "法国", Color.CYAN);
        positionArr[11] = france;
        // 电力公司 坐标 12
        CompanyDto electricPowerCompany = new CompanyDto();
        electricPowerCompany.superConstructor(12, "电力公司");
        positionArr[12] = electricPowerCompany;
        // 德国 坐标 13
        CityDto germany = new CityDto(0, 2400, 200, 1000, 3000, 7500, 9250, 11000, null, 1500 );
        germany.superConstructor(13, "德国", Color.CYAN);
        positionArr[13] = germany;
        // 意大利 坐标 14
        CityDto italy = new CityDto(0, 1400, 100, 500, 1500, 4500, 6250, 7500, null, 1000 );
        italy.superConstructor(14, "意大利", Color.CYAN);
        positionArr[14] = italy;
        // 巴黎火车站 坐标 15
        StationDto parisStation = new StationDto();
        parisStation.superConstructor(15, "巴黎火车站");
        positionArr[15] = parisStation;
        // 西班牙 坐标 16
        CityDto spain = new CityDto(0, 1400, 100, 500, 1500, 4500, 6250, 7500, null, 1000 );
        spain.superConstructor(16, "西班牙", Color.ORANGE);
        positionArr[16] = spain;
        // 命运 坐标 17
        LuckDto secondDestiny = new LuckDto();
        secondDestiny.superConstructor(17, "命运", Color.BLACK);
        positionArr[17] = secondDestiny;
        // 希腊 坐标 18
        CityDto greece = new CityDto(0, 1600, 120, 600, 1800, 5000, 7000, 9000, null, 1000 );
        greece.superConstructor(18, "希腊", Color.ORANGE);
        positionArr[18] = greece;
        // 荷兰 坐标 19
        CityDto netherlands = new CityDto(0, 2400,  200, 1000, 3000, 7500, 9250, 11000, null, 1500 );
        netherlands.superConstructor(19, "荷兰", Color.ORANGE);
        positionArr[19] = netherlands;
        // 免费停车场 坐标 20
        PositionDto park = new PositionDto(20, false, "停车场", Color.BLACK, null, false, 20);
        positionArr[20] = park;
        // 英国 坐标 21
        CityDto england = new CityDto(0, 2200,  180, 900, 2500, 7000, 8750, 10500, null, 1500 );
        england.superConstructor(21, "英国", Color.ORANGE);
        positionArr[21] = england;
        // 机会 坐标 22
        LuckDto secondChance = new LuckDto();
        secondChance.superConstructor(22, "机会", Color.BLACK);
        positionArr[22] = secondChance;
        // 俄罗斯 坐标 23
        CityDto russia = new CityDto(0, 1000, 60, 300, 900, 2700, 4000, 5500, null, 500 );
        russia.superConstructor(23, "俄罗斯", Color.ORANGE);
        positionArr[23] = russia;
        // 泰国 坐标 24
        CityDto thailand = new CityDto(0, 1800, 140, 700, 2000, 5500, 7500, 9500, null, 1000 );
        thailand.superConstructor(24, "泰国", Color.ORANGE);
        positionArr[24] = thailand;
        // 东京火车站 坐标 25
        StationDto tokyoStation = new StationDto();
        tokyoStation.superConstructor(25, "东京火车站");
        positionArr[25] = tokyoStation;
        // 土耳其 坐标 26
        CityDto turkey = new CityDto(0, 3500, 350, 1750, 5000, 11000, 13000, 15000, null, 2000 );
        turkey.superConstructor(26, "土耳其", Color.BLUE);
        positionArr[26] = turkey;
        // 澳大利亚 坐标 27
        CityDto australia = new CityDto(0, 3200, 280, 1500, 4500, 10000, 12000, 14000, null, 2000 );
        australia.superConstructor(27, "澳大利亚", Color.BLUE);
        positionArr[27] = australia;
        // 自来水公司 坐标 28
        CompanyDto water = new CompanyDto();
        water.superConstructor(28, "自来水公司");
        positionArr[28] = water;
        // 新加坡 坐标 29
        CityDto singapore = new CityDto(0, 3000, 260, 1300, 3900, 9000, 11000, 12750, null, 2000 );
        singapore.superConstructor(29, "新加坡", Color.BLUE);
        positionArr[29] = singapore;
        // 坐牢 坐标 30
        PositionDto toJail = new PositionDto(30, false, "停车场", Color.BLACK, null, false,  30);
        positionArr[30] = toJail;
        // 韩国 坐标 31
        CityDto korea = new CityDto(0, 1000, 60, 300, 900, 2700, 4000, 5500, null, 500 );
        korea.superConstructor(31, "韩国", Color.WHITE);
        positionArr[31] = korea;
        // 中国 坐标 32
        CityDto china = new CityDto(0, 4000,500, 2000, 6000, 14000, 17000, 20000, null, 2000 );
        china.superConstructor(32, "中国", Color.WHITE);
        positionArr[32] = china;
        // 命运 坐标 33
        LuckDto thirdDestiny = new LuckDto();
        thirdDestiny.superConstructor(33, "命运", Color.BLACK);
        positionArr[33] = thirdDestiny;
        // 中国香港 坐标 34
        CityDto chinaHK = new CityDto(0, 2800, 220, 1200, 3600, 8500, 10250, 12000, null, 1500 );
        chinaHK.superConstructor(34, "中国香港", Color.WHITE);
        positionArr[34] = chinaHK;
        // 北京火车站 坐标 35
        StationDto beiJingStation = new StationDto();
        beiJingStation.superConstructor(35, "北京火车站");
        positionArr[35] = beiJingStation;
        // 机会 坐标 36
        LuckDto thirdChance = new LuckDto();
        thirdChance.superConstructor(36, "机会", Color.BLACK);
        positionArr[36] = thirdChance;
        // 日本 坐标 37
        CityDto japan = new CityDto(0, 1000, 60, 300, 900, 2700, 4000, 5500, null, 500 );
        japan.superConstructor(37, "日本", Color.GREEN);
        positionArr[37] = japan;
        // 财产税 38
        PositionDto propertyTax = new PositionDto(38, false, "财产税", Color.BLACK, null, false, 38);
        positionArr[38] = propertyTax;
        // 巴西 坐标 39
        CityDto brazil = new CityDto(0, 1800, 140, 700, 2000, 5500, 7500, 9500, null, 1000 );
        brazil.superConstructor(39, "巴西", Color.GREEN);
        positionArr[39] = brazil;
        return Arrays.asList(positionArr);
    }

    /**
     * 初始化命运卡牌
     * @return List
     */
    private static List<LuckEntity> initDestinyCard(){
        List<LuckEntity> destinyCards = new ArrayList<>();
        Collections.addAll(destinyCards,
                new LuckEntity(0,"搭乘'欧洲之星'从巴黎直达伦敦","直达伦敦(若经过起点可领2000)",
                        "欧洲之星与1994年11月开始运营后，成为伦敦至巴黎铁路路线中最受欢迎的列车，最高时速可达300公里",0),
                new LuckEntity(1,"在南美洲种植咖啡树品质优良销量大增","免费盖一栋房子",
                        "南美洲的文化原以印第安文化为主，后来因欧洲殖民者以西班牙、葡萄牙的拉丁文化长期居主导地位，故南美洲常被称拉丁美洲。",0),
                new LuckEntity(2,"到梵蒂冈研究天主教历史","奖金1000元",
                        "梵蒂冈是全球面积最小、人口最少的国家但因为是天主教的中枢，所以在政治和文化等领域拥有着世界性的影响力。",0),
                new LuckEntity(3,"到澳洲打工渡假","奖金900元",
                        "澳洲打工渡假计划是提供给世界各国的年轻人一边打工一边渡假的机会，可进一步了解并学习澳洲文化及其人民的生活方式",0),
                new LuckEntity(4,"在泰国听到泰国国歌没有马上肃立","罚款600元",
                        "泰国人非常尊重他们的王室，泰国法律有对王室不敬罪的处罚条例。游客听到演奏泰国国歌，应马上停止活动并肃立。",0),
                new LuckEntity(5,"到南极大陆探险看到美丽的极光","花费700元",
                        "极光出现于高磁纬地区的上空，是一种绚丽多彩的发光现象。最易出现的时期是春分和秋分两个季节来临之前。",0),
                new LuckEntity(6,"在非洲阻止猎人盗猎保育类的动物们","奖金800元",
                        "热带草原占全非洲面积的1/3，是世界上面积最大的热带草原区。其中有许多濒临绝种的动物，例如汤氏瞪羚，麝猫等等。",0),
                new LuckEntity(7,"在死海抢救溺水的小朋友","奖金850元",
                        "死海，希伯来语意为“盐海”，位于约旦和以色列交界，是世界上最低的湖泊，死海的湖岸是地球上已露出陆地的最低点。",0),
                new LuckEntity(8,"阿拉伯联合大公国不小心打翻饮用水","赔偿500元",
                        "在沙漠王国是水比油贵，阿拉伯联合大公国虽有水源，饮水仍然不足，仍靠外海的地下水淡化供应。",0),
                new LuckEntity(9,"到日本京都赏樱订不到旅馆露宿街头","罚款房子每栋200元，旅馆600元",
                        "赏樱是日本皇室贵族间附庸风雅的活动，16世纪时丰臣秀吉开放庶民百姓参与，延续至今，春天赏樱已成日本的全民运动",0),
                new LuckEntity(10,"停留西班牙参加'塞维利亚春会'","休息一次",
                        "这是西班牙最重要的三庆典之一，这期间西班牙女郎穿着多层荷叶裙摆、蓬蓬袖洋装，与参加者一起跳传统的塞维利亚舞。",0),
                new LuckEntity(11,"在威尼斯街头与街头艺人一起表演","奖金600元",
                        "威尼斯是个艺术文化气息浓厚的城市，不论是在特定表演区或一般街头，都可以看到各式各样的表演。",0),
                new LuckEntity(12,"在新加坡观光时乱丢垃圾","罚款400元",
                        "新加坡有许多生活上的法律规范，而这些严谨的规范也让新加坡成为世人印象中最清洁也最守法的国家。",0),
                new LuckEntity(13,"到日本东京参观东京铁塔","给每人100元观光费",
                        "正式名称是日本电波塔，是一座是以巴黎埃菲尔铁塔为范本而建造的红白色铁塔，其高333公尺，是全世界最高的自立式铁塔。",0),
                new LuckEntity(14,"在极圈的冰岛发现失落的'冰岛萨迦'","奖金700元",
                        "冰岛最为著名的古典文学作品是“冰岛萨迦”一种人们开始定居在冰岛的时代写下的散文史诗，内容主要是英雄故事和家族传奇。",0)
                );
        return destinyCards;
    }

    /**
     * 初始化机会卡牌
     * @return  List
     */
    private static List<LuckEntity> initChanceCard(){
        List<LuckEntity> chanceCards = new ArrayList<>();
        Collections.addAll(chanceCards,
                new LuckEntity(0,"拯救了迷路的北极熊宝宝","玩家得600元",
                        "北极熊是生长在北极的熊，也是陆地上最庞大的肉食动物，它拥有极厚的脂肪及毛发保暖，所以能在北极严酷的气候里生存。",1),
                new LuckEntity(1,"到印尼品尝麝香猫咖啡","花费1200元",
                        "印尼的麝香猫咖啡是全世界最贵的咖啡，是麝香猫吃了树上的咖啡樱桃果，经过消化道之后排泄出来的剩余咖啡豆。",1),
                new LuckEntity(2,"在阿拉斯加挖到石油","获得出狱许可证",
                        "阿拉斯而早期被当成是冰封的世界，随着大量的金矿与石油被发现，成为美国的能源宝主要产原油、天然气贵金属。",1),
                new LuckEntity(3,"在加拿大遇到龙卷风车子被风吹走了","损失800元",
                        "龙卷风是一种相当猛烈的天气现象，因快速旋转而造成直立中空管状的强力气流，破坏力惊人，常造成严重的生命财产损失。",1),
                new LuckEntity(4,"搭乘日本新干线旅游","马上回到起点并领取2000元",
                        "新干线是日本的高速铁路客运专线系统，以'子弹列车'闻名，是全世界最快的列车",1),
                new LuckEntity(5,"与爱斯基摩人一起盖冰屋时因错算角度导致冰屋倒塌","房子最多的人拆一栋房子",
                        "爱斯基摩人又称为因纽特人，他们擅长搭盖1雪块做成的房屋，作为他们在漫长的严冬母中狩精期间的临时住所。",1),
                new LuckEntity(6,"登机时携带洗发精被美国海关没收","最靠近美国的玩家罚700元",
                        "为了防止恐怖事件，美国禁止旅客把胶状、膏状和液状的物品放置在随身行李中，但可以放在行李箱里托运。",1),
                new LuckEntity(7,"在印度吃牛肉干被罚","立即坐牢",
                        "牛在印度教中被视为神圣的动物，因此不得率杀牛，不得使用牛革制品，更忌食牛因",1),
                new LuckEntity(8,"在巴西参加狂欢节","大家转转盘，点数最大的人拿取点数X10的金额",
                        "巴西狂欢节被称为世界上最大的狂欢节，有“地球上最伟大的表演”之称。每年二月的中旬或下旬举行三天",1),
                new LuckEntity(9,"到西班牙参加'斗牛'受伤","玩家下回合暂停一次",
                        "“斗牛”是一项人与牛斗的运动。参与斗牛的人称为斗牛士，是西班牙的国技。这项运动非常危险，常有斗牛士因此受伤或死亡",1),
                new LuckEntity(10,"在撒哈拉沙漠因为海市蜃楼而迷路","现金最多的玩家罚1000元",
                        "海市蜃楼是一种光学幻景，是地球上物体反射的光经大气折射而形成的虚像，在沙莫常常出现幻景，让旅行者迷路。",1),
                new LuckEntity(11,"到苏格兰试穿苏格兰裙","捐200元再转转盘行动一次",
                        "苏格兰短裙，是一种男性穿着的民族礼服裙子，始创于苏格兰高地，以羊毛布匹制作而成，有格子花纹",1),
                new LuckEntity(12,"在澳洲被无尾熊抓伤因无尾熊指甲细菌多而发炎","付600元看医生",
                        "无尾熊只要吃尤加利树的树叶，就可以供给它们所需要的养分及水分，所以英语名为Koala是'不喝水'的意思",1),
                new LuckEntity(13,"到英国享受下午茶","最靠近英国的玩家付500元",
                        "最早有下午喝茶习惯的是以茶文化著称的中国，然而将下午茶发展为一种特定习俗的则是英国人",1),
                new LuckEntity(14,"在牙买加学习雷鬼音乐","房子最少名玩家免费盖一栋",
                        "雷鬼乐是牙买加盛行的曲风，融合了非洲传统乐，美国抒情蓝调及拉丁热情曲风对西方音乐有重大的影响",1)
        );
        return chanceCards;
    }
}
