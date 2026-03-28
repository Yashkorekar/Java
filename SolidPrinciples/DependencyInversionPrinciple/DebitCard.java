package SolidPrinciples.DependencyInversionPrinciple;

public class DebitCard implements BankCard {

    @Override
    public void doTransaction(long amount) {
        System.out.println("Paid using debit card: " + amount);
    }
}
