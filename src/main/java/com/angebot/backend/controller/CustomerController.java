package com.angebot.backend.controller;

import com.angebot.backend.dto.CustomerCreateDTO;
import com.angebot.backend.dto.CustomerDTO;
import com.angebot.backend.dto.CustomerUpdateDTO;
import com.angebot.backend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService service;

    @PostMapping  // Yeni müşteri ekle
    public CustomerDTO create(@Valid @RequestBody CustomerCreateDTO dto) {
        return service.createCustomer(dto);
    }

    @GetMapping("/me")  // Benim müşterilerim
    public List<CustomerDTO> getMyCustomers() {
        return service.getMyCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomer(@PathVariable UUID id) {
        return service.getCustomer(id);
    }

    @PutMapping("/{id}")
    public CustomerDTO updateCustomer(@PathVariable UUID id, 
                                    @Valid @RequestBody CustomerUpdateDTO dto) {
        return service.updateCustomer(id, dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
