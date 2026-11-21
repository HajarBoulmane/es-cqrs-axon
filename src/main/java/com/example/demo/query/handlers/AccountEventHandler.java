package com.example.demo.query.handlers;

import com.example.demo.events.*;
import com.example.demo.query.entities.Account;
import com.example.demo.query.entities.AccountOperation;
import com.example.demo.enums.OperationType;
import com.example.demo.query.repository.AccountRepository;
import com.example.demo.query.repository.OperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class    AccountEventHandler {

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;   // utilisé à l'étape 17

    public AccountEventHandler(AccountRepository accountRepository, OperationRepository operationRepository, QueryUpdateEmitter queryUpdateEmitter) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.queryUpdateEmitter = queryUpdateEmitter;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage<AccountCreatedEvent> eventMessage) {
        log.info("QUERY SIDE: AccountCreatedEvent received");
        Account account = Account.builder()
                .id(event.getAccountId())
                .balance(event.getInitialBalance())
                .status(event.getStatus())
                .currency(event.getCurrency())
                .createdAt(eventMessage.getTimestamp())   // astuce : timestamp technique de l'event
                .build();
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        Account account = accountRepository.findById(event.getAccountId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event) {
        Account account = accountRepository.findById(event.getAccountId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event, EventMessage<AccountDebitedEvent> msg) {
        Account account = accountRepository.findById(event.getAccountId()).get();
        AccountOperation op = AccountOperation.builder()
                .amount(event.getAmount())
                .date(msg.getTimestamp())
                .type(OperationType.DEBIT)
                .currency(event.getCurrency())
                .account(account)
                .build();
        operationRepository.save(op);
        account.setBalance(account.getBalance() - op.getAmount());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, EventMessage<AccountCreditedEvent> msg) {
        // identique à Debited : type CREDIT, et balance + amount
    }
}