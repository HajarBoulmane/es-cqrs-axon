package com.example.demo.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDebitedEvent {
    private String accountId;
    private double amount;
    private String currency;
}