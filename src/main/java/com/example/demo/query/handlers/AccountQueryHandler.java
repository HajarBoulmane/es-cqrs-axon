package com.example.demo.query.handlers;

import com.example.demo.query.dto.AccountStatementResponseDTO;
import com.example.demo.query.entities.Account;
import com.example.demo.query.entities.AccountOperation;
import com.example.demo.query.queries.GetAccountStatementQuery;
import com.example.demo.query.queries.GetAllAccountsQuery;
import com.example.demo.query.queries.WatchEventQuery;
import com.example.demo.query.repository.AccountRepository;
import com.example.demo.query.repository.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

// query/handlers
@Component
public class AccountQueryHandler {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }

    @QueryHandler
    public AccountStatementResponseDTO on(GetAccountStatementQuery query) {
        Account account = accountRepository.findById(query.getAccountId()).get();
        List<AccountOperation> operations = operationRepository.findByAccountId(query.getAccountId());
        return new AccountStatementResponseDTO(account, operations);
    }

    @QueryHandler
    public AccountOperation on(WatchEventQuery query) {
        return new AccountOperation();   // empty initial result, just to start the stream
    }

}