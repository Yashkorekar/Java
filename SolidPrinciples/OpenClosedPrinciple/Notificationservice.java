package SolidPrinciples.OpenClosedPrinciple;

public interface Notificationservice {

    void sendOTP(String message);

    void sendTransactionReport(String message);
}
