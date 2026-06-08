package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.HeritageItem;
import org.example.beijing.service.HeritageItemService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heritage")
@RequiredArgsConstructor
public class HeritageController {

    private final HeritageItemService heritageItemService;

    /**
     * 获取非遗项目列表，可按类别筛选
     */
    @GetMapping("/list")
    public ResponseResult<List<HeritageItem>> list(@RequestParam(required = false) String category) {
        List<HeritageItem> list = heritageItemService.listByCategory(category);
        return ResponseResult.success(list);
    }

    /**
     * 获取非遗项目详情
     */
    @GetMapping("/{id}")
    public ResponseResult<HeritageItem> detail(@PathVariable Long id) {
        HeritageItem item = heritageItemService.getDetail(id);
        return ResponseResult.success(item);
    }

    /**
     * 获取所有非遗类别（用于前端分类标签）
     */
    @GetMapping("/categories")
    public ResponseResult<List<String>> categories() {
        List<String> categories = heritageItemService.getAllCategories();
        return ResponseResult.success(categories);
    }
}