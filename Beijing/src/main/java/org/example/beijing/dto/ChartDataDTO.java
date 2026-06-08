package org.example.beijing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 图表数据封装类
 */
public class ChartDataDTO {

    /**
     * 柱状图数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BarChart {
        private List<String> xAxis;     // X轴标签
        private List<Integer> series;   // 数据系列
    }

    /**
     * 饼图数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PieChart {
        private List<Map<String, Object>> data; // 每项包含 name 和 value
    }

    /**
     * 玫瑰图数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoseChart {
        private List<Map<String, Object>> data; // 每项包含 name 和 value
    }

    /**
     * 折线图数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineChart {
        private List<String> xAxis;     // X轴标签（如时段）
        private List<Integer> series;   // 数据系列
    }

    /**
     * 词云数据项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordCloudItem {
        private String name;            // 词
        private Integer value;          // 权重
    }
}