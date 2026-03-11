package com.angebot.backend.repository;

import com.angebot.backend.entity.Asset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AssetRepositoryTest {

    @Autowired
    private AssetRepository assetRepository;

    @Test
    @DisplayName("Should save asset and find it by id")
    void shouldFindAssetById() {
        Asset asset = new Asset();
        asset.setFileName("test.pdf");
        asset.setContentType("application/pdf");

        asset.setData(new byte[]{1, 2, 3});

        Asset savedAsset = assetRepository.save(asset);

        Optional<Asset> result = assetRepository.findById(savedAsset.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getFileName()).isEqualTo("test.pdf");
        assertThat(result.get().getContentType()).isEqualTo("application/pdf");

    }

    @Test
    @DisplayName("Should return empty when asset not found")
    void shouldReturnEmptyWhenAssetNotFound() {
        UUID randomId = UUID.randomUUID();

        Optional<Asset> result = assetRepository.findById(randomId);

        assertThat(result).isEmpty();
    }
}