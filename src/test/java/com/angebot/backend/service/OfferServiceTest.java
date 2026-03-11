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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock private OfferRepository offerRepo;
    @Mock private CustomerRepository customerRepo;
    @Mock private RateCardRepository rateCardRepo;
    @Mock private JwtUtil jwtUtil;
    @Mock private OfferMapper mapper;

    @InjectMocks private OfferService offerService;

    private UUID userId;
    private UUID customerId;
    private RateCard rateCard;
    private Customer customer;
    private Offer offer;
    private OfferDTO offerDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        rateCard = new RateCard();
        rateCard.setWallM2Price(10.0);
        rateCard.setWallpaperM2Price(8.0);
        rateCard.setCeilingM2Price(12.0);
        rateCard.setWindowDeductionM2(5.0);
        rateCard.setDoorDeductionM2(4.0);

        customer = new Customer();
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max@test.de");

        offer = new Offer();
        offer.setUserId(userId);
        offer.setCustomerId(customerId);
        offer.setWallM2(50.0);
        offer.setGrandTotal(500.0);

        offerDTO = new OfferDTO();
        offerDTO.setId(1L);
        offerDTO.setGrandTotal(500.0);
    }

    // ─── createOffer ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("createOffer - başarıyla teklif oluşturur")
    void shouldCreateOffer() {
        OfferCreateDTO dto = new OfferCreateDTO();
        dto.setCustomerId(customerId);
        dto.setWallM2(50.0);
        dto.setWallpaperM2(0);
        dto.setCeilingM2(0);
        dto.setWindowsM2(0);
        dto.setDoors(0);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(rateCardRepo.findByUserId(userId)).thenReturn(Optional.of(rateCard));
        when(customerRepo.findByIdAndUserId(customerId, userId)).thenReturn(Optional.of(customer));
        when(mapper.toEntity(dto)).thenReturn(offer);
        when(offerRepo.save(any())).thenReturn(offer);
        when(mapper.toDTOWithCustomer(any(), any())).thenReturn(offerDTO);

        OfferDTO result = offerService.createOffer(dto);

        assertThat(result.getGrandTotal()).isEqualTo(500.0);
        verify(offerRepo).save(any());
    }

    @Test
    @DisplayName("createOffer - rateCard yoksa NotFoundException fırlatır")
    void shouldThrowIfRateCardNotFound() {
        OfferCreateDTO dto = new OfferCreateDTO();
        dto.setCustomerId(customerId);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(rateCardRepo.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.createOffer(dto))
                .isInstanceOf(NotFoundException.class);

        verify(offerRepo, never()).save(any());
    }

    @Test
    @DisplayName("createOffer - customer yoksa NotFoundException fırlatır")
    void shouldThrowIfCustomerNotFound() {
        OfferCreateDTO dto = new OfferCreateDTO();
        dto.setCustomerId(customerId);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(rateCardRepo.findByUserId(userId)).thenReturn(Optional.of(rateCard));
        when(customerRepo.findByIdAndUserId(customerId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.createOffer(dto))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── getMyOffers ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getMyOffers - teklif listesini döner")
    void shouldReturnMyOffers() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(offer));
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(customer));
        when(mapper.toDTOWithCustomer(any(), any())).thenReturn(offerDTO);

        List<OfferDTO> result = offerService.getMyOffers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getMyOffers - teklif yoksa boş liste döner")
    void shouldReturnEmptyListIfNoOffers() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of());

        List<OfferDTO> result = offerService.getMyOffers();

        assertThat(result).isEmpty();
    }

    // ─── getOffer ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getOffer - teklifi döner")
    void shouldReturnOffer() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdAndId(userId, 1L)).thenReturn(Optional.of(offer));
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(customer));
        when(mapper.toDTOWithCustomer(any(), any())).thenReturn(offerDTO);

        OfferDTO result = offerService.getOffer(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getOffer - bulunamazsa NotFoundException fırlatır")
    void shouldThrowIfOfferNotFound() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdAndId(userId, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.getOffer(99L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── updateOffer ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateOffer - teklifi günceller")
    void shouldUpdateOffer() {
        OfferCreateDTO dto = new OfferCreateDTO();
        dto.setCustomerId(customerId);
        dto.setWallM2(60.0);
        dto.setWallpaperM2(0);
        dto.setCeilingM2(0);
        dto.setWindowsM2(0);
        dto.setDoors(0);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdAndId(userId, 1L)).thenReturn(Optional.of(offer));
        when(customerRepo.findByIdAndUserId(customerId, userId)).thenReturn(Optional.of(customer));
        when(rateCardRepo.findByUserId(userId)).thenReturn(Optional.of(rateCard));
        when(offerRepo.save(any())).thenReturn(offer);
        when(mapper.toDTOWithCustomer(any(), any())).thenReturn(offerDTO);

        OfferDTO result = offerService.updateOffer(1L, dto);

        assertThat(result).isNotNull();
        verify(offerRepo).save(offer);
    }

    @Test
    @DisplayName("updateOffer - teklif yoksa NotFoundException fırlatır")
    void shouldThrowIfOfferNotFoundOnUpdate() {
        OfferCreateDTO dto = new OfferCreateDTO();
        dto.setCustomerId(customerId);

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdAndId(userId, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.updateOffer(99L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── deleteOffer ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteOffer - teklifi siler")
    void shouldDeleteOffer() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdAndId(userId, 1L)).thenReturn(Optional.of(offer));

        offerService.deleteOffer(1L);

        verify(offerRepo).delete(offer);
    }

    @Test
    @DisplayName("deleteOffer - teklif yoksa NotFoundException fırlatır")
    void shouldThrowIfOfferNotFoundOnDelete() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(offerRepo.findByUserIdAndId(userId, 99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.deleteOffer(99L))
                .isInstanceOf(NotFoundException.class);
    }
}