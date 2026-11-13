package org.ruscayn.repayment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class RepaymentScheduleCalculator {

    private RepaymentScheduleCalculator() {
    }

    public static List<Installment> calculate(
            long loanAmount, int termMonths, BigDecimal annualRatePercent, RepaymentType repaymentType) {
        Objects.requireNonNull(repaymentType, "repaymentType");
        Loan loan = new Loan(loanAmount, termMonths, new AnnualInterestRate(annualRatePercent));
        return repaymentType.schedule(loan);
    }
}
