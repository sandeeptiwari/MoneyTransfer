package com.bank.app;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import static java.lang.String.format;

public class BankTransfer {
    private final static String SOURCE_ACCOUNT_DONOT_EXIST = "Source account number %s don't exist.";

    private final static String DESTINATION_ACCOUNT_DONOT_EXIST = "Destination account number %s don't exist.";

    private final static String NOT_ENOUGH_BALANCE = "Account number %s don't have enough balance.";

    private final Map<String, BankAccount>  accountMap = new HashMap<>();;

    //Will check here ammount to be debited is less than acount balance or not :: So will use BiPredicate
    /*
     * x.compareTo(y)
     * 0 if x and y are equal
     * 1 if x is greater than y
     * -1 if x is smaller than y
     */
    BiFunction<Double, Double, Integer> withdrawlCheckPoint = (balance, withdrawlAmt) -> balance.compareTo(withdrawlAmt);

    BiConsumer<BankAccount, BankAccount> balancePrinter = (student, university) -> {
        System.out.println("Ending balance of Source account is "+student.getBalance()
                +" Destination Account balance is "+university.getBalance());
    };


    public BankTransfer() {
        AccountFactory accountFactory = BankAccount::new;
        BankAccount sandiBankAccount = accountFactory.getBankAccount("PT1001", new BigDecimal(5000), "Sandeep Tiwari");
        BankAccount sureshBankAccount = accountFactory.getBankAccount("PT1002", new BigDecimal(5000), "Suresh Pandey");
       accountMap.put("PT1001", sandiBankAccount);
       accountMap.put("PT1002", sureshBankAccount);
    }

    private BankAccount getAccount(String accountNumber, String errorReason) {
        BankAccount ret = accountMap.get(accountNumber);
        if (ret == null) {
            throw new IllegalArgumentException(errorReason);
        }
        return ret;
    }


    public void transferAmount(String accountNumberFrom, String  accountNumberTo, double amountToTransfer) throws IllegalArgumentException {
        synchronized (accountMap) {
            BankAccount bankAccountFrom = getAccount(accountNumberFrom, SOURCE_ACCOUNT_DONOT_EXIST);
            if (withdrawlCheckPoint.apply(bankAccountFrom.getBalance(), amountToTransfer) < 0) {
                throw new IllegalArgumentException(format(NOT_ENOUGH_BALANCE, accountNumberFrom));
            }
            BankAccount bankAccountTo = getAccount(accountNumberTo, DESTINATION_ACCOUNT_DONOT_EXIST);
            bankAccountFrom.withdraw(amountToTransfer);
            bankAccountTo.deposite(amountToTransfer);

            balancePrinter.accept(bankAccountFrom, bankAccountTo);
        }
    }


    public static void main(String[] args) {
        BiConsumer<String, Double> printer = (x, y) -> System.out.println(x+y);

        BankTransfer transfer = new BankTransfer();
        ExecutorService service = Executors.newFixedThreadPool(10);

        Thread t1 = new Thread(() ->{
            System.out.println(Thread.currentThread().getName() + " Executing balance tranfer here ");
            double toBeTransfer = 1000;
            transfer.transferAmount("PT1001", "PT1002", toBeTransfer);

            //printer.accept(Thread.currentThread().getName() + " Syas: Trnsfer is success, Balance in account of university " +
                  //  " is ", universityBankAccount.getBalance());
        });
        for (int i = 0; i < 20; i++) {
            service.submit(t1);
        }

        service.shutdown();

        try {
            while (!service.awaitTermination(5L, TimeUnit.MINUTES)) {
                System.out.println("Not Yet. Still waiting for termination");
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

    }
}
