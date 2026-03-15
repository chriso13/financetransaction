package com.chriso.financetransaction.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "worship-service",
        url = "${worship.service.url:}",
        fallback = WorshipClientFallback.class
)
public interface WorshipClient {

    @GetMapping("/api/v1/worship/{id}")
    Object getWorshipById(@PathVariable Long id);
}