package com.angebot.backend.dto;

import jakarta.validation.constraints.NotNull;

public record OfferEmailRequest(
        @NotNull Long offerId
) {}