package com.angebot.backend.config;

import com.angebot.backend.dto.CustomerCreateDTO;
import com.angebot.backend.dto.CustomerDTO;
import com.angebot.backend.dto.CustomerUpdateDTO;
import com.angebot.backend.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
    
    public CustomerDTO toDTO(Customer entity) {
        CustomerDTO dto = new CustomerDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
    
    public Customer toEntity(CustomerCreateDTO dto) {
        Customer c = new Customer();
        BeanUtils.copyProperties(dto, c);
        return c;
    }

    public void updateEntity(CustomerUpdateDTO dto, Customer entity) {

        // ---- Basic fields ----
        if (dto.firstName() != null) entity.setFirstName(dto.firstName());
        if (dto.lastName() != null) entity.setLastName(dto.lastName());
        if (dto.email() != null) entity.setEmail(dto.email());
        if (dto.phone() != null) entity.setPhone(dto.phone());

        // ---- Home Address ----
        if (dto.homeStreet() != null) entity.setHomeStreet(dto.homeStreet());
        if (dto.homeHouseNr() != null) entity.setHomeHouseNr(dto.homeHouseNr());
        if (dto.homePlz() != null) entity.setHomePlz(dto.homePlz());
        if (dto.homeOrt() != null) entity.setHomeOrt(dto.homeOrt());

        // ---- Work Address (optional) ----
        if (dto.workStreet() != null) entity.setWorkStreet(dto.workStreet());
        if (dto.workHouseNr() != null) entity.setWorkHouseNr(dto.workHouseNr());
        if (dto.workPlz() != null) entity.setWorkPlz(dto.workPlz());
        if (dto.workOrt() != null) entity.setWorkOrt(dto.workOrt());

        // ---- Update timestamp ----
        entity.setUpdatedAt(Instant.now());
    }

}
