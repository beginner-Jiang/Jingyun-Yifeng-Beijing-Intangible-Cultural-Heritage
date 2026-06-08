package org.example.beijing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.dto.WeatherDTO;
import org.example.beijing.service.WeatherService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;

    private static final String OPEN_METEO_URL = "https://api.open-meteo.com/v1/forecast";

    private static final Map<Integer, String> WEATHER_CODE_MAP = Map.ofEntries(
            Map.entry(0, "晴朗"),
            Map.entry(1, "大部晴"),
            Map.entry(2, "多云转晴"),
            Map.entry(3, "多云"),
            Map.entry(45, "雾"),
            Map.entry(48, "雾凇"),
            Map.entry(51, "小雨"),
            Map.entry(53, "中雨"),
            Map.entry(55, "大雨"),
            Map.entry(56, "冻小雨"),
            Map.entry(57, "冻大雨"),
            Map.entry(61, "小雨"),
            Map.entry(63, "中雨"),
            Map.entry(65, "大雨"),
            Map.entry(66, "冻小雨"),
            Map.entry(67, "冻大雨"),
            Map.entry(71, "小雪"),
            Map.entry(73, "中雪"),
            Map.entry(75, "大雪"),
            Map.entry(77, "雪粒"),
            Map.entry(80, "小雨"),
            Map.entry(81, "中雨"),
            Map.entry(82, "大雨"),
            Map.entry(85, "小雪"),
            Map.entry(86, "大雪"),
            Map.entry(95, "雷暴"),
            Map.entry(96, "雷暴伴有小雨"),
            Map.entry(99, "雷暴伴有大雨")
    );

    @Override
    public WeatherDTO.Response getForecast(Double lat, Double lng) {
        String url = UriComponentsBuilder.fromHttpUrl(OPEN_METEO_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lng)
                .queryParam("daily", "weathercode,temperature_2m_max,temperature_2m_min,uv_index_max,precipitation_sum,windspeed_10m_max,winddirection_10m_dominant,sunrise,sunset")
                .queryParam("hourly", "temperature_2m,relativehumidity_2m,apparent_temperature,precipitation,weathercode,windspeed_10m,winddirection_10m")
                .queryParam("timezone", "auto")
                .queryParam("forecast_days", "7")
                .build()
                .toUriString();

        log.info("调用 open-meteo API: {}", url);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("daily") || !response.containsKey("hourly")) {
            throw new RuntimeException("获取天气信息失败");
        }

        // 解析每日数据
        Map<String, Object> daily = (Map<String, Object>) response.get("daily");
        List<String> times = (List<String>) daily.get("time");
        List<Integer> weatherCodes = (List<Integer>) daily.get("weathercode");
        List<Double> tempMax = (List<Double>) daily.get("temperature_2m_max");
        List<Double> tempMin = (List<Double>) daily.get("temperature_2m_min");
        List<Double> uvIndex = (List<Double>) daily.get("uv_index_max");
        List<Double> precip = (List<Double>) daily.get("precipitation_sum");
        List<Double> windSpeed = (List<Double>) daily.get("windspeed_10m_max");
        List<Integer> windDir = (List<Integer>) daily.get("winddirection_10m_dominant");
        List<String> sunrise = (List<String>) daily.get("sunrise");
        List<String> sunset = (List<String>) daily.get("sunset");

        // 解析小时数据
        Map<String, Object> hourly = (Map<String, Object>) response.get("hourly");
        List<String> hourlyTimes = (List<String>) hourly.get("time");
        List<Double> hourlyTemp = (List<Double>) hourly.get("temperature_2m");
        List<Integer> hourlyHumidity = (List<Integer>) hourly.get("relativehumidity_2m");
        List<Double> hourlyApparentTemp = (List<Double>) hourly.get("apparent_temperature");
        List<Double> hourlyPrecip = (List<Double>) hourly.get("precipitation");
        List<Integer> hourlyWeatherCode = (List<Integer>) hourly.get("weathercode");
        List<Double> hourlyWindSpeed = (List<Double>) hourly.get("windspeed_10m");
        List<Integer> hourlyWindDir = (List<Integer>) hourly.get("winddirection_10m");

        // 构建每日详细数据，并包含当天的小时数据
        List<WeatherDTO.Daily> dailyList = new ArrayList<>();
        for (int i = 0; i < times.size(); i++) {
            WeatherDTO.Daily d = new WeatherDTO.Daily();
            d.setDate(times.get(i));
            d.setWeatherCode(weatherCodes.get(i));
            d.setWeatherDesc(WEATHER_CODE_MAP.getOrDefault(weatherCodes.get(i), "未知"));
            d.setTempMax(tempMax.get(i));
            d.setTempMin(tempMin.get(i));
            d.setUvIndex(uvIndex.get(i));
            d.setPrecipitation(precip.get(i));
            d.setWindSpeed(windSpeed.get(i));
            d.setWindDirection(windDir.get(i));
            d.setSunrise(sunrise.get(i));
            d.setSunset(sunset.get(i));

            // 提取当天小时数据（0点到23点）
            String targetDate = times.get(i);
            List<WeatherDTO.HourlyData> hourlyList = new ArrayList<>();
            for (int h = 0; h < hourlyTimes.size(); h++) {
                if (hourlyTimes.get(h).startsWith(targetDate)) {
                    WeatherDTO.HourlyData hd = new WeatherDTO.HourlyData();
                    hd.setTime(hourlyTimes.get(h).substring(11, 16)); // "HH:MM"
                    hd.setTemperature(hourlyTemp.get(h));
                    hd.setHumidity(hourlyHumidity.get(h));
                    hd.setApparentTemperature(hourlyApparentTemp.get(h));
                    hd.setPrecipitation(hourlyPrecip.get(h));
                    hd.setWeatherCode(hourlyWeatherCode.get(h));
                    hd.setWindSpeed(hourlyWindSpeed.get(h));
                    hd.setWindDirection(hourlyWindDir.get(h));
                    hourlyList.add(hd);
                }
            }
            d.setHourly(hourlyList);

            // 取中午12点的数据作为当日代表（用于概览）
            for (WeatherDTO.HourlyData hd : hourlyList) {
                if (hd.getTime().equals("12:00")) {
                    d.setApparentTemperature(hd.getApparentTemperature());
                    d.setHumidity(hd.getHumidity());
                    d.setPressure(1013.0); // 无气压数据，默认值
                    d.setVisibility(10.0);  // 无能见度数据，默认值
                    break;
                }
            }

            dailyList.add(d);
        }

        WeatherDTO.Response result = new WeatherDTO.Response();
        result.setDaily(dailyList);
        return result;
    }
}