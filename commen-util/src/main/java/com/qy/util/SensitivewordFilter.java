package com.qy.util;

import java.util.*;

public class SensitivewordFilter {


    @SuppressWarnings("rawtypes")
    public static Map sensitiveWordMap = null;
    public static int minMatchTYpe = 1;
    public static int maxMatchType = 2;


    public SensitivewordFilter() {
        sensitiveWordMap = initKeyWord();
    }

    @SuppressWarnings("rawtypes")
    public static Map initKeyWord() {
        try {

            Set<String> keyWordSet = readSensitiveWordFile();

            addSensitiveWordToHashMap(keyWordSet);
            //spring��ȡapplication��Ȼ��application.setAttribute("sensitiveWordMap",sensitiveWordMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sensitiveWordMap;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void addSensitiveWordToHashMap(Set<String> keyWordSet) {
        sensitiveWordMap = new HashMap(keyWordSet.size());
        String key = null;
        Map nowMap = null;
        Map<String, String> newWorMap = null;
        //���keyWordSet
        Iterator<String> iterator = keyWordSet.iterator();
        while (iterator.hasNext()) {
            key = iterator.next();    //�ؼ���
            nowMap = sensitiveWordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);       //ת����char��
                Object wordMap = nowMap.get(keyChar);       //��ȡ

                if (wordMap != null) {        //�����ڸ�key��ֱ�Ӹ�ֵ
                    nowMap = (Map) wordMap;
                } else {
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put("isEnd", "0");     //�������һ��
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");    //���һ��
                }
            }
        }
    }

    public static Set<String> readSensitiveWordFile() {
        Set<String> set = new HashSet<>();
        String[] String = new String[]{"习近平", "李克强", "张德江", "俞正声", "刘云山", "王岐山", "张高丽", "毛泽东", "周恩来", "刘少奇", "朱德", "邓小平", "江泽民", "胡锦涛", "彭丽媛", "普京", "特朗普", "金正恩", "蔡英文", "共产党", "中共", "国民党", "中华民国", "藏独", "西藏国", "疆独", "新疆国", "台湾国", "台独", "太阳花运动", "港独", "政变", "自焚", "镇压", "暴乱", "上访", "绝食", "静坐", "六四", "占中", "穆罕默德", "穆斯林", "真主", "回教", "希特勒", "基地组织", "东突", "达赖", "热比娅", "伊斯兰国", "ISIS", "ISIL", "圣战", "isis", "isil", "邪教", "法轮功", "李洪志", "法轮大法", "全能神", "呼喊派", "徒弟会", "全范围教会", "灵灵教", "新约教会", "观音法门", "主神教", "被立王", "同一教", "三班仆人派", "灵仙真佛宗", "天父的儿女", "达米宣教会", "世界以利亚福音宣教会", "常受教", "中华大陆行政执事站", "实际神", "东方闪电", "吸毒", "冰毒", "海洛因", "可卡因", "摇头丸", "罂粟", "白粉", "裸聊", "色诱", "强奸", "赌博", "走私", "爱滋", "sex", "AIDS", "fuck", "爆菊", "卖淫", "妓女", "婊子", "荡妇", "嫖娼", "乱交", "艳照", "色情", "做爱", "他妈", "你妈", "他娘", "傻逼", "乳房", "鸡巴", "阴茎", "阴道", "毛泽东", "周恩来", "刘少奇", "朱德", "彭德怀", "林彪", "刘伯承", "陈毅", "贺龙", "聂荣臻", "徐向前", "罗荣桓", "叶剑英", "李大钊", "陈独秀", "孙中山", "孙文", "孙逸仙", "邓小平", "陈云", "江泽民", "李鹏", "朱镕基", "李瑞环", "尉健行", "李岚清", "胡锦涛", "罗干", "温家宝", "吴邦国", "曾庆红", "贾庆林", "黄菊", "吴官正", "李长春", "吴仪", "回良玉", "曾培炎", "周永康", "曹刚川", "唐家璇", "华建敏", "陈至立", "陈良宇", "张德江", "张立昌", "张德江", "俞正声", "刘云山", "王岐山", "张高丽", "王乐泉", "刘云山", "王刚", "王兆国", "刘淇", "贺国强", "郭伯雄", "胡耀邦", "王乐泉", "王兆国", "周永康", "李登辉", "连战", "陈水扁", "宋楚瑜", "吕秀莲", "郁慕明", "蒋介石", "蒋中正", "蒋经国", "马英九", "习近平", "李克强", "吴帮国", "无帮国", "无邦国", "无帮过", "瘟家宝", "假庆林", "甲庆林", "假青林", "离长春", "习远平", "袭近平", "李磕墙", "贺过墙", "和锅枪", "粥永康", "轴永康", "肘永康", "周健康", "粥健康", "周小康", "李肇星务", "国务委员", "国务院", "中央委员", "发改委", "国家发展和改革委员会", "发展和改革委员会", "国家发展与改革委员会", "发展与改革委员会", "薄熙来", "温家饱", "温假饱", "胡惊涛", "习仲勋", "华国锋", "彭丽媛", "新闻出版总署", "新闻出版署", "蔡英文", "布什", "布莱尔", "小泉", "纯一郎", "萨马兰奇", "安南", "阿拉法特", "普京", "默克尔", "克林顿", "里根", "尼克松", "林肯", "杜鲁门", "赫鲁晓夫", "列宁", "斯大林", "马克思", "恩格斯", "金正日", "金日成", "萨达姆", "胡志明", "西哈努克", "希拉克", "撒切尔", "阿罗约", "曼德拉", "卡斯特罗", "富兰克林", "华盛顿", "艾森豪威尔", "拿破仑", "亚历山大", "路易", "拉姆斯菲尔德", "劳拉", "鲍威尔", "奥巴马", "本拉登", "奥马尔", "柴玲", "达赖喇嘛", "江青", "张春桥", "姚文元", "王洪文", "东条英机", "希特勒", "墨索里尼", "冈村秀树", "冈村宁次", "高丽朴", "赵紫阳", "王丹", "沃尔开西", "李洪志", "李大师", "赖昌星", "马加爵", "班禅", "额尔德尼", "山本五十六", "阿扁", "阿扁万岁", "热那亚", "热比娅", "尖阁列岛", "实际神", "东方闪电", "全能神", "安倍晋三", "金正恩", "张学良", "恐怖组织", "买毒品", "卖毒品", "茉莉花", "钓鱼岛", "钓鱼岛不属于中国", "突尼斯", "gongchandang", "西藏314事件", "新疆7.5事件", "新疆国", "党中央", "新闻管制", "一边一国", "两国论", "分裂中国", "革命", "茉莉花", "突尼斯", "国内动态详情", "回回", "六四", "六四运动", "美国之音", "密宗", "民国", "民进党", "民运", "民主", "民主潮", "摩门教", "纳粹", "南华早报", "南蛮", "明慧网", "起义", "亲民党", "瘸腿帮", "人民报", "法轮功", "法轮大法", "打倒共产党", "台独万岁", "圣战", "示威", "台独", "台独分子", "台联", "台湾民国", "台湾岛国", "台湾国", "台湾独立", "太子党", "天安门事件", "屠杀", "小泉", "新党", "新疆独立", "新疆分裂", "新疆国", "疆独", "西藏独立", "西藏分裂", "西藏国", "藏独", "藏青会", "藏妇会", "学潮", "学运", "一党专政", "一中一台", "两个中国", "一贯道", "游行", "造反", "真善忍", "镇压", "政变", "政治", "政治反对派", "政治犯", "中共", "共产党", "反党", "反共", "政府", "民主党", "中国之春", "转法轮", "自焚", "共党", "共匪", "苏家屯", "基地组织", "塔利班", "东亚病夫", "支那", "高治联", "高自联", "专政", "专制", "世界维吾尔大会", "核工业基地", "核武器", "铀", "原子弹", "氢弹", "导弹", "核潜艇", "大参考", "小参考", "国内动态清样", "释迦牟尼", "阿弥陀佛", "多维", "河殇", "摩门教", "穆罕默德", "圣战", "耶和华", "耶稣", "伊斯兰", "真主安拉", "白莲教", "天主教", "基督教", "东正教", "大法", "法轮", "法轮功", "瘸腿帮", "真理教", "真善忍", "转法轮", "自焚", "走向圆满", "黄大仙", "跳大神", "神汉", "神婆", "真理教", "大卫教", "阎王", "黑白无常", "牛头马面", "藏独", "高丽棒子", "疆独", "蒙古鞑子", "台独", "台独分子", "台联", "台湾民国", "西藏独立", "新疆独立", "南蛮", "老毛子", "回民吃猪肉", "k粉", "古柯碱", "谋杀", "杀人", "吸毒", "贩毒", "赌博", "拐卖", "走私", "卖淫", "造反", "强奸", "轮奸", "抢劫", "先奸后杀", "押大", "押小", "押注", "下注", "抽头", "坐庄", "赌马", "赌球", "筹码", "老虎机", "轮盘赌", "安非他命", "大麻", "可卡因", "海洛因", "冰毒", "摇头丸", "杜冷丁", "鸦片", "罂粟", "迷幻药", "白粉", "嗑药", "吸毒", "冰毒", "冰粉", "强暴幼女", "二奶", "偷窥图片", "成人影片", "AV电影下载", "夫妻床上激情自拍", "处女", "房事", "押大", "押小", "坐台", "猥亵", "猥琐", "肉欲", "肉体", "排泄", "卵子", "搞基", "约炮", "撸管子", "处男", "黄盘", "毛盘", "艾滋病", "性病", "叫春", "牛B", "牛比", "J8", "小姐兼职", "交媾", "毛片", "黄片", "交配", "房事", "性事", "偷窥", "马拉戈壁", "AIDS", "aids", "Aids", "DICK", "dick", "Dick", "penis", "sex", "SM", "屙", "爱滋", "淋病", "梅毒", "爱液", "屄", "逼", "臭机八", "臭鸡巴", "吹喇叭", "吹箫", "催情药", "屌", "肛交", "肛门", "龟头", "黄色", "机八", "机巴", "鸡八", "鸡巴", "机掰", "机巴", "鸡叭", "鸡鸡", "鸡掰", "鸡奸", "妓女", "奸", "茎", "精液", "精子", "尻", "口交", "滥交", "乱交", "轮奸", "卖淫", "屁眼", "嫖娼", "强奸", "强奸犯", "情色", "肉棒", "乳房", "乳峰", "乳交", "乳头", "乳晕", "三陪", "色情", "射精", "手淫", "威而钢", "威而柔", "伟哥", "性高潮", "性交", "性虐", "性欲", "穴", "颜射", "阳物", "一夜情", "阴部", "阴唇", "阴道", "阴蒂", "阴核", "阴户", "阴茎", "阴门", "淫", "淫秽", "淫乱", "淫水", "淫娃", "淫液", "淫汁", "淫穴", "淫洞", "援交妹", "做爱", "梦遗", "阳痿", "早泄", "奸淫", "性欲", "性交", "Bitch", "bt", "cao", "FUCK", "Fuck", "fuck", "kao", "NMD", "NND", "sb", "shit", "SHIT", "SUCK", "Suck", "tmd", "TMD", "tnnd", "K他命", "白痴", "笨蛋", "屄", "变态", "婊子", "操她妈", "操妳妈", "操你", "操你妈", "操他妈", "草你", "肏", "册那", "侧那", "测拿", "插", "蠢猪", "荡妇", "发骚", "废物", "干她妈", "干妳", "干妳娘", "干你", "干你妈", "干你妈B", "干你妈b", "干你妈逼", "干你娘", "干他妈", "狗娘养的", "滚", "鸡奸", "贱货", "贱人", "烂人", "老母", "老土", "妈比", "妈的", "马的", "妳老母的", "妳娘的", "你妈逼", "破鞋", "仆街", "去她妈", "去妳的", "去妳妈", "去你的", "去你妈", "去死", "去他妈", "日", "日你", "赛她娘", "赛妳娘", "赛你娘", "赛他娘", "骚货", "傻B", "傻比", "傻子", "上妳", "上你", "神经病", "屎", "屎妳娘", "屎你娘", "他妈的", "王八蛋", "我操", "我日", "乡巴佬", "猪猡", "屙", "干", "尿", "掯", "屌", "操", "骑你", "湿了", "操你", "操他", "操她", "骑你", "骑他", "骑她", "欠骑", "欠人骑", "来爽我", "来插我", "干你", "干他", "干她", "干死", "干爆", "干机", "FUCK", "机叭", "臭鸡", "臭机", "烂鸟", "览叫", "阳具", "肉棒", "肉壶", "奶子", "摸咪咪", "干鸡", "干入", "小穴", "强奸", "插你", "插你", "爽你", "爽你", "干干", "干X", "我操", "他干", "干它", "干牠", "干您", "干汝", "干林", "操林", "干尼", "操尼", "我咧干", "干勒", "干我", "干到", "干啦", "干爽", "欠干", "狗干", "我干", "来干", "轮干", "轮流干", "干一干", "援交", "骑你", "我操", "轮奸", "鸡奸", "奸暴", "再奸", "我奸", "奸你", "奸你", "奸他", "奸她", "奸一奸", "淫水", "淫湿", "鸡歪", "仆街", "臭西", "尻", "遗精", "烂逼", "大血比", "叼你妈", "靠你妈", "草你", "干你", "日你", "插你", "奸你", "戳你", "逼你老母", "挨球", "我日你", "草拟妈", "卖逼", "狗操卖逼", "奸淫", "日死", "奶子", "阴茎", "奶娘", "他娘", "她娘", "骚B", "你妈了妹", "逼毛", "插你妈", "叼你", "渣波波", "嫩b", "weelaa", "缔顺", "帝顺", "蒂顺", "系统消息", "午夜", "看下", "草泥马", "法克鱿", "雅蠛蝶", "潜烈蟹", "菊花蚕", "尾申鲸", "吉跋猫", "搞栗棒", "吟稻雁", "达菲鸡", "SM", "ML", "3P", "群P", "马勒戈壁", "双飞", "周恩來", "碡 ", "籀", "朱駿 ", "朱狨基", "朱容基", "朱溶剂", "朱熔基", "朱镕基", "邾", "猪操", "猪聋畸", "猪毛", "猪毛1", "舳", "瘃", "躅", "", "翥", "專政", "颛", "丬", "隹", "窀", "卓伯源", "倬", "斫", "诼", "髭", "鲻", "子宫", "秭", "訾", "自焚", "自民党", "自慰", "自已的故事", "自由民主论坛", "总理", "偬", "诹", "陬", "鄹", "鲰", "躜", "缵", "作爱", "作秀", "阼", "祚", "做爱", "阿扁萬歲", "阿萊娜", "啊無卵", "埃裏克蘇特勤", "埃斯萬", "艾麗絲", "愛滋", "愛滋病", "垵", "暗黑法師", "嶴", "奧克拉", "奧拉德", "奧利弗", "奧魯奇", "奧倫", "奧特蘭", "㈧", "巴倫侍從", "巴倫坦", "白立樸", "白夢", "白皮書", "班禪", "寶石商人", "保釣", "鮑戈", "鮑彤", "鮑伊", "暴風亡靈", "暴亂", "暴熱的戰士", "暴躁的城塔野獸", "暴躁的警衛兵靈魂", "暴躁的馬杜克", "北大三角地論壇", "北韓", "北京當局", "北美自由論壇", "貝尤爾", "韝", "逼樣", "比樣", "蹕", "颮", "鑣", "婊子養的 ", "賓周", "冰後", "博訊", "不滅帝王", " ", "不爽不要錢", "布萊爾", "布雷爾", "蔡崇國", "蔡啓芳", "黲", "操鶏", "操那嗎B", "操那嗎逼", "操那嗎比", "操你媽", "操你爺爺", "曹長青", "曹剛川", "草", "草你媽", "草擬媽", "册那娘餓比", "插那嗎B", "插那嗎逼", "插那嗎比", "插你媽", "插你爺爺", "覘", "蕆", "囅", "閶", "長官沙塔特", "常勁", "朝鮮", "車侖", "車侖女幹", "沉睡圖騰", "陳炳基", "陳博志", "陳定南", "陳建銘", "陳景俊", "陳菊", "陳軍", "陳良宇", "陳蒙", "陳破空", "陳水扁", "陳唐山", "陳希同", "陳小同", "陳宣良", "陳學聖", "陳一諮", "陳總統", "諶", "齔", "櫬", "讖", "程凱", "程鐵軍", "鴟", "痴鳩", "痴拈", "遲鈍的圖騰", "持不同政見 ", "赤色騎士", "赤色戰士", "處女膜", "傳染性病", "吹簫", "春夏自由論壇", "戳那嗎B", "戳那嗎逼", "戳那嗎比", "輳", "鹺", "錯B", "錯逼", "錯比", "錯那嗎B", "錯那嗎逼", "錯那嗎比", "達夫警衛兵", "達夫侍從", "達癩", "打飛機", "大參考", "大東亞", "大東亞共榮", "大鶏巴", "大紀元", "大紀元新聞網", "大紀園", "大家論壇", "大奶媽", "大史記", "大史紀", "大衛教", "大中國論壇", "大中華論壇", "大衆真人真事", "紿", "戴維教", "戴相龍", "彈劾", "氹", "蕩婦", "導師", "盜竊犯", "德維爾", "登輝", "鄧笑貧", "糴", "迪裏夏提", "覿", "地下教會", "帝國主義", "電視流氓", "叼你媽", "釣魚島", "丁關根", "東北獨立", "東部地下水路", "東方紅時空", "東方時空", "東南西北論談", "東社", "東升", "東條", "東條英機", "東突暴動", "東突獨立", "東土耳其斯坦", "東西南北論壇", "東亞", "東院看守", "動亂", "鬥士哈夫拉蘇", "鬥士霍克", "獨裁", "獨裁政治", "獨夫", "獨立臺灣會", "俄國 ", "鮞", "㈡", "發楞", "發掄", "發掄功", "發倫", "發倫功", "發輪", "發論", "發論公", "發論功", "發騷", "發正念", "法~倫", "法~淪", "法~綸", "法~輪", "法~論", "法爾卡", "法掄", "法掄功", "法侖", "法淪", "法綸", "法輪大法", "法輪功", "法十輪十功", "法謫", "法謫功", "反封鎖", "反封鎖技術", "反腐敗論壇", "反人類", "反社會", "方勵之", "防衛指揮官", "放蕩", "飛揚論壇", "廢墟守護者", "費鴻泰", "費良勇", "分隊長施蒂文", "粉飾太平", "糞便", "鱝", "豐饒的果實", "風雨神州", "風雨神州論壇", "封從德", "封殺", "封印的靈魂騎士", "馮東海", "馮素英", "紱", "襆", "嘸 ", "傅作義", "幹bi", "幹逼", "幹比", "幹的你", "幹幹幹", "幹她", "幹你", "幹你老比", "幹你老母", "幹你娘", "幹全家", "幹死", "幹死你", "幹他", "幹一家", "趕你娘", "岡巒", "剛比", "剛比樣子", "崗哨士兵", "肛門", "高麗棒子", "高文謙", "高薪養廉", "高自聯", "膏藥旗", "戈瑞爾德", "戈揚", "鴿派", "歌功頌德", "格雷(關卡排名管理者)", "格魯", "格魯(城鎮移動)", "鯁", "工自聯", "弓雖", "共産", "共産黨", "共産主義", "共黨", "共軍", "共榮圈", "緱", "狗誠", "狗狼養的", "狗娘養的", "狗養", "狗雜種", "覯", "轂", "古龍祭壇", "骨獅", "鯝", "鴰", "詿", "關卓中", "貫通兩極法", "廣聞", "嬀", "龜兒子", "龜公", "龜孫子", "龜頭", "龜投", "劌", "緄", "滾那嗎", "滾那嗎B", "滾那嗎錯比", "滾那嗎老比", "滾那嗎瘟比", "鯀", "咼", "郭俊銘", "郭羅基", "郭岩華", "國家安全", "國家機密", "國軍", "國賊", "哈爾羅尼", "頇", "韓東方", "韓聯潮", "韓正", "漢奸", "顥", "灝", "河殤", "賀國强", "賀龍", "黑社會", "黑手黨", "紅燈區", "紅色恐怖", "紅炎猛獸", "洪傳", "洪興", "洪哲勝", "黌", "鱟", "胡緊掏", "胡錦滔", "胡錦淘", "胡景濤", "胡喬木", "胡總書記", "湖岸護衛兵", "湖岸警衛兵", "湖岸哨兵隊長", "護法", "鸌", "華建敏", "華通時事論壇", "華夏文摘", "華語世界論壇", "華岳時事論壇", "懷特", "鍰", "皇軍", "黃伯源", "黃慈萍", "黃禍", "黃劍輝", "黃金幼龍", "黃菊", "黃片", "黃翔", "黃義交", "黃仲生", "回民暴動", "噦", "繢", "毀滅步兵", "毀滅騎士", "毀滅射手", "昏迷圖騰", "混亂的圖騰", "鍃", "活動 ", "擊倒圖騰", "擊傷的圖騰", "鶏8", "鶏八", "鶏巴", "鶏吧", "鶏鶏", "鶏奸", "鶏毛信文匯", "鶏女", "鶏院", "姬勝德", "積克館", "賫", "鱭", "賈廷安", "賈育台", "戔", "監視塔", "監視塔哨兵", "監視塔哨兵隊長", "鰹", "韉", "簡肇棟", "建國黨", "賤B", "賤bi", "賤逼", "賤比", "賤貨", "賤人", "賤種", "江八點", "江羅", "江綿恒", "江戲子", "江則民", "江澤慧", "江賊", "江賊民", "薑春雲", "將則民", "僵賊", "僵賊民", "講法", "蔣介石", "蔣中正", "降低命中的圖騰", "醬猪媳", "撟", "狡猾的達夫", "矯健的馬努爾", "嶠", "教養院", "癤", "揭批書", "訐", "她媽", "届中央政治局委員", "金槍不倒 ", "金堯如", "金澤辰", "巹", "錦濤", "經文", "經血", "莖候佳陰", "荊棘護衛兵 ", "靖國神社", "㈨", "舊斗篷哨兵", "齟", "巨槌騎兵", "巨鐵角哈克", "鋸齒通道被遺弃的骷髏", "鋸齒通道骷髏", "屨", "棬", "絕望之地", "譎", "軍妓", "開苞", "開放雜志", "凱奧勒尼什", "凱爾本", "凱爾雷斯", "凱特切爾", "砍翻一條街", "看中國", "闞", "靠你媽", "柯賜海", "柯建銘", "科萊爾", "克萊恩", "克萊特", "克勞森", "客戶服務", "緙", "空氣精靈", "空虛的伊坤", "空虛之地", "恐怖主義", "瞘", "嚳", "鄺錦文", "貺", "昆圖", "拉姆斯菲爾德", "拉皮條", "萊特", "賴士葆", "蘭迪", "爛B", "爛逼", "爛比", "爛袋", "爛貨", "濫B", "濫逼", "濫比", "濫貨", "濫交", "勞動教養所", "勞改", "勞教", "鰳", "雷尼亞", "誄", "李紅痔", "李洪寬", "李繼耐", "李蘭菊", "李老師", "李錄", "李祿", "李慶安", "李慶華", "李淑嫻", "李鐵映", "李旺陽", "李小鵬", "李月月鳥", "李志綏", "李總理", "李總統", "裏菲斯", "鱧", "轢", "躒", "奩", "連方瑀", "連惠心", "連勝德", "連勝文", "連戰", "聯總", "廉政大論壇", "煉功", "兩岸關係", "兩岸三地論壇", "兩個中國", "兩會", "兩會報道", "兩會新聞", "廖錫龍 ", "林保華", "林長盛", "林佳龍", "林信義", "林正勝", "林重謨", "躪", "淩鋒", "劉賓深", "劉賓雁", "劉剛", "劉國凱", "劉華清", "劉俊國", "劉凱中", "劉千石", "劉青", "劉山青", "劉士賢", "劉文勝", "劉文雄", "劉曉波", "劉曉竹", "劉永川", "㈥", "鷚", "龍虎豹", "龍火之心", "盧卡", "盧西德", "陸委會", "輅", "呂京花", "呂秀蓮", "亂交", "亂倫", "亂輪", "鋝", "掄功", "倫功", "輪大", "輪功", "輪奸", "論壇管理員", "羅福助", "羅幹", "羅禮詩", "羅文嘉", "羅志明", "腡", "濼", "洛克菲爾特", "媽B", "媽比", "媽的", "媽批", "馬大維", "馬克思", "馬良駿", "馬三家", "馬時敏", "馬特斯", "馬英九", "馬永成", "瑪麗亞", "瑪雅", "嗎的", "嗎啡", "勱", "麥克斯", "賣逼", "賣比", "賣國", "賣騷", "賣淫", "瞞報", "毛厠洞", "毛賊", "毛賊東", "美國", "美國參考", "美國佬", "美國之音", "蒙獨", "蒙古達子", "蒙古獨", "蒙古獨立", "禰", "羋", "綿恒", "黽", "民國", "民進黨", "民聯", "民意論壇", "民陣", "民主墻", "緡", "湣", "鰵", "摸你鶏巴", " ", "莫偉强", "木子論壇", "內褲", "內衣", "那嗎B", "那嗎逼", "那嗎錯比", "那嗎老比", "那嗎瘟比", "那娘錯比", "納粹", "奶頭", "南大自由論壇", "南蠻子", "鬧事", "能樣", "尼奧夫", "倪育賢", "鯢", "你媽", "你媽逼", "你媽比", "你媽的", "你媽了妹", "你說我說論壇", "你爺 ", "娘餓比", "捏你鶏巴", "儂著岡巒", "儂著卵拋", "奴隸魔族士兵", "女幹", "女主人羅姬馬莉", "儺", "諾姆", "潘國平", "蹣 ", "龐建國", "泡沫經濟", "轡", "噴你", "皮條客", "羆", "諞", "潑婦 ", "齊墨", "齊諾", "騎你", "磧", "僉", "鈐", "錢達", "錢國梁", "錢其琛", "膁", "槧", "錆", "繰", "喬石", "喬伊", "橋侵襲兵", "譙", "鞽", "篋", "親美", "親民黨", "親日", "欽本立", "禽獸", "唚", "輕舟快訊", "情婦", "情獸", "檾", "慶紅", "丘垂貞", "詘", "去你媽的", "闃", "全國兩會", "全國人大", "犬", "綣", "瘸腿幫", "愨", "讓你操", "熱比婭", "熱站政論網", "人民報", "人民大會堂", "人民內情真相", "人民真實", "人民之聲論壇", "人權", "日本帝國", "日軍", "日內瓦金融", "日你媽", "日你爺爺", "日朱駿", "顬", "乳頭", "乳暈", "瑞士金融大學", "薩達姆", "三K黨", "三個代表", "三級片", "三去車侖工力", "㈢", "毿", "糝", "騷B", "騷棒", "騷包", "騷逼", "騷棍", "騷貨", "騷鶏", "騷卵 ", "殺你全家", "殺你一家", "殺人犯", "傻鳥", "煞筆", "山口組", "善惡有報", "上訪", "上海幫", "上海孤兒院", "厙", "社會主義", "射了還說要", "灄", "詵", "神經病", "諗", "生孩子沒屁眼", "生命分流的圖騰", "澠", "聖射手", "聖戰", "盛華仁", "濕了還說不要", "濕了還說要", "釃", "鯴", "㈩", "石化圖騰", "石拳戰鬥兵", "時代論壇", "時事論壇", "鰣", "史萊姆", "史萊姆王", "士兵管理員瓦爾臣", "世界經濟導報", "事實獨立", "侍從貝赫爾特", "侍從倫斯韋", "貰", "攄", "數據中國", "雙十節", "氵去車侖工力", "氵去車侖工力?", "稅力", "司馬晋", "司馬璐", "司徒華", "私?服", "私處", "思科羅", "斯諾", "斯皮爾德", "四川獨", "四川獨立", "四人幫", "㈣", "宋書元", "藪", "蘇菲爾", "蘇拉", "蘇南成", "蘇紹智", "蘇特勒守護兵", "蘇特勤", "蘇特勤護衛兵", "蘇特勤魔法師", "蘇曉康", "蘇盈貴", "蘇貞昌", "誶", "碎片製造商人馬克", "碎片製造商人蘇克", "孫大千", "孫中山", "他媽", "他媽的", "他嗎的", "他母親", "塔內", "塔烏", "鰨", "闥", "臺盟", "臺灣帝國", "臺灣獨立", "臺灣獨", "臺灣共産黨", "臺灣狗", "臺灣建國運動組織", "臺灣民國", "臺灣青年獨立聯盟", "臺灣政論區", "臺灣自由聯盟", "鮐", "太監", "泰奴橋警衛兵", "泰奴橋掠奪者", "湯光中", "唐柏橋", "鞀", "謄", "天安門", "天安門錄影帶", "天安門事件", "天安門屠殺", "天安門一代", "天閹", "田紀雲", "齠", "鰷", "銚", "庭院警衛兵", "統獨", "統獨論壇", "統戰", "頭領奧馬", "頭領墳墓管理員", "圖書管理員卡特", "屠殺", "團長戈登", "團員馬爾汀", "摶", "鼉", "籜", "膃", "外交論壇", "外交與方略", "晚年周恩來", "綰", "萬里", "萬潤南", "萬維讀者論壇", "萬曉東", "王寶森", "王超華", "王輔臣", "王剛", "王涵萬", "王滬寧", "王軍濤", "王樂泉", "王潤生", "王世堅", "王世勛", "王秀麗", "王兆國", "網禪", "網特", "猥褻", "鮪", "溫B", "溫逼", "溫比", "溫家寶", "溫元凱", "閿", "無界瀏覽器", "吳百益", "吳敦義", "吳方城", "吳弘達", "吳宏達", "吳仁華", "吳淑珍", "吳學燦", "吳學璨", "吳育升", "吳志芳", "西藏獨", "吸收的圖騰", "吸血獸", "覡", "洗腦", "系統", "系統公告", "餼", "郤", "下賤", "下體", "薟", "躚", "鮮族", "獫", "蜆", "峴", "現金", "現金交易", "獻祭的圖騰", "鯗", "項懷誠", "項小吉", "嘵", "小B樣", "小比樣", "小參考", "小鶏鶏", "小靈通", "小泉純一郎", "謝長廷", "謝深山", "謝選駿", "謝中之", "辛灝年", "新觀察論壇", "新華舉報", "新華內情", "新華通論壇", "新疆獨", "新生網", "新手訓練營", "新聞出版總署", "新聞封鎖", "新義安", "新語絲", "信用危機", "邢錚", "性愛", "性無能", "修煉", "頊", "虛弱圖騰", "虛無的飽食者", "徐國舅", "許財利", "許家屯", "許信良", "諼", "薛偉", "學潮", "學聯", "學運", "學自聯", "澩", "閹狗", "訁", "嚴家其", "嚴家祺", "閻明複", "顔清標", "顔慶章", "顔射", "讞", "央視內部晚會", "陽具", "陽痿", "陽物", "楊懷安", "楊建利", "楊巍", "楊月清", "楊周", "姚羅", "姚月謙", "軺", "搖頭丸", "藥材商人蘇耐得", "藥水", "耶穌", "野鶏", "葉菊蘭", "夜話紫禁城", "一陀糞", "㈠", "伊莎貝爾", "伊斯蘭", "伊斯蘭亞格林尼斯", "遺精", "議長阿茵斯塔", "議員斯格文德", "异見人士", "异型叛軍", "异議人士", "易丹軒", "意志不堅的圖騰", "瘞", "陰部", "陰唇", "陰道", "陰蒂", "陰戶", "陰莖", "陰精", "陰毛", "陰門", "陰囊", "陰水", "淫蕩", "淫穢", "淫貨", "淫賤", "尹慶民", "引導", "隱者之路", "鷹眼派氏族", "硬直圖騰", "憂鬱的艾拉", "尤比亞", "由喜貴", "游蕩的僵尸", "游蕩的士兵", "游蕩爪牙", "游錫坤", "游戲管理員", "友好的魯德", "幼齒", "幼龍", "于幼軍", "余英時", "漁夫菲斯曼", "輿論", "輿論反制", "傴", "宇明網", "齬", "飫", "鵒", "元老蘭提(沃德）", "圓滿", "緣圈圈", "遠志明", "月經", "韞", "雜種", "鏨", "造愛", "則民", "擇民", "澤夫", "澤民", "賾", "賊民", "譖", "扎卡維是英雄", "驏", "張伯笠", "張博雅", "張鋼", "張健", "張林", "張清芳", "張偉國", "張溫鷹", "張昭富", "張志清", "章孝嚴", "帳號", "賬號", "招鶏", "趙海青", "趙建銘", "趙南", "趙品潞", "趙曉微", "趙紫陽", "貞操", "鎮壓", "爭鳴論壇", "正見網", "正義黨論壇", "㊣", "鄭寶清", "鄭麗文", "鄭義", "鄭餘鎮", "鄭源", "鄭運鵬", "政權", "政治反對派", "縶", "躑", "指點江山論壇", "騭", "觶", "躓", "中毒的圖騰", "中毒圖騰", "中俄邊界", "中國復興論壇", "中國共産黨", "中國孤兒院", "中國和平", "中國論壇", "中國社會進步黨", "中國社會論壇", "中國威脅論", "中國問題論壇", "中國移動通信", "中國真實內容", "中國之春", "中國猪", "中華大地", "中華大衆", "中華講清", "中華民國", "中華人民實話實說", "中華人民正邪", "中華時事", "中華養生益智功", "中華真實報道", "中央電視臺", "鐘山風雨論壇", " ", "周鋒鎖", "周守訓 ", "朱鳳芝", "朱立倫", "朱溶劑", "㈱", "猪聾畸", "主攻指揮官", "主義", "助手威爾特", "專制", "顓", "轉化", "諑", "資本主義", "鯔", "子宮", "自民黨", "自由民主論壇", "總理", "諏", "鯫 ", "躦", "纘", "作愛", "做愛", "胡总书记", "胡景涛", "大纪元", "大纪元新闻网", "燕玲论坛", "fuck", "共产党", "urban", "我操", "cao", "他妈的", "TMD", "鸡巴", "煞笔", "傻B", "法轮功", "江泽民", "胡锦涛", "温家宝", "urban-rivals", "rivals", "我日", "UR", "ur", "性交", "口交", "婊子", "妓女", "", "她妈", "牛逼", "牛B", "牛比", "煞笔", "傻逼", "傻B", "操你妈", "装逼", "装B", "日你妈", "不玩了", "删号", "卖号  ", "删 号", "妈的", "妈逼", "草你妈", "T.M.D", "JB", "jb", "出售账号", "出售此号", "卖号", "U/R", "U-R", "j8", "吗的", "8仙", "狗日", "出售神符", "色情", "黄色", "藏独", "台独", "法轮大法", "鸡巴", "毛泽东", "NPC", "*法*轮*功*", "*李*洪*志*阿扁", "阿扁万岁", "阿拉", "阿拉法特", "挨球", "安南", "安全局", "澳洲光明网", "八九", "八九风波", "办理文凭", "办理证件", "包皮", "保钓", "保监会", "保密局", "鸨", "鲍岳桥", "暴动", "暴乱", "暴徒", "北京之春", "贝肉", "本?拉登", "本?拉登", "苯比", "笨屄", "笨逼", "屄", "屄毛", "逼毛", "逼你老母", "逼样", "比毛", "婊", "婊子", "宾周", "冰毒", "波霸", "博讯", "薄一波", "布莱尔", "布雷尔", "布什", "布什", "财政部", "参事室", "藏独", "藏独", "藏独", "操", "操GM", "操Gm", "操gM", "操gm", "操XX", "操逼", "操比", "操蛋", "操你", " 交媾", "CAO", " K粉", "J8", " 小姐兼职", "交媾", "西藏314事件", "新疆7.5事件", " 乱伦", "毛片", "黄片", "交配", "群交", "小姐兼职", "茉莉花革命", "操你八辈祖宗", "操你妈", "操你妈屄", "操他", "曹刚川", "草的你妈", "草妈", "草你妈", "草拟妈", "肏", "测绘局", "插GM", "插Gm", "插gM", "插gm", "插妳", "插你", "插你妈", "插深些", "产权局", "朝鲜", "车臣", "车仑", "陈功", "陈良宇", "陈水扁", "陈希同", "陈晓宁", "陈毅", "陈至立", "成人电影", "成人片", "吃大便", "吃屎", "迟浩田", "赤匪", "抽插", "抽你丫的", "臭化西", "出售假币", "出售枪支", "出售手枪", "吹喇叭", "吹箫", "春药", "蠢猪", "戳你", "粗制吗啡", "催情药", "达赖", "达赖喇嘛", "打炮", "", "大B", "大逼", "大便", "大波波", "大麻", "大麻树脂", "大麻油", "大师", "戴维教", "大学骚乱", "大血B", "大血比", "呆卵", "戴海静", "戴红", "戴晶", "戴维教", "党主席", "荡妇", "档案局", "盗窃犯", "盗窃犯", "道教", "邓小平", "帝国主义", "电监会", "叼你", "叼你妈", "屌", "屌7", "屌鸠", "屌毛", "屌妳", "屌七", "屌西", "钓鱼台", "丁关根", "丁子霖", "东北独立", "东升", "东条英机", "东突", "东突暴动和独立", "东突组织", "东亚病夫", "董建华", "董贱华", "董文华", "懂文华", "独立", "独立台湾会", "恩格斯", "二B", "二屄", "二逼", "二乙基酰胺发抡", "发抡功", "发伦", "发伦功", "发轮", "发论", "发论公", "发论功", "发骚", "法(轮)功", "法*轮*功", "法功", "法愣", "法仑", "法轮", "法轮大法", "法轮功", "法西斯", "法制办", "反动", "反革命", "发票", "冰粉", "性奴", "反共", "反华", "反恐委员会", "反日", "反政府", "分裂祖国", "佛教", "佛展千手法", "佛祖", "斧头镰刀", "阝月", "傅鹏", "傅作义", "干GM", "干Gm", "干gM", "干gm", "干拎娘", "干妳", "干妳老母", "干妳妈", "干妳娘", "干你", "干你妈", "干你妈b", "干你妈逼", "干你娘", "干七八", "干死你", "肛", "肛交", "肛门", "港澳办", "高俊", "高丽棒子", "高校暴乱", "高校群体事件", "高校骚乱", "睾", "睾丸", "膏药旗", "弓虽女干", "公安", "公安部", "公安局", "共产党", "共产主义", "共匪", "共狗", "狗b", "狗操", "狗卵", "狗娘", "狗屁", "狗日", "狗日的", "狗屎", "观世音", "官逼民反", "官商勾结", "龟儿子", "龟公", "龟孙子", "龟头", "鬼村", "滚", "郭伯雄", "国安局", "国防部", "国防科工委", "国管局", "国际法院", "国家民委", "国家主席", "国家主要部委", "国民党", "国民党万岁", "海洛因", "海洋局", "何候华", "贺国强", "贺龙", "黑社会", "黑手党", "黑手党", "黑手党", "红卫兵", "洪兴", "洪志", "后庭", "胡XX", "胡紧涛", "胡紧掏", "胡紧套", "胡锦涛", "胡锦淘", "胡乔木", "胡耀邦", "胡主席", "花柳", "华国锋", "华建敏", "换妻", "黄　菊", "黄菊", "黄色电影", "黄色小电影", "回教", "回良玉", "回民暴动", "回族人吃猪肉", "昏药", "火棒", "机八", "机巴", "鸡八", "鸡巴", "鸡叭", "鸡芭", "鸡掰", "鸡奸", "基地组织", "基督", "基督教", "激情电影", "激情小电影", "鸡", "计牌软件", "计生委", "妓", "妓女", "妓院", "贾庆林", "奸", "奸夫淫妇", "奸你", "奸淫", "贱", "贱逼", "贱货", "贱人", "江Core", "江八", "江八点", "江独裁", "江核心", "江青", "江戏子", "江择民", "江泽民", "江贼民", "江折民", "江猪", "江猪媳", "江主席", "僵贼民", "疆独", "蒋介石", "蒋经国", "蒋中正", "酱猪媳", "交通部", "姣西", "叫床", "叫鸡", "叫小姐", "教育部", "她妈的金日成", "金正日", "禁书", "经济社会理事会", "经社理事会", "精液", "精子", "警匪一家", "敬国神社", "靖国神社", "静坐", "纠察员", "鸠", "鸠屎", "军长发威", "军国主义", "军妓", "尻", "靠", "靠你妈", "靠腰", "可待因", "可卡叶", "可卡因", "克林顿", "恐怖份子", "恐怖主义", "口交", "寇晓伟", "狂操", "狂操你全家", "拉登", "拉姆斯菲尔德", "懒教", "烂B", "烂屄", "烂逼", "烂比", "烂屌", "烂货", "劳+教+所", "劳动保障部", "老逼", "老毛子", "老母", "黎阳评", "李长春", "李登辉", "李弘旨", "李红志", "李宏旨", "李宏志", "李洪志", "李岚清", "李鹏", "李鹏*", "李瑞环", "李山", "李铁映", "李先念", "连战", "联大", "联合国", "联合国大会", "联易", "联易互动", "粮食局", "两腿之间", "列宁", "林彪", "林业局", "刘　淇", "刘军", "刘淇", "刘少奇", "刘云山", "流氓", "六.四", "六。四", "六?四", "六合彩", "六四", "六-四", "六四事件", "六四真相", "龙新民", "吕秀莲", "旅游局", "卵", "轮功", "轮奸", "罗　干", "罗干", "骡干", "妈逼", "妈比", "妈卖妈屁", "妈批", "妈祖", "妈B", "妈的", "麻醉钢枪", "麻醉枪", "麻醉药", "麻醉乙醚", "马克思", "马卖马屁", "马英九", "吗啡", "吗啡碱", "吗啡片", "买财富", "买卖枪支", "麦角酸", "卖.国", "卖B", "卖ID", "卖QQ", "卖逼", "卖比", "卖财富", "卖党求荣", "卖国", "卖国求荣", "卖号", "卖卡", "卖软件", "卖淫", "毛XX", "毛厕洞", "毛一鲜", "毛泽东", "毛贼东", "毛主席", "梅花网", "美国", "美国佬", "美国之音", "美利坚", "蒙尘药", "蒙独", "蒙古达子", "蒙古独立", "迷魂药", "迷奸药", "迷歼药", "迷药", "密洞", "密宗", "民航局", "民进党", "民运", "民政部", "明慧网", "摩门教", "莫索里尼", "穆罕默德", "穆斯林", "乳头", "奶子", "妳老母的", "妳妈的", "妳马的", "妳娘的", "南联盟", "南蛮子", "南蛮子", "嫩B", "嫩b", "伱妈", "你爸", "你大爷", "你二大爷", "你老母", "你老味", "你姥", "你姥姥的", "你妈", "你妈逼", "你妈的", "你娘", "你爷爷的", "鸟GM", "鸟Gm", "鸟gM", "鸟gm", "鸟你", "牛逼", "牛比", "农业部", "虐待", "拍肩神药", "喷你", "彭真", "皮条", "屁眼", "嫖客", "苹果日报", "破坏", "破鞋", "仆街", "普京", "气象局", "钱其琛", "枪决女犯", "枪决现场", "枪支弹药", "强奸", "强奸犯", "强卫", "强效失意药", "强硬发言", "抢劫", "乔石", "侨办", "切七", "窃听器", "窃听器材", "亲民党", "青天白日", "情色", "去你妈的", "去死", "全国人大", "瘸腿帮", "人大", "人大代表", "人代会", "人弹", "人民", "人民大会堂", "人民广场", "人民日报", "人民银行", "人体炸弹", "日GM", "日Gm", "日gM", "日gm", "日X妈", "日本RING", "日本鬼子", "日你", "日你妈", "日你娘", "日他娘", "肉棒", "肉壁", "肉洞", "肉缝", "肉棍", "肉棍子", "肉穴", "乳", "乳波臀浪", "乳房", "乳交", "乳头", "撒尿", "萨达姆", "塞白", "塞你爸", "塞你公", "塞你老母", "塞你老师", "塞你母", "塞你娘", "三个呆婊", "三个代婊", "三级片", "三民主义", "三陪", "三陪女", "三去车仑", "三唑仑", "骚", "骚B", "骚逼", "骚货", "骚", "色情", "色情电影", "色情服务", "色情小电影", "杀人犯", "傻B", "傻屄", "傻逼", "傻比", "傻吊", "傻卵", "傻子", "煞逼", "商务部", "上妳", "上你", "社科院", "射精", "身份生成器", "神经病", "神通加持法", "生鸦片", "圣女峰", "十八摸", "十年动乱石进", "食捻屎", "食屎", "驶你爸", "驶你公", "驶你老母", "驶你老师", "驶你母", "驶你娘", "是鸡", "手淫", "受虐狂", "售ID", "售号", "售软件", "双峰微颤", "氵去", "水利部", "水去车仑", "税务总局", "司法部", "私服", "私/服", "私\\服", "私服", "私-服", "私—服", "斯大林", "死gd", "死GD", "死gm", "死GM", "死全家", "四川独立", "四人帮", "宋楚瑜", "宋祖英", "孙文", "孙逸仙", "孙中山", "他爹", "他妈", "他妈的", "他马的", "他母亲", "他祖宗", "台办", "台独", "台联", "台湾党", "台湾帝国", "台湾独立", "台湾共产党", "台湾共和国", "台湾狗", "台湾国", "台湾民国", "太监", "太子党", "唐家璇", "天皇陛下", "田纪云", "舔西", "投毒杀人", "透视软件", "推油", "外　挂", "外挂", "外/挂", "外\\挂", "外_挂", "外挂", "外-挂", "外—挂", "外汇局", "外交部", "外专局", "晚年周恩来", "万税", "王八蛋", "王宝森", "王刚", "王昊", "王乐泉", "王岐山", "王太华", "王兆国", "王震", "网管", "威而钢", "威而柔", "卫生部", "尉健行", "温加宝", "温家宝", "温家保", "温馨", "温总理", "文化部", "文物局", "倭国", "倭寇", "我操", "我操你", "我干", "我妳老爸", "我日", "我日你", "无界浏览器", "吴　仪", "吴邦国", "吴官正", "吴仪", "五星红旗", "西藏独立", "西藏天葬", "希拉克", "希特勒", "希望之声", "洗脑班", "系统", "系统公告", "系统讯息", "鲜族", "乡巴佬", "想上你", "小鸡鸡", "小泉", "小泉纯一郎", "小日本", "小肉粒", "小乳头", "小穴", "邪教", "新疆独立", "兴奋剂", "性爱", "性交", "性虐待", "性无能", "性欲", "徐光春", "学潮", "血逼", "血腥图片", "鸦片", "鸦片液", "鸦片渣", "烟草局", "严方军", "阳精", "阳具", "摇头丸", "摇头玩", "耶和华", "耶苏", "耶稣", "叶剑英", "夜情", "一党专制", "一贯道", "一国两制", "一夜情", "一中一台", "伊拉克", "伊朗", "伊斯兰", "以茎至洞", "抑制剂", "阴部", "阴唇", "阴道", "阴蒂", "阴核", "阴户", "阴茎", "阴毛", "阴水", "阴小撕大", "淫", "淫荡", "淫秽", "淫货", "淫贱", "淫叫", "淫毛", "淫靡", "淫水", "淫娃", "淫语连连", "淫欲", "英雄纪念碑", "硬挺", "邮政局", "游戏发奖员", "游戏宫理员", "游戏管理员", "游行", "俞正声", "舆论钳制", "玉杵", "欲火焚身", "原子能机构", "援交", "远程偷拍", "曰GM", "曰Gm", "曰gM", "曰gm", "曰你", "月经", "月经不调", "月经", "扎卡维是英雄", "杂种", "造反", "曾培炎", "曾庆红", "扎卡维", "张朝阳", "张潮阳", "张德江", "张磊", "张立昌", "张小平", "赵紫阳", "侦探设备", "真理教", "中国恐怖组织", "中华民国", "中南海", "中宣部", "周恩来", "周永康", "周总理", "朱德", "朱容鸡", "朱容基", "朱熔基", "朱镕基", "朱总理", "猪操", "猪容基", "主席", "转法轮", "转法轮", "装屄", "装逼", "追查国际", "子女任职名单", "自焚", "自杀手册", "自杀指南", "自制手枪", "自治机关", "宗教", "总局", "总理", "作爱", "坐台的", "做爱", "共产党", "江泽民", "胡锦涛", "温家宝", "严方军", "屄毛", "操逼毛", "东突", "骚货", "法轮功", "江泽民", "胡锦涛", "温家宝", "urban-rivals", "rivals", "u r", "ur", "我日", "UR", "ur", "性交", "口交", "U R", "taobao", "webgame.com.cn", "婊子", "妓女", "他妈", "她妈", "牛逼", "牛比", "牛B", "煞笔", "傻逼", "傻B", "你妈", "操你妈", "装逼", "装B", "日你妈", "不玩了", "删号", "卖号", "垃圾游戏", "烂游戏", "删 号", "妈的", "妈逼", "草你妈", "T.M.D", "JB", "jb", "淘宝", "出售账号", "出售此号", "卖号", "U/R", "U-R", "c a o", "j8", "吗的", "8仙", "狗日", "出售神符", "色情", "黄色", "h站", "龙虎", "虎门", "龙 虎 门", "WEB牌戰", "WEB战牌", "战牌", "8 仙", "ＵＲ", "ur", "UR", "街头对抗", "藏独", "台独", "法轮大法", "混 沌决", "ur", "UR", "urban", "鸡巴", "坐台的", "作爱", "总理", "宗教", "自治机关", "自制手枪", "ISIS", "ISIL", "isis", "isil"};
        for (String s : String) {
            set.add(s);
        }
        return set;
    }

