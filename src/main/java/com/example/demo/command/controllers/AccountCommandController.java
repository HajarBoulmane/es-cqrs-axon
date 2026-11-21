package com.example.demo.command.controllers;

import com.example.demo.command.commands.AddAccountCommand;
import com.example.demo.command.commands.CreditAccountCommand;
import com.example.demo.command.commands.DebitAccountCommand;
import com.example.demo.command.dtos.AddNewAccountRequestDTO;
import com.example.demo.command.dtos.CreditAccountRequestDTO;
import com.example.demo.command.dtos.DebitAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping("/commands/accounts")
public class AccountCommandController {

    private CommandGateway commandGateway;
    private final EventStore eventStore;

    public AccountCommandController(EventStore eventStore, CommandGateway commandGateway) {
        this.eventStore = eventStore;
        this.commandGateway = commandGateway;
    }

    @PostMapping("/add")
    public CompletableFuture<String> addNewAccount(AddNewAccountRequestDTO request) {
       CompletableFuture<String> response= commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(),
                request.initialBalance(),
                request.currency()
        ));
       return response;
    }

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception exception){
        return exception.getMessage();
    }

    @PostMapping("/account/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO r) {
        return commandGateway.send(new CreditAccountCommand(r.accountId(), r.amount(), r.currency()));
    }

    @GetMapping("/events/{accountId}")
    public Stream<?> eventStore(@PathVariable String accountId) {
        return eventStore.readEvents(accountId).asStream();
    }

    @PostMapping("/account/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO request) {
        return commandGateway.send(new DebitAccountCommand(
                request.accountId(),
                request.amount(),
                request.currency()));
    }

}
