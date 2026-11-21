package com.example.demo.events;

import com.example.demo.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedEvent {

    private String accountId;
    private double initialBalance;
    private AccountStatus status;
    private String currency;
}
