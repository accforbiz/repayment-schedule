package org.ruscayn.repayment;

import java.util.List;

public interface RepaymentMethod {

    List<Installment> schedule(Loan loan);
}
