package SolidPrinciples.InterfaceSegregationPrinciple;

public class GooglePay implements UPIPayments, CashBackManager {

    @Override
    public void payMoney() {
        System.out.println("Google Pay is transferring money using UPI");
    }

    @Override
    public void getScratchCard() {
        System.out.println("Google Pay scratch card unlocked");
    }

    @Override
    public void getCashBackAsCreditBalance() {
        System.out.println("Google Pay cashback credited to balance");
    }

    public static void main(String[] args) {
        GooglePay googlePay = new GooglePay();
        UPIPayments paytm = new Paytm();
        UPIPayments phonepe = new Phonepe();

        googlePay.payMoney();
        googlePay.getScratchCard();
        googlePay.getCashBackAsCreditBalance();

        paytm.payMoney();
        paytm.getScratchCard();

        phonepe.payMoney();
        phonepe.getScratchCard();
    }
}
