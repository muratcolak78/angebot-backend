package com.angebot.backend.controller;

import com.angebot.backend.entity.Asset;
import com.angebot.backend.service.SettingsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private AssetController controller;

    @Test
    @DisplayName("GET /api/assets/{id} - asset başarıyla döner")
    void shouldReturnAsset() {
        UUID assetId = UUID.randomUUID();

        Asset asset = new Asset();
        asset.setContentType("image/png");
        asset.setFileName("logo.png");
        asset.setData(new byte[]{1, 2, 3});

        when(settingsService.getAsset(assetId)).thenReturn(asset);

        ResponseEntity<byte[]> result = controller.get(assetId);

        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getHeaders().getFirst("Content-Type")).isEqualTo("image/png");
        assertThat(result.getBody()).isEqualTo(new byte[]{1, 2, 3});
    }

    @Test
    @DisplayName("GET /api/assets/{id} - asset bulunamazsa exception fırlatır")
    void shouldThrowIfAssetNotFound() {
        UUID assetId = UUID.randomUUID();
        when(settingsService.getAsset(assetId))
                .thenThrow(new IllegalArgumentException("ASSET_NOT_FOUND"));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> controller.get(assetId)
        );
    }
}