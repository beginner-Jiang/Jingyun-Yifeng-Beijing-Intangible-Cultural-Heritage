package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.ArtworkDTO;
import org.example.beijing.dto.ArtworkUpdateDTO;
import org.example.beijing.entity.Artwork;
import org.example.beijing.service.ArtworkService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/artwork")
@RequiredArgsConstructor
public class ArtworkController {

    private final ArtworkService artworkService;

    @PostMapping("/publish")
    public ResponseResult<?> publish(@RequestBody ArtworkDTO.PublishDTO dto, HttpServletRequest request) {
        Long inheritorId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");
        boolean isInheritor = "inheritor".equals(role);
        artworkService.publish(inheritorId, isInheritor, dto);
        return ResponseResult.success("作品发布成功");
    }

    @GetMapping("/latest")
    public ResponseResult<List<Artwork>> getLatest(@RequestParam(defaultValue = "6") int limit) {
        List<Artwork> list = artworkService.getLatest(limit);
        return ResponseResult.success(list);
    }

    @GetMapping("/user/{userId}")
    public ResponseResult<List<Artwork>> getUserArtworks(@PathVariable Long userId) {
        List<Artwork> list = artworkService.getByInheritorId(userId);
        return ResponseResult.success(list);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteArtwork(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        artworkService.deleteArtwork(id, userId);
        return ResponseResult.success("删除成功");
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateArtwork(@PathVariable Long id,
                                           @RequestBody ArtworkUpdateDTO dto,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        artworkService.updateArtwork(id, userId, dto);
        return ResponseResult.success("更新成功");
    }
}