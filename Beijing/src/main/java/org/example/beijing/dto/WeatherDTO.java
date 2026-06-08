package org.example.beijing.dto;

import lombok.Data;
import java.util.List;

public class WeatherDTO {

    @Data
    public static class HourlyData {
        private String time;
        private Double temperature;
        private Integer humidity;
        private Double apparentTemperature;
        private Double precipitation;
        private Integer weatherCode;
        private Double windSpeed;
        private Integer windDirection;
    }

    @Data
    public static class Daily {
        private String date;
        private String weatherDesc;
        private Integer weatherCode;
        private Double tempMax;
        private Double tempMin;
        private Double uvIndex;
        private Double precipitation;
        private Double windSpeed;
        private Integer windDirection;
        private String sunrise;
        private String sunset;
        private Double apparentTemperature;
        private Integer humidity;
        private Double pressure;
        private Double visibility;
        private List<HourlyData> hourly;
    }

    @Data
    public static class Response {
        private List<Daily> daily;
    }
}