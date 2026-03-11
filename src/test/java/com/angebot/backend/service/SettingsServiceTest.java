package com.angebot.backend.service;

import com.angebot.backend.auth.jwt.JwtUtil;
import com.angebot.backend.dto.SettingsRequest;
import com.angebot.backend.dto.SettingsResponse;
import com.angebot.backend.entity.Asset;
import com.angebot.backend.entity.Settings;
import com.angebot.backend.entity.User;
import com.angebot.backend.repository.AssetRepository;
import com.angebot.backend.repository.SettingsRepository;
import com.angebot.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock private SettingsRepository settingsRepo;
    @Mock private UserRepository userRepo;
    @Mock private AssetRepository assetRepo;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private SettingsService settingsService;

    private UUID userId;
    private User user;
    private Settings settings;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setEmail("max@test.de");
        user.setPasswordHash("hash");

        settings = new Settings();
        settings.setUser(user);
        settings.setFirstName("Max");
        settings.setLastName("Mustermann");
        settings.setCompanyName("Maler GmbH");
        settings.setPhone("0711123456");
        settings.setEmail("max@maler.de");
        settings.setTaxNumber("123456");
        settings.setStreet("Bahnhofstraße");
        settings.setHouseNr("12");
        settings.setPlz("70173");
        settings.setOrt("Stuttgart");
    }

    // ─── getMySettings ────────────────────────────────────────────────────────

    @Test
    @DisplayName("getMySettings - mevcut settings döner")
    void shouldReturnExistingSettings() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(settingsRepo.findByUserId(userId)).thenReturn(Optional.of(settings));

        SettingsResponse response = settingsService.getMySettings();

        assertThat(response.firstName()).isEqualTo("Max");
        assertThat(response.companyName()).isEqualTo("Maler GmbH");
        assertThat(response.email()).isEqualTo("max@test.de"); // User'dan gelir
    }

    @Test
    @DisplayName("getMySettings - settings yoksa yeni oluşturulur")
    void shouldCreateSettingsIfNotExists() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(settingsRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(settingsRepo.save(any())).thenReturn(settings);

        SettingsResponse response = settingsService.getMySettings();

        assertThat(response).isNotNull();
        verify(settingsRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("getMySettings - user bulunamazsa exception fırlatır")
    void shouldThrowIfUserNotFound() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(settingsRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> settingsService.getMySettings())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("USER_NOT_FOUND");
    }

    // ─── updateMySettings ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateMySettings - alanlar güncellenir")
    void shouldUpdateSettings() {
        SettingsRequest req = new SettingsRequest(
                "Anna", "Schmidt", "Schmidt GmbH", null,
                "0711999", "anna@test.de", "654321",
                "Hauptstraße", "5", "70174", "Stuttgart"
        );

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(settingsRepo.findByUserId(userId)).thenReturn(Optional.of(settings));
        when(settingsRepo.save(any())).thenReturn(settings);

        SettingsResponse response = settingsService.updateMySettings(req);

        assertThat(response).isNotNull();
        verify(settingsRepo, times(1)).save(settings);
        assertThat(settings.getFirstName()).isEqualTo("Anna");
        assertThat(settings.getCompanyName()).isEqualTo("Schmidt GmbH");
    }

    // ─── uploadLogo ───────────────────────────────────────────────────────────



    @Test
    @DisplayName("uploadLogo - dosya yoksa exception fırlatır")
    void shouldThrowIfLogoFileEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "logo", "logo.png", "image/png", new byte[0]
        );

        assertThatThrownBy(() -> settingsService.uploadLogo(emptyFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("FILE_REQUIRED");
    }

    @Test
    @DisplayName("uploadLogo - izin verilmeyen format exception fırlatır")
    void shouldThrowIfLogoFormatInvalid() {
        MockMultipartFile file = new MockMultipartFile(
                "logo", "logo.pdf", "application/pdf", new byte[100]
        );

        assertThatThrownBy(() -> settingsService.uploadLogo(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ONLY_PNG_JPG_ALLOWED");
    }

    @Test
    @DisplayName("uploadLogo - 2MB üstü dosya exception fırlatır")
    void shouldThrowIfLogoTooLarge() {
        byte[] bigFile = new byte[3 * 1024 * 1024]; // 3MB
        MockMultipartFile file = new MockMultipartFile(
                "logo", "logo.png", "image/png", bigFile
        );

        assertThatThrownBy(() -> settingsService.uploadLogo(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("FILE_TOO_LARGE");
    }

    @Test
    @DisplayName("uploadSignature - 512KB üstü dosya exception fırlatır")
    void shouldThrowIfSignatureTooLarge() {
        byte[] bigFile = new byte[600 * 1024]; // 600KB
        MockMultipartFile file = new MockMultipartFile(
                "sign", "sign.png", "image/png", bigFile
        );

        assertThatThrownBy(() -> settingsService.uploadSignature(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("FILE_TOO_LARGE");
    }

    // ─── getAsset ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAsset - mevcut asset döner")
    void shouldReturnAsset() {
        UUID assetId = UUID.randomUUID();
        Asset asset = new Asset();
        asset.setContentType("image/png");

        when(assetRepo.findById(assetId)).thenReturn(Optional.of(asset));

        Asset result = settingsService.getAsset(assetId);

        assertThat(result.getContentType()).isEqualTo("image/png");
    }

    @Test
    @DisplayName("getAsset - bulunamazsa exception fırlatır")
    void shouldThrowIfAssetNotFound() {
        UUID assetId = UUID.randomUUID();
        when(assetRepo.findById(assetId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> settingsService.getAsset(assetId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ASSET_NOT_FOUND");
    }
}