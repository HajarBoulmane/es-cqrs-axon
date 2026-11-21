package com.example.demo.command.dtos;

import com.example.demo.enums.AccountStatus;

public record UpdateAccountStatusRequestDTO(String accountId, AccountStatus status) {}
