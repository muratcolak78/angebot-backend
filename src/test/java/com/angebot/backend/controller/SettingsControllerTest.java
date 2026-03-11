package com.angebot.backend.controller;

import com.angebot.backend.dto.SettingsRequest;
import com.angebot.backend.dto.SettingsResponse;
import com.angebot.backend.service.SettingsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

    @Mock
    private SettingsService service;

    @InjectMocks
    private SettingsController controller;

    private SettingsResponse buildResponse() {
        return new SettingsResponse(
                "Max", "Mustermann", "Maler GmbH",
                "0711123", "max@test.de", "123456",
                "Bahnhofstraße", "12", "70173", "Stuttgart",
                null, null
        );
    }

    @Test
    @DisplayName("GET /api/settings/me - settings döner")
    void shouldReturnSettings() {
        when(service.getMySettings()).thenReturn(buildResponse());

        SettingsResponse result = controller.getMe();

        assertThat(result.firstName()).isEqualTo("Max");
        assertThat(result.companyName()).isEqualTo("Maler GmbH");
    }

    @Test
    @DisplayName("PUT /api/settings/me - settings günceller")
    void shouldUpdateSettings() {
        SettingsRequest req = new SettingsRequest(
                "Anna", "Schmidt", "Schmidt GmbH", null,
                "0711999", "anna@test.de", "654321",
                "Hauptstraße", "5", "70174", "Stuttgart"
        );

        when(service.updateMySettings(any())).thenReturn(buildResponse());

        SettingsResponse result = controller.updateMe(req);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Max");
    }

    @Test
    @DisplayName("POST /api/settings/me/logo - logo yükler")
    void shouldUploadLogo() throws Exception {
        UUID assetId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "logo.png", "image/png", new byte[100]
        );

        when(service.uploadLogo(any())).thenReturn(assetId);

        Map<String, UUID> result = controller.uploadLogo(file);

        assertThat(result.get("assetId")).isEqualTo(assetId);
    }

    @Test
    @DisplayName("POST /api/settings/me/signature - imza yükler")
    void shouldUploadSignature() throws Exception {
        UUID assetId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile(
                "file", "sign.png", "image/png", new byte[100]
        );

        when(service.uploadSignature(any())).thenReturn(assetId);

        Map<String, UUID> result = controller.uploadSignature(file);

        assertThat(result.get("assetId")).isEqualTo(assetId);
    }
}