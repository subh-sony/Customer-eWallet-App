package com.ewallet.transaction.controller;

import com.ewallet.transaction.dto.ApiResponse;
import com.ewallet.transaction.dto.FundTransferRequest;
import com.ewallet.transaction.dto.TransferResponse;
import com.ewallet.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Controller", description = "APIs for transaction management")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/fundTransfer")
    @Operation(summary = "Fund Transfer", description = "Transfers funds from customer account to merchant account")
    public ResponseEntity<TransferResponse> fundTransfer(@Valid @RequestBody FundTransferRequest request) {
        log.info("Received fund transfer request for customer: {}", request.getCustomerPhoneNumber());
        TransferResponse response = transactionService.fundTransfer(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get transactions by customer ID", description = "Retrieves all transactions for a customer")
    public ResponseEntity<ApiResponse> getTransactions(@PathVariable Long customerId) {
        log.info("Received get transactions request for customer ID: {}", customerId);
        try {
            var transactions = transactionService.getTransactionsByCustomerId(customerId);
            return ResponseEntity.ok(new ApiResponse("Transactions retrieved successfully", true, transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), false, null));
        }
    }
}

