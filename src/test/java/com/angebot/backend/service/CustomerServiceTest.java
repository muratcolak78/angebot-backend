package com.angebot.backend.service;

import com.angebot.backend.auth.jwt.JwtUtil;
import com.angebot.backend.config.CustomerMapper;
import com.angebot.backend.dto.CustomerCreateDTO;
import com.angebot.backend.dto.CustomerDTO;
import com.angebot.backend.dto.CustomerUpdateDTO;
import com.angebot.backend.entity.Customer;
import com.angebot.backend.exception.ConflictException;
import com.angebot.backend.exception.NotFoundException;
import com.angebot.backend.repository.CustomerRepository;
import com.angebot.backend.repository.OfferRepository;
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
class CustomerServiceTest {

    @Mock private CustomerRepository repo;
    @Mock private CustomerMapper mapper;
    @Mock private OfferRepository offerRepo;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private CustomerService customerService;

    private UUID userId;
    private UUID customerId;
    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setUserId(userId);
        customer.setFirstName("Max");
        customer.setLastName("Mustermann");
        customer.setEmail("max@test.de");

        customerDTO = new CustomerDTO();
        customerDTO.setFirstName("Max");
        customerDTO.setLastName("Mustermann");
        customerDTO.setEmail("max@test.de");
    }

    // ─── createCustomer ───────────────────────────────────────────────────────

    @Test
    @DisplayName("createCustomer - başarıyla müşteri oluşturur")
    void shouldCreateCustomer() {
        CustomerCreateDTO dto = new CustomerCreateDTO(
                "Max", "Mustermann", "max@test.de", "0711123",
                null, null, null, null, null, null, null, null
        );

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.existsByUserIdAndEmail(userId, "max@test.de")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(customer);
        when(repo.save(customer)).thenReturn(customer);
        when(mapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.createCustomer(dto);

        assertThat(result.getFirstName()).isEqualTo("Max");
        verify(repo).save(customer);
    }

    @Test
    @DisplayName("createCustomer - aynı email varsa ConflictException fırlatır")
    void shouldThrowIfEmailAlreadyExists() {
        CustomerCreateDTO dto = new CustomerCreateDTO(
                "Max", "Mustermann", "max@test.de", null,
                null, null, null, null, null, null, null, null
        );

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.existsByUserIdAndEmail(userId, "max@test.de")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(dto))
                .isInstanceOf(ConflictException.class);

        verify(repo, never()).save(any());
    }

    // ─── getMyCustomers ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getMyCustomers - müşteri listesini döner")
    void shouldReturnCustomerList() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(List.of(customer));
        when(mapper.toDTO(customer)).thenReturn(customerDTO);

        List<CustomerDTO> result = customerService.getMyCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("max@test.de");
    }

    @Test
    @DisplayName("getMyCustomers - müşteri yoksa boş liste döner")
    void shouldReturnEmptyListIfNoCustomers() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserId(userId)).thenReturn(List.of());

        List<CustomerDTO> result = customerService.getMyCustomers();

        assertThat(result).isEmpty();
    }

    // ─── getCustomer ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getCustomer - müşteriyi döner")
    void shouldReturnCustomer() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserIdAndId(userId, customerId)).thenReturn(Optional.of(customer));
        when(mapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.getCustomer(customerId);

        assertThat(result.getEmail()).isEqualTo("max@test.de");
    }

    @Test
    @DisplayName("getCustomer - bulunamazsa NotFoundException fırlatır")
    void shouldThrowIfCustomerNotFound() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserIdAndId(userId, customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomer(customerId))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── updateCustomer ───────────────────────────────────────────────────────

    @Test
    @DisplayName("updateCustomer - müşteriyi günceller")
    void shouldUpdateCustomer() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO(
                "Anna", null, null, null,
                null, null, null, null,
                null, null, null, null
        );

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserIdAndId(userId, customerId)).thenReturn(Optional.of(customer));
        when(repo.save(customer)).thenReturn(customer);
        when(mapper.toDTO(customer)).thenReturn(customerDTO);

        CustomerDTO result = customerService.updateCustomer(customerId, dto);

        assertThat(result).isNotNull();
        verify(mapper).updateEntity(dto, customer);
        verify(repo).save(customer);
    }

    @Test
    @DisplayName("updateCustomer - müşteri yoksa NotFoundException fırlatır")
    void shouldThrowIfCustomerNotFoundOnUpdate() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO(
                "Anna", null, null, null,
                null, null, null, null,
                null, null, null, null
        );

        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByUserIdAndId(userId, customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(customerId, dto))
                .isInstanceOf(NotFoundException.class);
    }

    // ─── deleteCustomer ───────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteCustomer - müşteriyi siler")
    void shouldDeleteCustomer() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByIdAndUserId(customerId, userId)).thenReturn(Optional.of(customer));
        when(offerRepo.existsByCustomerId(customerId)).thenReturn(false);

        customerService.deleteCustomer(customerId);

        verify(repo).delete(customer);
    }

    @Test
    @DisplayName("deleteCustomer - teklifler varsa ConflictException fırlatır")
    void shouldThrowIfCustomerHasOffers() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByIdAndUserId(customerId, userId)).thenReturn(Optional.of(customer));
        when(offerRepo.existsByCustomerId(customerId)).thenReturn(true);

        assertThatThrownBy(() -> customerService.deleteCustomer(customerId))
                .isInstanceOf(ConflictException.class);

        verify(repo, never()).delete(any());
    }

    @Test
    @DisplayName("deleteCustomer - müşteri yoksa NotFoundException fırlatır")
    void shouldThrowIfCustomerNotFoundOnDelete() {
        when(jwtUtil.getCurrentUserId()).thenReturn(userId);
        when(repo.findByIdAndUserId(customerId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteCustomer(customerId))
                .isInstanceOf(NotFoundException.class);
    }
}