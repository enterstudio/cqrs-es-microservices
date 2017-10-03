package com.paas.microservices;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class AccountDomainServiceTest {
	private AccountRepository repo;
	private AccountDomainService accountService;
	private Account account;

	@Before
	public void setup() {
		repo = new InMemoryAccountRepository();
		accountService = new RepositoryAccountDomainService(repo);
		account = createAccount();
	}

	private Account createAccount() {
		UUID transactionId = UUID.randomUUID();
		Account created = accountService.createAccount(transactionId);
		return created;
	}

	@Test
	public void newAccountsHaveAZeroBalance() {
		assertThat(account.balance).isEqualTo(0d);
	}

	@Test
	public void createAccountRequestsAreIdempotent() {
		UUID transactionId = UUID.randomUUID();
		int accountsBefore = repo.getTotalNumberOfAccounts();
		accountService.createAccount(transactionId);
		accountService.createAccount(transactionId);
		assertThat(repo.getTotalNumberOfAccounts()).isEqualTo(accountsBefore + 1);
	}

	@Test
	public void accountsAreCreditable() {
		assertThat(account.balance).isEqualTo(0);

		UUID creditTxId = UUID.randomUUID();
		accountService.creditAccount(creditTxId, account.accountNumber, 50d);
		double balance = accountService.getBalance(account.accountNumber);
		assertThat(balance).isEqualTo(50d);
	}

	@Test
	public void creditsAreIdempotent() {
		assertThat(account.balance).isEqualTo(0);

		UUID creditTxId = UUID.randomUUID();
		accountService.creditAccount(creditTxId, account.accountNumber, 50d);
		accountService.creditAccount(creditTxId, account.accountNumber, 50d);
		double balance = accountService.getBalance(account.accountNumber);
		assertThat(balance).isEqualTo(50d);
	}

	@Test
	public void accountsAreDebitable() {
		assertThat(account.balance).isEqualTo(0);
		accountService.creditAccount(UUID.randomUUID(), account.accountNumber, 200d);

		accountService.debitAccount(UUID.randomUUID(), account.accountNumber, 100d);
		accountService.debitAccount(UUID.randomUUID(), account.accountNumber, 50d);
		double balance = accountService.getBalance(account.accountNumber);
		assertThat(balance).isEqualTo(50);
	}

	@Test
	public void debitsAreIdempotent() {
		assertThat(account.balance).isEqualTo(0);

		UUID creditTxID = UUID.randomUUID();
		accountService.creditAccount(creditTxID, account.accountNumber, 100d);
		UUID debitTxID = UUID.randomUUID();
		accountService.debitAccount(debitTxID, account.accountNumber, 30d);
		accountService.debitAccount(debitTxID, account.accountNumber, 30d);
		double balance = accountService.getBalance(account.accountNumber);
		assertThat(balance).isEqualTo(70d);

	}
}
