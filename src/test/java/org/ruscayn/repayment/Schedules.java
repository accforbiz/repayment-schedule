package org.ruscayn.repayment;

import java.math.BigDecimal;
import java.util.List;

final class Schedules {

    private Schedules() {
    }

    static List<Installment> of(long loanAmount, int termMonths, String ratePercent, RepaymentType type) {
        return RepaymentScheduleCalculator.calculate(
                loanAmount, termMonths, new BigDecimal(ratePercent), type);
    }
}
