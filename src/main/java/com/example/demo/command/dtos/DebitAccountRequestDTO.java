package com.example.demo.command.dtos;

public record DebitAccountRequestDTO(String accountId, double amount, String currency) {}