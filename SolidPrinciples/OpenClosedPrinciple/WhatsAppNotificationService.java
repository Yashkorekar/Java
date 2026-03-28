package SolidPrinciples.OpenClosedPrinciple;

public class WhatsAppNotificationService implements Notificationservice {

    @Override
    public void sendOTP(String message) {
        System.out.println("Sending WhatsApp OTP: " + message);
    }

    @Override
    public void sendTransactionReport(String message) {
        System.out.println("Sending WhatsApp report: " + message);
    }
}
