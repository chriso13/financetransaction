package com.chriso.financetransaction.adapter;

import com.chriso.financetransaction.exception.ServiceUnavailableException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MemberClientFallback implements MemberClient {

    @Override
    public Object getMemberById(Long id) {

        throw new ServiceUnavailableException(
                "Member service is currently unavailable"
        );
    }
}