//    public static Set<String> readSensitiveWordFile() throws Exception {
//        Set<String> set;
//
//        File file = new File("C:\\SensitiveWord.txt");
//        InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");
//        try {
//            if (file.isFile() && file.exists()) {
//                set = new HashSet<>();
//                BufferedReader bufferedReader = new BufferedReader(read);
//                String txt;
//                while ((txt = bufferedReader.readLine()) != null) {
//                    set.add(txt);
//                }
//            } else {
//                throw new Exception("敏感词读取错误");
//            }
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            read.close();
//        }
//        return set;
//    }

    public boolean isContaintSensitiveWord(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            int matchFlag = SensitivewordFilter.CheckSensitiveWord(txt, i, matchType);
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }


    public static Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = CheckSensitiveWord(txt, i, matchType);
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return sensitiveWordList;
    }


    public static String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
        String resultTxt = txt;
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }


    private static String getReplaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;
        for (int i = 1; i < length; i++) {
            resultReplace += replaceChar;
        }

        return resultReplace;
    }


    @SuppressWarnings({"rawtypes"})
    public static int CheckSensitiveWord(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        int matchFlag = 0;
        char word = 0;
        Map nowMap = initKeyWord();
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    if (SensitivewordFilter.minMatchTYpe == matchType) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        if (matchFlag < 2 || !flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

    public static void main(String[] args) {
        SensitivewordFilter filter = new SensitivewordFilter();
        System.out.println("敏感词的数量：" + filter.sensitiveWordMap.size());
        String string = "习近平、李克强、张德江、俞正声、太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
            + "然后法.轮.功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
            + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三.级.片 深人静的晚上，关上电话静静的发呆着。";
        System.out.println("待检测语句字数：" + string.length());
        long beginTime = System.currentTimeMillis();
        Set<String> set = filter.getSensitiveWord(string, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        System.out.println("总共消耗时间为：" + (endTime - beginTime));
        String str = filter.replaceSensitiveWord(string, 1, "*");
        System.out.println("替换后：" + str);


        //从文件 读取 start
//        String s = "";
//        File file = new File("D:\\SensitiveWord.txt");
//        try {
//            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");
//            if (file.isFile() && file.exists()) {
//                BufferedReader bufferedReader = new BufferedReader(read);
//                String txt;
//                while ((txt = bufferedReader.readLine()) != null) {
//                    s += "\"";
//                    s += txt;
//                    s += "\"";
//                    s += ",";
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        System.out.println(s);
        //从文件 读取 end


    }

}
