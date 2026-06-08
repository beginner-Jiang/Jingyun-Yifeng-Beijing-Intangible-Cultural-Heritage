package org.example.beijing.service;

import org.example.beijing.dto.WeatherDTO;

public interface WeatherService {

    /**
     * 获取指定经纬度的未来7天天气预报
     * @param lat 纬度
     * @param lng 经度
     * @return 天气预报数据
     */
    WeatherDTO.Response getForecast(Double lat, Double lng);
}