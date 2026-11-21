package com.example.demo.command.events;

import com.example.demo.command.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountCreatedEvent {

    private String accountId;
    private double initialBalance;
    private AccountStatus status;
    private String currency;
}
