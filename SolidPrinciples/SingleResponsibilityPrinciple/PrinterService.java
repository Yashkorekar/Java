package SolidPrinciples.SingleResponsibilityPrinciple;

public class PrinterService {

    public void printPassbook(String accountNumber, long balance) {
        System.out.println("Passbook entry -> account: " + accountNumber + ", balance: " + balance);
    }
}
