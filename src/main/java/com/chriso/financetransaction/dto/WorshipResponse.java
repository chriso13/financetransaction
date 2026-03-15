package com.chriso.financetransaction.dto;


import com.chriso.financetransaction.utility.WorshipType;

import java.time.LocalDateTime;

public record WorshipResponse(
        Long id,

        String serviceName,

        String description,

        WorshipType worshipType,

        LocalDateTime worshipDate,

        String location,

        boolean active
) {
}
