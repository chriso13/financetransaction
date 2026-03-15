package com.chriso.financetransaction.adapter;

import com.chriso.financetransaction.dto.MemberResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;
@FeignClient(
        name = "member-service",
        url = "${member.service.url:}",
        fallback = MemberClientFallback.class
)
public interface MemberClient {

    @GetMapping("/api/v1/members/{id}")
    Object getMemberById(@PathVariable Long id);
}