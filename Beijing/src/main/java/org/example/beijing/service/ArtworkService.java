package org.example.beijing.service;

import org.example.beijing.dto.ArtworkDTO;
import org.example.beijing.dto.ArtworkUpdateDTO;
import org.example.beijing.entity.Artwork;

import java.util.List;

public interface ArtworkService {

    void publish(Long inheritorId, boolean isInheritor, ArtworkDTO.PublishDTO dto);

    List<Artwork> getLatest(int limit);

    List<Artwork> getByInheritorId(Long inheritorId);

    void deleteByInheritorId(Long inheritorId);

    void deleteArtwork(Long artworkId, Long userId);

    void updateArtwork(Long artworkId, Long userId, ArtworkUpdateDTO dto);
}