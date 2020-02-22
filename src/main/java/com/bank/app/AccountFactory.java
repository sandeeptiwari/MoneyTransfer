package com.bank.app;

import java.math.BigDecimal;

/**
 * Hello world!
 *
 */
public interface AccountFactory
{
   BankAccount getBankAccount(String accountNumber, BigDecimal balance, String accountHolder);
}
