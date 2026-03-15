package com.chriso.financetransaction.adapter;

import com.chriso.financetransaction.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorshipClientFallback implements WorshipClient {

    @Override
    public Object getWorshipById(Long id) {

        throw new ServiceUnavailableException(
                "Worship service is currently unavailable"
        );
    }
}