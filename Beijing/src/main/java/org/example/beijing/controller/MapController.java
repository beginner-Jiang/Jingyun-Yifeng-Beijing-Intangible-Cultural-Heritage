package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.Site;
import org.example.beijing.service.SiteService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final SiteService siteService;

    /**
     * 获取所有非遗点位，支持按类型和关键词搜索
     */
    @GetMapping("/sites")
    public ResponseResult<List<Site>> getSites(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        List<Site> list = siteService.searchSites(type, keyword);
        return ResponseResult.success(list);
    }

    /**
     * 获取附近点位（基于经纬度和半径）
     */
    @GetMapping("/nearby")
    public ResponseResult<List<Site>> nearby(
            @RequestParam Double lng,
            @RequestParam Double lat,
            @RequestParam(defaultValue = "3000") Integer radius) {
        List<Site> list = siteService.findNearby(lng, lat, radius);
        return ResponseResult.success(list);
    }

    /**
     * 获取单个点位详情
     */
    @GetMapping("/site/{id}")
    public ResponseResult<Site> siteDetail(@PathVariable Long id) {
        Site site = siteService.getById(id);
        return ResponseResult.success(site);
    }
}