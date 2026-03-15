package com.chriso.financetransaction.controller;

import com.chriso.financetransaction.dto.DashboardResponseDto;
import com.chriso.financetransaction.dto.FinanceTransactionRequest;
import com.chriso.financetransaction.dto.FinanceTransactionResponse;
import com.chriso.financetransaction.service.FinanceTransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class FinanceTransactionController {

    private final FinanceTransactionService service;

    public FinanceTransactionController(FinanceTransactionService service) {
        this.service = service;
    }

    // =========================
    // CREATE TRANSACTION
    // =========================
    @PostMapping
    public ResponseEntity<FinanceTransactionResponse> create(
            @Valid @RequestBody FinanceTransactionRequest request,
            Principal principal
    ) {
        String currentUser = principal != null ? principal.getName() : "SYSTEM";
        FinanceTransactionResponse response = service.createTransaction(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<FinanceTransactionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // =========================
    // GET ALL (PAGINATED)
    // =========================
    @GetMapping
    public ResponseEntity<Page<FinanceTransactionResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<FinanceTransactionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FinanceTransactionRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // 🚀 DASHBOARD ENDPOINTS
    // =========================

    // All dashboard (year optional)
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<DashboardResponseDto> getDashboard(
            @RequestParam(required = false) Integer year
    ) {
        int targetYear = (year != null) ? year : java.time.LocalDate.now().getYear();
        DashboardResponseDto dashboard = service.getDashboardData(targetYear);
        return ResponseEntity.ok(dashboard);
    }

    // Monthly dashboard (year optional)
    @GetMapping("/analytics/dashboard/monthly")
    public ResponseEntity<DashboardResponseDto> getMonthlyDashboard(
            @RequestParam(required = false) Integer year
    ) {
        int targetYear = (year != null) ? year : java.time.LocalDate.now().getYear();
        DashboardResponseDto dashboard = service.getDashboardData(targetYear);
        return ResponseEntity.ok(dashboard);
    }
}