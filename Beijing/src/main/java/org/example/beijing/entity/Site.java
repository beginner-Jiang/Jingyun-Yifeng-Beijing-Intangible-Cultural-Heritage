package org.example.beijing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 非遗点位表（博物馆、工坊、演出场所等）
 */
@Data
@TableName("heritage_site")
public class Site {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;            // 点位名称
    private String address;         // 详细地址
    private BigDecimal longitude;   // 经度
    private BigDecimal latitude;    // 纬度
    private Long heritageItemId;    // 关联的非遗项目ID
    private String contact;         // 联系方式
    private String openHours;       // 开放时间
    private String type;            // 类型：博物馆、工坊、演出场所、工作室等
    private String description;     // 点位描述
    private String imageUrl;        // 点位图片
}