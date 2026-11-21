package com.example.demo.events;

import com.example.demo.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountActivatedEvent {
    private String accountId;
    private AccountStatus status;
}