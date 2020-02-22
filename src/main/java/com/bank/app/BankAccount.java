package com.bank.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BinaryOperator;

public class BankAccount {
    private final String accountNumber;
    private BigDecimal balance;
    private final String accountHolder;

    private BinaryOperator<BigDecimal> substractOp = (balance, withdralAmount) -> balance.subtract(withdralAmount);
    private BinaryOperator<BigDecimal> addOperation = (balance, depositeAmt) -> balance.add(depositeAmt);
    private ReadWriteLock accountLock;

    public BankAccount(String accountNumber, BigDecimal balance, String accountHolder) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountHolder = accountHolder;
        accountLock = new ReentrantReadWriteLock();
    }


    public void withdraw(double amount) {
        this.accountLock.writeLock().lock();
        try {
            balance = substractOp.apply(balance, new BigDecimal(amount));
        } finally {
            this.accountLock.writeLock().unlock();
        }
    }

    public void deposite(double amount) {
        this.accountLock.writeLock().lock();
        try{
            balance = addOperation.apply(balance, new BigDecimal(amount));
        } finally {
            this.accountLock.writeLock().unlock();
        }
    }

    public double getBalance() {
        this.accountLock.readLock().lock();
        try {
            return this.balance.setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        } finally {
            this.accountLock.readLock().unlock();
        }
    }

    public String getAccountHolder() {
        this.accountLock.readLock().lock();
        try {
            return accountHolder;
        } finally {
            this.accountLock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountNumber=" + accountNumber +
                ", balance=" + balance +
                ", accountHolder='" + accountHolder + '\'' +
                '}';
    }
}
