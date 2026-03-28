package SolidPrinciples.SingleResponsibilityPrinciple;

public class BankService {

    public long deposit(long currentBalance, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        return currentBalance + amount;
    }

    public long withdraw(long currentBalance, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > currentBalance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        return currentBalance - amount;
    }

    public static void main(String[] args) {
        BankService bankService = new BankService();
        LoanService loanService = new LoanService();
        NotificationService notificationService = new NotificationService();
        PrinterService printerService = new PrinterService();

        long updatedBalance = bankService.deposit(10_000, 2_000);
        updatedBalance = bankService.withdraw(updatedBalance, 1_500);

        System.out.println(loanService.getLoanInterestInfo("HOME"));
        notificationService.sendTransactionAlert("ACC-1001", updatedBalance);
        printerService.printPassbook("ACC-1001", updatedBalance);
    }
}
