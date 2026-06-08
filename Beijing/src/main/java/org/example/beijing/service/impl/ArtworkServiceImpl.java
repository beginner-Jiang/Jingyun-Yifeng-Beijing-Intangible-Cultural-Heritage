package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.ArtworkDTO;
import org.example.beijing.dto.ArtworkUpdateDTO;
import org.example.beijing.entity.Artwork;
import org.example.beijing.mapper.ArtworkMapper;
import org.example.beijing.service.ArtworkService;
import org.example.beijing.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkMapper artworkMapper;
    private final FileUploadUtil fileUploadUtil;

    @Override
    @Transactional
    public synchronized void publish(Long inheritorId, boolean isInheritor, ArtworkDTO.PublishDTO dto) {
        if (!isInheritor) {
            throw new RuntimeException("只有传承人可以发布作品");
        }
        if (dto.getImageUrl() == null || dto.getImageUrl().isEmpty()) {
            throw new RuntimeException("作品图片不能为空");
        }

        Artwork artwork = new Artwork();
        // 手动设置ID
        Long maxId = artworkMapper.selectMaxId();
        Long newId = (maxId == null ? 1L : maxId + 1);
        artwork.setId(newId);
        artwork.setTitle(dto.getTitle());
        artwork.setDescription(dto.getDescription());
        artwork.setImageUrl(dto.getImageUrl());
        artwork.setInheritorId(inheritorId);
        artworkMapper.insert(artwork);
    }

    @Override
    public List<Artwork> getLatest(int limit) {
        return artworkMapper.selectLatest(limit);
    }

    @Override
    public List<Artwork> getByInheritorId(Long inheritorId) {
        return artworkMapper.selectByInheritorId(inheritorId);
    }

    @Override
    @Transactional
    public void deleteByInheritorId(Long inheritorId) {
        List<Artwork> artworks = artworkMapper.selectByInheritorId(inheritorId);
        for (Artwork artwork : artworks) {
            if (artwork.getImageUrl() != null) {
                fileUploadUtil.deleteFileByUrl(artwork.getImageUrl());
            }
        }
        LambdaQueryWrapper<Artwork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artwork::getInheritorId, inheritorId);
        artworkMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void deleteArtwork(Long artworkId, Long userId) {
        Artwork artwork = artworkMapper.selectById(artworkId);
        if (artwork == null) {
            throw new RuntimeException("作品不存在");
        }
        if (!artwork.getInheritorId().equals(userId)) {
            throw new RuntimeException("只能删除自己发布的作品");
        }

        if (artwork.getImageUrl() != null) {
            fileUploadUtil.deleteFileByUrl(artwork.getImageUrl());
        }

        artworkMapper.deleteById(artworkId);
    }

    @Override
    @Transactional
    public void updateArtwork(Long artworkId, Long userId, ArtworkUpdateDTO dto) {
        Artwork artwork = artworkMapper.selectById(artworkId);
        if (artwork == null) {
            throw new RuntimeException("作品不存在");
        }
        if (!artwork.getInheritorId().equals(userId)) {
            throw new RuntimeException("只能编辑自己发布的作品");
        }

        if (dto.getImageUrl() != null && !dto.getImageUrl().equals(artwork.getImageUrl())) {
            if (artwork.getImageUrl() != null) {
                fileUploadUtil.deleteFileByUrl(artwork.getImageUrl());
            }
        }

        artwork.setTitle(dto.getTitle());
        artwork.setDescription(dto.getDescription());
        artwork.setImageUrl(dto.getImageUrl());

        artworkMapper.updateById(artwork);
    }
}