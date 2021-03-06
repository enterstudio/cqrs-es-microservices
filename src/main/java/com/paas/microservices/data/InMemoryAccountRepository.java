package com.paas.microservices.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.eventbus.Subscribe;
import com.paas.microservices.Account;
import com.paas.microservices.ResetHandler;
import com.paas.microservices.ResetStateEvent;
import com.paas.microservices.StoringEventBus;

public class InMemoryAccountRepository implements AccountRepository, ResetHandler {
	private final StoringEventBus eventBus;
	private final Map<UUID, Account> accounts;

	public InMemoryAccountRepository(StoringEventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
		this.accounts = new HashMap<>();
	}

	@Override
	public void handle(ResetStateEvent event) {
		accounts.clear();
	}

	@Override
	@Subscribe
	public void create(AccountCreateRequestDataEvent requestEvent) {
		UUID newId = requestEvent.eventId;
		Account newAccount = new Account(requestEvent.eventId, requestEvent.startingBalance);
		accounts.put(newId, newAccount);

		AccountCreatedDataEvent createdEvent = new AccountCreatedDataEvent(requestEvent.eventId, newId, requestEvent);
		eventBus.post(createdEvent);
	}

	@Override
	@Subscribe
	public Account save(AccountBalanceSetRequestDataEvent requestEvent) {
		accounts.put(requestEvent.account.accountNumber, requestEvent.account);
		eventBus.post(new AccountUpdatedDataEvent(requestEvent));
		return requestEvent.account;
	}

	@Override
	public Account load(UUID accountNumber) {
		if(! accounts.containsKey(accountNumber)) {
			throw new RuntimeException("AccountNumber ["+accountNumber+"] did not exist");
		}

		return accounts.get(accountNumber);
	}

	@Override
	public Integer getTotalNumberOfAccounts() {
		return accounts.size();
	}
}
