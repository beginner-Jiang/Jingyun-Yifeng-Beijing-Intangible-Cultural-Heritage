package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.service.FootprintService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/footprint")
@RequiredArgsConstructor
public class FootprintController {

    private final FootprintService footprintService;

    /**
     * 获取当前用户的浏览足迹
     * @param limit 最多返回条数，默认30
     * @param request 请求
     * @return 足迹列表
     */
    @GetMapping("/list")
    public ResponseResult<List<Map<String, Object>>> list(
            @RequestParam(defaultValue = "30") int limit,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Map<String, Object>> list = footprintService.getUserFootprints(userId, limit);
        return ResponseResult.success(list);
    }
}