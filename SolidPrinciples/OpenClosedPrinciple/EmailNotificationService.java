package SolidPrinciples.OpenClosedPrinciple;

public class EmailNotificationService implements Notificationservice {

    @Override
    public void sendOTP(String message) {
        System.out.println("Sending email OTP: " + message);
    }

    @Override
    public void sendTransactionReport(String message) {
        System.out.println("Sending email report: " + message);
    }

    public static void main(String[] args) {
        Notificationservice[] services = {
            new EmailNotificationService(),
            new MobileNotificationService(),
            new WhatsAppNotificationService()
        };

        for (Notificationservice service : services) {
            service.sendOTP("123456");
            service.sendTransactionReport("Transaction completed successfully");
        }
    }
}
