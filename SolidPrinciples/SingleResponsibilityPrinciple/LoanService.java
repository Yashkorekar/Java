package SolidPrinciples.SingleResponsibilityPrinciple;

public class LoanService {

    public String getLoanInterestInfo(String loanType) {
        return switch (loanType) {
            case "HOME" -> "Home loan interest starts around 8.25%";
            case "PERSONAL" -> "Personal loan interest starts around 11.5%";
            case "CAR" -> "Car loan interest starts around 9.1%";
            default -> "Please contact the bank for updated loan rates";
        };
    }
}
