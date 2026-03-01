package com.angebot.backend.config;

import com.angebot.backend.dto.OfferCreateDTO;
import com.angebot.backend.dto.OfferDTO;
import com.angebot.backend.entity.Customer;
import com.angebot.backend.entity.Offer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OfferMapper {


    public OfferDTO toDTO(Offer entity) {
        OfferDTO dto = new OfferDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }


    public OfferDTO toDTOWithCustomer(Offer entity, Customer customer) {
        OfferDTO dto = toDTO(entity); // Mevcut metodu kullan

        if (customer != null) {
            dto.setCustomerFirstName(customer.getFirstName());
            dto.setCustomerLastName(customer.getLastName());
            dto.setCustomerEmail(customer.getEmail());
            dto.setCustomerPhone(customer.getPhone());
        }

        return dto;
    }

    public Offer toEntity(OfferCreateDTO dto) {
        Offer offer = new Offer();
        BeanUtils.copyProperties(dto, offer);
        return offer;
    }
}