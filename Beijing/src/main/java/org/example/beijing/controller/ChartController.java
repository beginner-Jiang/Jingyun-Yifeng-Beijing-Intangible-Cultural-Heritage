package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.ChartDataDTO;
import org.example.beijing.service.StatisticsService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chart")
@RequiredArgsConstructor
public class ChartController {

    private final StatisticsService statisticsService;

    @GetMapping("/hot")
    public ResponseResult<ChartDataDTO.BarChart> hotRanking(@RequestParam(defaultValue = "month") String period) {
        ChartDataDTO.BarChart data = statisticsService.getHotRanking(period);
        return ResponseResult.success(data);
    }

    @GetMapping("/age")
    public ResponseResult<ChartDataDTO.PieChart> inheritorAge(@RequestParam(defaultValue = "month") String period) {
        ChartDataDTO.PieChart data = statisticsService.getInheritorAgePie(period);
        return ResponseResult.success(data);
    }

    @GetMapping("/region")
    public ResponseResult<ChartDataDTO.RoseChart> regionDistribution() {
        ChartDataDTO.RoseChart data = statisticsService.getRegionRose();
        return ResponseResult.success(data);
    }

    @GetMapping("/active")
    public ResponseResult<ChartDataDTO.LineChart> userActive(@RequestParam(defaultValue = "month") String period) {
        ChartDataDTO.LineChart data = statisticsService.getUserActiveLine(period);
        return ResponseResult.success(data);
    }

    @GetMapping("/wordcloud")
    public ResponseResult<List<ChartDataDTO.WordCloudItem>> wordCloud(@RequestParam(defaultValue = "month") String period) {
        List<ChartDataDTO.WordCloudItem> data = statisticsService.getWordCloud(period);
        return ResponseResult.success(data);
    }

    @GetMapping("/user-stats")
    public ResponseResult<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = statisticsService.getUserStats();
        return ResponseResult.success(stats);
    }
}