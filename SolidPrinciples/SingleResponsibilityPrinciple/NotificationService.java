package SolidPrinciples.SingleResponsibilityPrinciple;

public class NotificationService {

    public void sendOtp(String medium) {
        System.out.println("Sending OTP using " + medium);
    }

    public void sendTransactionAlert(String accountNumber, long balance) {
        System.out.println("Alert for " + accountNumber + ": updated balance is " + balance);
    }
}
