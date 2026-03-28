package SolidPrinciples.InterfaceSegregationPrinciple;

public class Paytm implements UPIPayments {

    @Override
    public void payMoney() {
        System.out.println("Paytm is processing the UPI payment");
    }

    @Override
    public void getScratchCard() {
        System.out.println("Paytm scratch card unlocked");
    }
}
