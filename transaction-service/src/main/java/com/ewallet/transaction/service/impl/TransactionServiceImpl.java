package com.ewallet.transaction.service.impl;

import com.ewallet.transaction.dto.*;
import com.ewallet.transaction.entity.Transaction;
import com.ewallet.transaction.entity.TransactionStatus;
import com.ewallet.transaction.repository.TransactionRepository;
import com.ewallet.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Value("${customer.service.url:http://localhost:8081}")
    private String customerServiceUrl;

    @Value("${merchant.service.url:http://localhost:8084}")
    private String merchantServiceUrl;

    @Transactional
    public TransferResponse fundTransfer(FundTransferRequest request) {
        log.info("Initiating fund transfer for customer: {}", request.getCustomerPhoneNumber());

        CustomerResponse customer = getCustomerDetails(request.getCustomerPhoneNumber());
        if (customer == null) {
            log.error("Customer not found for phone number: {}", request.getCustomerPhoneNumber());
            return new TransferResponse("Customer not found", false, null);
        }

        MerchantResponse merchant = getMerchantDetails(request.getMerchantId());
        if (merchant == null) {
            log.error("Merchant not found for merchant ID: {}", request.getMerchantId());
            return new TransferResponse("Merchant not found", false, null);
        }

        CustomerAccountResponse customerAccount = getCustomerAccount(request.getFromAccountNumber());
        if (customerAccount == null) {
            log.error("Customer account not found: {}", request.getFromAccountNumber());
            return new TransferResponse("Customer account not found", false, null);
        }

        if (customerAccount.getBalance().compareTo(request.getAmount()) < 0) {
            log.error("Insufficient balance. Available: {}, Required: {}",
                    customerAccount.getBalance(), request.getAmount());
            return new TransferResponse("Insufficient balance", false, null);
        }

        Transaction transaction = Transaction.builder()
                .customerId(customer.getId())
                .fromAccountNumber(request.getFromAccountNumber())
                .toAccountNumber(merchant.getMerchantAccountNumber())
                .amount(request.getAmount())
                .status(TransactionStatus.IN_PROGRESS)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {} and status: IN_PROGRESS", transaction.getId());

        try {
            UpdateBalanceRequest debitRequest = new UpdateBalanceRequest(
                    request.getFromAccountNumber(),
                    request.getAmount().negate()
            );
            debitFromCustomerAccount(debitRequest);
            log.info("Amount debited from customer account: {}", request.getFromAccountNumber());

            creditToMerchant(merchant.getMerchantAccountNumber(), request.getAmount());
            log.info("Amount credited to merchant account: {}", merchant.getMerchantAccountNumber());

            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setUpdatedDate(LocalDateTime.now());
            transactionRepository.save(transaction);
            log.info("Transaction status updated to PENDING for transaction ID: {}", transaction.getId());

            simulateSettlement(transaction);
            FundTransferResponse fundTransferResponse = new FundTransferResponse();
            fundTransferResponse.setAmount(transaction.getAmount());
            fundTransferResponse.setStatus(transaction.getStatus());
            fundTransferResponse.setCreatedDate(transaction.getCreatedDate());
            fundTransferResponse.setUpdatedDate(transaction.getUpdatedDate());
            fundTransferResponse.setTransactionId(transaction.getId());
            fundTransferResponse.setFromAccountNumber(transaction.getFromAccountNumber());
            fundTransferResponse.setToAccountNumber(transaction.getToAccountNumber());

            return new TransferResponse("Fund transfer completed successfully", true, fundTransferResponse);

        } catch (Exception e) {
            log.error("Error during fund transfer: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setUpdatedDate(LocalDateTime.now());
            transactionRepository.save(transaction);
            return new TransferResponse("Fund transfer failed: " + e.getMessage(), false, null);
        }
    }

    private CustomerResponse getCustomerDetails(String phoneNumber) {
        try {
            String url = customerServiceUrl + "/customer/" + phoneNumber;
            ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);
            if (response != null && response.isSuccess() && response.getData() != null) {
                // Convert Map to CustomerResponse
                if (response.getData() instanceof java.util.Map) {
                    java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
                    CustomerResponse customer = new CustomerResponse();
                    customer.setId(Long.valueOf(data.get("id").toString()));
                    customer.setName(data.get("name").toString());
                    customer.setPhoneNumber(data.get("phoneNumber").toString());
                    customer.setEmail(data.get("email").toString());
                    return customer;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching customer details: {}", e.getMessage());
        }
        return null;
    }

    private CustomerAccountResponse getCustomerAccount(String accountNumber) {
        try {
            String url = customerServiceUrl + "/customer/account/" + accountNumber;
            ApiResponse response = restTemplate.getForObject(url, ApiResponse.class);
            if (response != null && response.isSuccess() && response.getData() != null) {
                // Convert Map to CustomerAccountResponse
                if (response.getData() instanceof java.util.Map) {
                    java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
                    CustomerAccountResponse account = new CustomerAccountResponse();
                    account.setId(Long.valueOf(data.get("id").toString()));
                    account.setCustomerId(Long.valueOf(data.get("customerId").toString()));
                    account.setAccountNumber(data.get("accountNumber").toString());
                    account.setBalance(new java.math.BigDecimal(data.get("balance").toString()));
                    account.setCurrency(data.get("currency").toString());
                    return account;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching customer account: {}", e.getMessage());
        }
        return null;
    }

    private MerchantResponse getMerchantDetails(String merchantId) {
        try {
            String url = merchantServiceUrl + "/merchant/" + merchantId;
            MerchantResponse response = restTemplate.getForObject(url, MerchantResponse.class);
            return response;
        } catch (Exception e) {
            log.error("Error fetching merchant details: {}", e.getMessage());
        }
        return null;
    }

    private void debitFromCustomerAccount(UpdateBalanceRequest request) {
        try {
            String url = customerServiceUrl + "/customer/update-balance";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UpdateBalanceRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.postForObject(url, entity, ApiResponse.class);
        } catch (Exception e) {
            log.error("Error debiting from customer account: {}", e.getMessage());
            throw new RuntimeException("Failed to debit from customer account", e);
        }
    }

    private void creditToMerchant(String merchantAccountNumber, BigDecimal amount) {
        try {
            String url = merchantServiceUrl + "/merchant/credit";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            CreditRequest creditRequest = new CreditRequest(merchantAccountNumber, amount);
            HttpEntity<CreditRequest> entity = new HttpEntity<>(creditRequest, headers);
            restTemplate.postForObject(url, entity, ApiResponse.class);
            log.info("Amount credited to merchant account: {}", merchantAccountNumber);
        } catch (Exception e) {
            log.error("Error crediting to merchant account: {}", e.getMessage());
            throw new RuntimeException("Failed to credit to merchant account", e);
        }
    }

    private void simulateSettlement(Transaction transaction) {
        // Simulate settlement process
        // In production, this would be handled by a scheduled job or external system
        log.info("Simulating settlement for transaction ID: {}", transaction.getId());
        transaction.setStatus(TransactionStatus.AUTHORIZED);
        transaction.setUpdatedDate(LocalDateTime.now());
        transactionRepository.save(transaction);
        log.info("Transaction status updated to AUTHORIZED for transaction ID: {}", transaction.getId());
    }

    public java.util.List<Transaction> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomerId(customerId);
    }

    // Inner class for credit request
    private static class CreditRequest {
        private String merchantAccountNumber;
        private BigDecimal amount;

        public CreditRequest(String merchantAccountNumber, BigDecimal amount) {
            this.merchantAccountNumber = merchantAccountNumber;
            this.amount = amount;
        }

        public String getMerchantAccountNumber() {
            return merchantAccountNumber;
        }

        public void setMerchantAccountNumber(String merchantAccountNumber) {
            this.merchantAccountNumber = merchantAccountNumber;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}

