package org.ruscayn.repayment.cli;

import java.math.BigDecimal;
import java.util.List;
import org.ruscayn.repayment.Installment;
import org.ruscayn.repayment.RepaymentScheduleCalculator;
import org.ruscayn.repayment.RepaymentType;

final class LoanArguments {

    static final int COUNT = 4;

    private final String loanAmount;
    private final String termMonths;
    private final String annualRatePercent;
    private final String repaymentType;

    LoanArguments(String[] arguments) {
        this.loanAmount = arguments[0];
        this.termMonths = arguments[1];
        this.annualRatePercent = arguments[2];
        this.repaymentType = arguments[3];
    }

    List<Installment> schedule() {
        return RepaymentScheduleCalculator.calculate(
                wholeNumber("Loan amount", loanAmount),
                termMonthsWithinIntRange(),
                decimalNumber("Annual interest rate", annualRatePercent),
                RepaymentType.parse(repaymentType));
    }

    private int termMonthsWithinIntRange() {
        return Math.clamp(wholeNumber("Loan term", termMonths), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private static long wholeNumber(String label, String text) {
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException notWholeNumber) {
            throw new IllegalArgumentException(label + " must be a whole number, but was " + text + ".");
        }
    }

    private static BigDecimal decimalNumber(String label, String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException notNumber) {
            throw new IllegalArgumentException(label + " must be a number, but was " + text + ".");
        }
    }
}
