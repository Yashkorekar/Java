package SolidPrinciples.DependencyInversionPrinciple;

public class ShoppingMall {

    private final BankCard bankCard;

    public ShoppingMall(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public void doPurchaseSomething(long amount) {
        bankCard.doTransaction(amount);
    }

    public static void main(String[] args) {
        ShoppingMall creditCardMall = new ShoppingMall(new CreditCard());
        ShoppingMall debitCardMall = new ShoppingMall(new DebitCard());

        creditCardMall.doPurchaseSomething(5_000);
        debitCardMall.doPurchaseSomething(2_000);
    }
}
