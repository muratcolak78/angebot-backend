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
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CrossOrigin("*")
public class CustomerService {
    private final CustomerRepository repo;
    private final CustomerMapper mapper;
    private final OfferRepository offerRepo;
    private final JwtUtil jwtUtil;

    public CustomerDTO createCustomer(CustomerCreateDTO dto) {
        UUID userId = jwtUtil.getCurrentUserId();
        if (repo.existsByUserIdAndEmail(userId, dto.email()))
            throw new ConflictException("There is same email");

        Customer c = mapper.toEntity(dto);
        c.setUserId(userId);
        return mapper.toDTO(repo.save(c));
    }

    public List<CustomerDTO> getMyCustomers() {
        UUID userId = jwtUtil.getCurrentUserId();
        return repo.findByUserId(userId).stream()
                .map(mapper::toDTO)
                .toList();
    }

    public CustomerDTO getCustomer(UUID id) {
        UUID userId = jwtUtil.getCurrentUserId();
        Customer customer = repo.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("No customers found"));
        return mapper.toDTO(customer);  // ← Basit!
    }

    public CustomerDTO updateCustomer(UUID id, CustomerUpdateDTO dto) {
        UUID userId = jwtUtil.getCurrentUserId();
        Customer customer = repo.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("No customers"));

        mapper.updateEntity(dto, customer);
        return mapper.toDTO(repo.save(customer));
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        UUID userId = jwtUtil.getCurrentUserId();
        Customer customer = repo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Müşteri bulunamadı"));

        // İlişkili teklifler var mı kontrol et
        boolean hasOffers = offerRepo.existsByCustomerId(id);
        if (hasOffers) {
            throw new ConflictException("Bu müşteriye ait teklifler var. Önce teklifleri silin!");
        }

        repo.delete(customer);
    }
}
