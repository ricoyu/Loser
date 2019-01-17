package com.loserico.concurrent.stampedlock;

/**
 * Our first BankAccount has no synchronization at all. It also does not check that
 * the amounts being deposited and withdrawn are positive. We will leave out
 * argument checking in our code.
 * 
 * @author Loser
 * @since Jul 23, 2016
 * @version
 *
 */
public class BankAccount {
	private long balance;

	public BankAccount(long balance) {
		this.balance = balance;
	}

	public void deposit(long amount) {
		balance += amount;
	}

	public void withdraw(long amount) {
		balance -= amount;
	}

	public long getBalance() {
		return balance;
	}
}