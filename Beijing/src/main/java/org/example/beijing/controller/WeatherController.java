package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.WeatherDTO;
import org.example.beijing.service.WeatherService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 获取未来7天天气预报（含今天）
     * @param lat 纬度
     * @param lng 经度
     * @return 天气预报数据
     */
    @GetMapping("/forecast")
    public ResponseResult<WeatherDTO.Response> getForecast(@RequestParam Double lat, @RequestParam Double lng) {
        WeatherDTO.Response forecast = weatherService.getForecast(lat, lng);
        return ResponseResult.success(forecast);
    }
}