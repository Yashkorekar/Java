package SolidPrinciples.DependencyInversionPrinciple;

public class CreditCard implements BankCard {

    @Override
    public void doTransaction(long amount) {
        System.out.println("Paid using credit card: " + amount);
    }
}
