package com.angebot.backend.service;

import com.angebot.backend.auth.jwt.JwtUtil; // JwtUtil kullan
import com.angebot.backend.dto.SettingsRequest;
import com.angebot.backend.dto.SettingsResponse;
import com.angebot.backend.entity.Asset;
import com.angebot.backend.entity.Settings;
import com.angebot.backend.entity.User;
import com.angebot.backend.repository.AssetRepository;
import com.angebot.backend.repository.SettingsRepository;
import com.angebot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingsService {

    private static final long MAX_LOGO_BYTES = 2 * 1024 * 1024;
    private static final long MAX_SIGN_BYTES = 512 * 1024;
    private static final Set<String> ALLOWED_IMG = Set.of(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE
    );

    private final SettingsRepository settingsRepo;
    private final UserRepository userRepo;
    private final AssetRepository assetRepo;
    private final JwtUtil jwtUtil; // CurrentUser yerine JwtUtil

    public SettingsResponse getMySettings() {
        UUID userId = jwtUtil.getCurrentUserId(); // JWT'den userId al
        Settings s = getOrCreateForUser(userId);
        return toResponse(s);
    }

    public SettingsResponse updateMySettings(SettingsRequest req) {
        UUID userId = jwtUtil.getCurrentUserId(); // JWT'den userId al
        Settings s = getOrCreateForUser(userId);

        s.setFirstName(req.firstName());
        s.setLastName(req.lastName());
        s.setCompanyName(req.companyName());
        s.setStreet(req.street());
        s.setHouseNr(req.houseNr());
        s.setPlz(req.plz());
        s.setOrt(req.ort());
        s.setPhone(req.phone());
        s.setTaxNumber(req.taxNumber());
        s.setEmail(req.email());

        settingsRepo.save(s);
        return toResponse(s);
    }

    public UUID uploadLogo(MultipartFile file) throws IOException {
        return uploadImageAndAttach(file, true);
    }

    public UUID uploadSignature(MultipartFile file) throws IOException {
        return uploadImageAndAttach(file, false);
    }

    public Asset getAsset(UUID id) {
        return assetRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ASSET_NOT_FOUND"));
    }

    private UUID uploadImageAndAttach(MultipartFile file, boolean isLogo) throws IOException {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("FILE_REQUIRED");

        if (file.getContentType() == null || !ALLOWED_IMG.contains(file.getContentType())) {
            throw new IllegalArgumentException("ONLY_PNG_JPG_ALLOWED");
        }

        long max = isLogo ? MAX_LOGO_BYTES : MAX_SIGN_BYTES;
        if (file.getSize() > max)
            throw new IllegalArgumentException("FILE_TOO_LARGE");

        Asset a = new Asset();
        a.setContentType(file.getContentType());
        a.setFileName(file.getOriginalFilename());
        a.setData(file.getBytes());

        Asset saved = assetRepo.save(a);

        UUID userId = jwtUtil.getCurrentUserId();
        Settings s = getOrCreateForUser(userId);

        if (isLogo)
            s.setLogoAssetId(saved.getId());
        else
            s.setSignatureAssetId(saved.getId());

        settingsRepo.save(s);
        return saved.getId();
    }

    private Settings getOrCreateForUser(UUID userId) {
        return settingsRepo.findByUserId(userId)  // userId ile ara
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));

                    Settings s = new Settings();
                    s.setUser(user);
                    s.setCompanyName("");
                    return settingsRepo.save(s);
                });
    }

    private static SettingsResponse toResponse(Settings s) {
        return new SettingsResponse(
                s.getFirstName(),
                s.getLastName(),
                s.getCompanyName(),
                s.getPhone(),
                s.getUser().getEmail(),  // User'dan email al
                s.getTaxNumber(),
                s.getStreet(),
                s.getHouseNr(),
                s.getPlz(),
                s.getOrt(),
                s.getLogoAssetId(),
                s.getSignatureAssetId()
        );
    }
}