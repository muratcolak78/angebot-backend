package com.angebot.backend.service;

import com.angebot.backend.auth.jwt.JwtUtil;
import com.angebot.backend.config.OfferMapper;
import com.angebot.backend.dto.OfferCreateDTO;
import com.angebot.backend.dto.OfferDTO;
import com.angebot.backend.entity.Customer;
import com.angebot.backend.entity.Offer;
import com.angebot.backend.entity.RateCard;
import com.angebot.backend.exception.NotFoundException;
import com.angebot.backend.repository.CustomerRepository;
import com.angebot.backend.repository.OfferRepository;
import com.angebot.backend.repository.RateCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OfferService {
    private final OfferRepository offerRepo;
    private final CustomerRepository customerRepo;
    private final RateCardRepository rateCardRepo;
    private final JwtUtil jwtUtil;
    private final OfferMapper mapper;

    @Transactional
    public OfferDTO createOffer(OfferCreateDTO dto) {
        UUID userId = jwtUtil.getCurrentUserId();

        // 1. RateCard al (user'ın fiyatları)
        RateCard rateCard = rateCardRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Fiyat tablosu yok"));

        // 2. Customer validate ve bilgilerini al
        Customer customer = customerRepo.findByIdAndUserId(dto.getCustomerId(), userId)
                .orElseThrow(() -> new NotFoundException("Müşteri yok"));

        // 3. Yeni Offer + Measurements
        Offer offer = mapper.toEntity(dto);
        offer.setUserId(userId);

        // 4. RateCard × Measurements = Totals
        offer.setWallTotal(dto.getWallM2() * rateCard.getWallM2Price());
        offer.setWallpaperTotal(dto.getWallpaperM2() * rateCard.getWallpaperM2Price());
        offer.setCeilingTotal(dto.getCeilingM2() * rateCard.getCeilingM2Price());
        offer.setWindowDeduction(dto.getWindowsM2() * rateCard.getWindowDeductionM2());
        offer.setDoorDeduction(dto.getDoors() * rateCard.getDoorDeductionM2());

        // 5. Grand Total
        offer.setGrandTotal(
                offer.getWallTotal() +
                        offer.getWallpaperTotal() +
                        offer.getCeilingTotal() -
                        offer.getWindowDeduction() -
                        offer.getDoorDeduction()
        );

        Offer saved = offerRepo.save(offer);

        // DEĞİŞİKLİK: Customer bilgileriyle birlikte DTO döndür
        return mapper.toDTOWithCustomer(saved, customer);
    }

    // DEĞİŞİKLİK: getMyOffers - artık müşteri bilgileriyle birlikte dönüyor
    public List<OfferDTO> getMyOffers() {
        UUID userId = jwtUtil.getCurrentUserId();

        // Tarihe göre sıralı getir (en yeni en üstte)
        List<Offer> offers = offerRepo.findByUserIdOrderByCreatedAtDesc(userId);

        return offers.stream()
                .map(offer -> {
                    // Her offer için customer'ı bul
                    Customer customer = customerRepo.findById(offer.getCustomerId()).orElse(null);
                    return mapper.toDTOWithCustomer(offer, customer);
                })
                .collect(Collectors.toList());
    }

    // DEĞİŞİKLİK: getOffer - müşteri bilgileriyle birlikte dön
    public OfferDTO getOffer(Long id) {
        UUID userId = jwtUtil.getCurrentUserId();
        Offer offer = offerRepo.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("Teklif yok"));

        Customer customer = customerRepo.findById(offer.getCustomerId()).orElse(null);
        return mapper.toDTOWithCustomer(offer, customer);
    }

    @Transactional
    public OfferDTO updateOffer(Long id, OfferCreateDTO dto) {
        UUID userId = jwtUtil.getCurrentUserId();

        // 1. Offer + Owner validate
        Offer offer = offerRepo.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("Teklif bulunamadı"));

        // 2. Customer validate ve bilgilerini al
        Customer customer = customerRepo.findByIdAndUserId(dto.getCustomerId(), userId)
                .orElseThrow(() -> new NotFoundException("Müşteri yok"));

        // 3. RateCard (fiyatlar değişmedi)
        RateCard rateCard = rateCardRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Fiyat tablosu yok"));

        // 4. Update measurements
        offer.setCustomerId(dto.getCustomerId());
        offer.setWallM2(dto.getWallM2());
        offer.setWallpaperM2(dto.getWallpaperM2());
        offer.setCeilingM2(dto.getCeilingM2());
        offer.setWindowsM2(dto.getWindowsM2());
        offer.setDoors(dto.getDoors());

        // 5. Recalculate totals
        offer.setWallTotal(dto.getWallM2() * rateCard.getWallM2Price());
        offer.setWallpaperTotal(dto.getWallpaperM2() * rateCard.getWallpaperM2Price());
        offer.setCeilingTotal(dto.getCeilingM2() * rateCard.getCeilingM2Price());
        offer.setWindowDeduction(dto.getWindowsM2() * rateCard.getWindowDeductionM2());
        offer.setDoorDeduction(dto.getDoors() * rateCard.getDoorDeductionM2());
        offer.setGrandTotal(
                offer.getWallTotal() + offer.getWallpaperTotal() + offer.getCeilingTotal() -
                        offer.getWindowDeduction() - offer.getDoorDeduction()
        );

        Offer saved = offerRepo.save(offer);

        // DEĞİŞİKLİK: Customer bilgileriyle birlikte DTO döndür
        return mapper.toDTOWithCustomer(saved, customer);
    }

    // YENİ: Delete metodu ekleyelim
    @Transactional
    public void deleteOffer(Long id) {
        UUID userId = jwtUtil.getCurrentUserId();
        Offer offer = offerRepo.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("Teklif bulunamadı"));
        offerRepo.delete(offer);
    }
}