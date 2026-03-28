package SolidPrinciples.InterfaceSegregationPrinciple;

public class Phonepe implements UPIPayments {

    @Override
    public void payMoney() {
        System.out.println("PhonePe is processing the UPI payment");
    }

    @Override
    public void getScratchCard() {
        System.out.println("PhonePe scratch card unlocked");
    }
}
