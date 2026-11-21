package com.example.demo.command.dtos;

import com.example.demo.command.enums.AccountStatus;

public record UpdateAccountStatusRequestDTO(String accountId, AccountStatus status) {}
