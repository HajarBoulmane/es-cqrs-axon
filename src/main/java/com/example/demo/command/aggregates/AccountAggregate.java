package com.example.demo.command.aggregates;

import com.example.demo.command.commands.AddAccountCommand;
import com.example.demo.command.commands.CreditAccountCommand;
import com.example.demo.command.commands.DebitAccountCommand;
import com.example.demo.command.commands.UpdateAccountStatusCommand;
import com.example.demo.enums.AccountStatus;
import com.example.demo.events.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Slf4j
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private AccountStatus status;

    public AccountAggregate(){}

    @CommandHandler
    public  AccountAggregate(AddAccountCommand command){
        log.info("############## AddAccountCommand Received ###################");
        if(command.getInitialBalance()<=0) throw new IllegalArgumentException("Balance must be positive");
        AggregateLifecycle.apply(new AccountCreatedEvent(
                command.getId(),
                command.getInitialBalance(),
                AccountStatus.CREATED,
                command.getCurrency()
                ));
        AggregateLifecycle.apply(new AccountActivatedEvent(
                command.getId(), AccountStatus.ACTIVATED
                )
        );
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("############## AccountCreatedCommand occured ###################");
        this.accountId = event.getAccountId();
        this.balance=event.getInitialBalance();
        this.status=event.getStatus();

    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        log.info("AccountActivatedEvent occurred");
        this.status = event.getStatus();
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        log.info("CreditAccountCommand received");
        if (!status.equals(AccountStatus.ACTIVATED))
            throw new RuntimeException("Account " + accountId + " is not activated");
        if (command.getAmount() <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        this.balance += event.getAmount();   // status inchangé
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        log.info("DebitAccountCommand received");
        if (!status.equals(AccountStatus.ACTIVATED))
            throw new RuntimeException("Account " + accountId + " is not activated");
        if (balance < command.getAmount())
            throw new RuntimeException("Balance not sufficient");
        if (command.getAmount() <= 0)
            throw new IllegalArgumentException("Amount must be positive");

        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        this.balance -= event.getAmount();
    }

    @CommandHandler
    public void handle(UpdateAccountStatusCommand command) {
        if (this.status.equals(command.getStatus()))
            throw new RuntimeException("Account is already in status " + command.getStatus());
        AggregateLifecycle.apply(new AccountStatusUpdatedEvent(command.getId(), command.getStatus()));
    }

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {
        this.status = event.getStatus();
    }

}
