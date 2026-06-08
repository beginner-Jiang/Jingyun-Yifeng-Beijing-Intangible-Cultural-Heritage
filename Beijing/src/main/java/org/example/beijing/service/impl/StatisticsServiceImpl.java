package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.dto.ChartDataDTO;
import org.example.beijing.entity.User;
import org.example.beijing.mapper.CollectionMapper;
import org.example.beijing.mapper.LoginLogMapper;
import org.example.beijing.mapper.UserMapper;
import org.example.beijing.service.StatisticsService;
import org.example.beijing.service.WordCloudService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CollectionMapper collectionMapper;
    private final LoginLogMapper loginLogMapper;
    private final WordCloudService wordCloudService;
    private final UserMapper userMapper;

    private LocalDateTime getStartTime(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "week":   return now.minusWeeks(1).with(LocalTime.MIN);
            case "year":   return now.withDayOfYear(1).with(LocalTime.MIN);
            case "all":    return LocalDateTime.of(2000, 1, 1, 0, 0);
            default:       return now.withDayOfMonth(1).with(LocalTime.MIN);
        }
    }

    @Override
    public ChartDataDTO.BarChart getHotRanking(String period) {
        LocalDateTime start = getStartTime(period);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        List<Map<String, Object>> results = collectionMapper.selectCountByItem(start, end);
        List<String> xAxis = new ArrayList<>();
        List<Integer> series = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String name = (String) row.get("name");
            Number count = (Number) row.get("count");
            if (name != null && count != null && count.intValue() > 0) {
                xAxis.add(name);
                series.add(count.intValue());
            }
        }
        if (xAxis.size() > 10) {
            xAxis = xAxis.subList(0, 10);
            series = series.subList(0, 10);
        }
        return new ChartDataDTO.BarChart(xAxis, series);
    }

    @Override
    public ChartDataDTO.PieChart getInheritorAgePie(String period) {
        LocalDateTime start = getStartTime(period);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (!"all".equals(period)) {
            wrapper.ge(User::getCreatedAt, start);
        }
        List<User> users = userMapper.selectList(wrapper);
        Map<String, Integer> ageMap = new LinkedHashMap<>();
        ageMap.put("30岁以下", 0);
        ageMap.put("30-40岁", 0);
        ageMap.put("40-50岁", 0);
        ageMap.put("50-60岁", 0);
        ageMap.put("60岁以上", 0);

        for (User user : users) {
            if (user.getAge() == null) continue;
            int age = user.getAge();
            if (age < 30) ageMap.put("30岁以下", ageMap.get("30岁以下") + 1);
            else if (age < 40) ageMap.put("30-40岁", ageMap.get("30-40岁") + 1);
            else if (age < 50) ageMap.put("40-50岁", ageMap.get("40-50岁") + 1);
            else if (age < 60) ageMap.put("50-60岁", ageMap.get("50-60岁") + 1);
            else ageMap.put("60岁以上", ageMap.get("60岁以上") + 1);
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : ageMap.entrySet()) {
            if (entry.getValue() > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", entry.getKey());
                item.put("value", entry.getValue());
                data.add(item);
            }
        }
        return new ChartDataDTO.PieChart(data);
    }

    @Override
    public ChartDataDTO.RoseChart getRegionRose() {
        List<Map<String, Object>> regionStats = collectionMapper.selectRegionCount();
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map<String, Object> row : regionStats) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", row.get("region"));
            item.put("value", ((Number) row.get("count")).intValue());
            data.add(item);
        }
        return new ChartDataDTO.RoseChart(data);
    }

    @Override
    public ChartDataDTO.LineChart getUserActiveLine(String period) {
        LocalDateTime start = getStartTime(period);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        List<Map<String, Object>> hourStats = loginLogMapper.countByHour(start, end);
        Map<Integer, Integer> hourMap = new HashMap<>();
        for (int i = 0; i < 24; i++) hourMap.put(i, 0);
        if (hourStats != null) {
            for (Map<String, Object> row : hourStats) {
                int hour = ((Number) row.get("hour")).intValue();
                int count = ((Number) row.get("count")).intValue();
                hourMap.put(hour, count);
            }
        }
        List<String> xAxis = new ArrayList<>();
        List<Integer> series = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            xAxis.add(i + "点");
            series.add(hourMap.get(i));
        }
        return new ChartDataDTO.LineChart(xAxis, series);
    }

    @Override
    public List<ChartDataDTO.WordCloudItem> getWordCloud(String period) {
        LocalDateTime start = getStartTime(period);
        LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);
        Map<String, Integer> wordFreq = wordCloudService.getWordCloud(start, end, 50);
        List<ChartDataDTO.WordCloudItem> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            list.add(new ChartDataDTO.WordCloudItem(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    @Override
    public Map<String, Object> getUserStats() {
        long total = userMapper.selectCount(null);
        long normal = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "user"));
        long inheritor = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, "inheritor"));
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("normal", normal);
        result.put("inheritor", inheritor);
        return result;
    }
}