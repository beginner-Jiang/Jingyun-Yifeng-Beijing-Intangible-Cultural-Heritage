package org.example.beijing.service;

import org.example.beijing.dto.ChartDataDTO;
import java.util.List;
import java.util.Map;

public interface StatisticsService {
    ChartDataDTO.BarChart getHotRanking(String period);
    ChartDataDTO.PieChart getInheritorAgePie(String period);
    ChartDataDTO.RoseChart getRegionRose();
    ChartDataDTO.LineChart getUserActiveLine(String period);
    List<ChartDataDTO.WordCloudItem> getWordCloud(String period); 
    Map<String, Object> getUserStats();
}