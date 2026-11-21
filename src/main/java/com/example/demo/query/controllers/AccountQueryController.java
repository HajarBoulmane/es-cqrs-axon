package com.example.demo.query.controllers;

import com.example.demo.query.dto.AccountStatementResponseDTO;
import com.example.demo.query.entities.Account;
import com.example.demo.query.entities.AccountOperation;
import com.example.demo.query.queries.GetAccountStatementQuery;
import com.example.demo.query.queries.GetAllAccountsQuery;
import com.example.demo.query.queries.WatchEventQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query")
public class AccountQueryController {

    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/accounts/all")
    public CompletableFuture<List<Account>> getAllAccounts() {
        return queryGateway.query(new GetAllAccountsQuery(),
                ResponseTypes.multipleInstancesOf(Account.class));
    }

    @GetMapping("/accounts/accountStatement/{accountId}")
    public CompletableFuture<AccountStatementResponseDTO> getAccountStatement(@PathVariable String accountId) {
        return queryGateway.query(new GetAccountStatementQuery(accountId),
                ResponseTypes.instanceOf(AccountStatementResponseDTO.class));
    }

    @GetMapping(value = "/watch/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountOperation> watch(@PathVariable String accountId) {
        SubscriptionQueryResult<AccountOperation, AccountOperation> result =
                queryGateway.subscriptionQuery(
                        new WatchEventQuery(accountId),
                        ResponseTypes.instanceOf(AccountOperation.class),   // initial result
                        ResponseTypes.instanceOf(AccountOperation.class));  // updates
        return result.initialResult().concatWith(result.updates());
    }
}