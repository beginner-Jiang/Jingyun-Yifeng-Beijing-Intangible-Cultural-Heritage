package org.example.beijing.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TextFilterUtil {

    // 非遗关键词库（完整词汇）
    private static final Set<String> INHERITANCE_KEYWORDS = new HashSet<>(Arrays.asList(
            "景泰蓝", "京剧", "相声", "雕漆", "天桥中幡", "厂甸庙会", "同仁堂", "京西太平鼓",
            "永定河传说", "北京宫毯", "智化寺京音乐", "北京面人郎", "兔儿爷", "脸谱", "皮影",
            "玉雕", "牙雕", "花丝", "扎燕", "庙会", "太平鼓", "中幡", "宫毯", "花丝镶嵌",
            "风筝", "内画", "鼻烟壶", "剪纸", "泥塑", "彩塑", "木雕", "砖雕", "石雕",
            "核雕", "漆器", "金漆镶嵌", "京绣", "补花", "绒鸟", "绒花", "毛猴", "鬃人",
            "面人", "糖人", "糖画", "吹糖人", "草编", "竹编", "藤编", "柳编", "苇编",
            "棕编", "麦秆剪贴", "皮影戏", "木偶戏", "杂技", "魔术", "马戏", "评书", "大鼓",
            "单弦", "岔曲", "时调", "小曲", "琴书", "快板", "数来宝", "双簧", "口技",
            "京韵大鼓", "梅花大鼓", "西河大鼓", "乐亭大鼓", "京东大鼓", "河南坠子", "山东快书",
            "四川清音", "苏州评弹", "扬州评话", "福州评话", "闽剧", "粤剧", "川剧", "越剧",
            "黄梅戏", "楚剧", "湘剧", "赣剧", "秦腔", "晋剧", "河北梆子", "豫剧", "吕剧",
            "沪剧", "淮剧", "锡剧", "扬剧", "甬剧", "绍剧", "婺剧", "瓯剧", "徽剧", "汉剧",
            "祁剧", "桂剧", "滇剧", "黔剧", "陇剧", "评剧", "吉剧", "龙江剧", "藏戏",
            "维吾尔剧", "傣剧", "彝剧", "壮剧", "苗剧"
    ));

    // 不良词库
    private static final Set<String> BAD_WORDS = new HashSet<>(Arrays.asList(
            "fuck", "shit", " bitch", "tmd", "妈逼", "操你妈", "傻逼", "垃圾", "废物"
    ));

    // 停用词库
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "的", "了", "在", "是", "我", "你", "他", "她", "它", "我们", "你们", "他们",
            "这个", "那个", "这些", "那些", "这里", "那里", "和", "与", "或", "但是",
            "所以", "因为", "如果", "那么", "就", "也", "还", "又", "都", "只", "不"
    ));

    private static final Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]");

    public Map<String, Integer> extractKeywords(List<String> texts, int topN) {
        Map<String, Integer> freqMap = new HashMap<>();

        for (String text : texts) {
            if (text == null || text.trim().isEmpty()) continue;
            String cleaned = cleanText(text);
            // 先匹配非遗关键词库中的完整词汇
            for (String keyword : INHERITANCE_KEYWORDS) {
                if (cleaned.contains(keyword)) {
                    int count = countOccurrences(cleaned, keyword);
                    freqMap.put(keyword, freqMap.getOrDefault(keyword, 0) + count);
                }
            }
            // 再提取其他中文词（长度≥2）
            List<String> words = splitChineseWords(cleaned);
            for (String word : words) {
                if (word.length() < 2) continue;
                if (STOP_WORDS.contains(word)) continue;
                if (isBadWord(word)) continue;
                if (INHERITANCE_KEYWORDS.contains(word)) continue;
                freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
            }
        }

        return freqMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(keyword, idx)) != -1) {
            count++;
            idx += keyword.length();
        }
        return count;
    }

    private String cleanText(String text) {
        text = text.replaceAll("<[^>]+>", "");
        text = text.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z0-9]", " ");
        return text;
    }

    private List<String> splitChineseWords(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (CHINESE_PATTERN.matcher(String.valueOf(c)).matches()) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    words.add(current.toString());
                    current.setLength(0);
                }
            }
        }
        if (current.length() > 0) {
            words.add(current.toString());
        }
        return words;
    }

    private boolean isBadWord(String word) {
        return BAD_WORDS.stream().anyMatch(bad -> word.contains(bad) || bad.contains(word));
    }
}