package com.example.demo.query.dto;

import com.example.demo.query.entities.Account;
import com.example.demo.query.entities.AccountOperation;

import java.util.List;

public record AccountStatementResponseDTO(Account account, List<AccountOperation> operations) {}
