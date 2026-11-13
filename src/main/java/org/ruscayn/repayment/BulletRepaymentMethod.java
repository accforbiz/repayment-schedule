package org.ruscayn.repayment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BulletRepaymentMethod implements RepaymentMethod {

    /*
     * 만기일시상환: 매월 이자만 내다가 마지막 회차에 원금 전액을 갚는다.
     * 원금이 줄지 않으므로 이자는 매월 같다.
     * 예) 1,200,000원, 연 12%, 3개월
     *     1회차 원금 0 / 이자 12,000, 2회차 원금 0 / 이자 12,000, 3회차 원금 1,200,000 / 이자 12,000
     */
    @Override
    public List<Installment> schedule(Loan loan) {
        long interest = loan.monthlyInterestOn(loan.amount());
        Installment interestOnly = new Installment(0L, interest);
        List<Installment> installments =
                new ArrayList<>(Collections.nCopies(loan.termMonths() - 1, interestOnly));
        installments.add(new Installment(loan.amount(), interest));
        return List.copyOf(installments);
    }
}
