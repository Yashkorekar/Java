package SolidPrinciples.OpenClosedPrinciple;

public class MobileNotificationService implements Notificationservice {

    @Override
    public void sendOTP(String message) {
        System.out.println("Sending mobile OTP: " + message);
    }

    @Override
    public void sendTransactionReport(String message) {
        System.out.println("Sending mobile report: " + message);
    }
}
