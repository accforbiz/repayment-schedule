package org.ruscayn.repayment;

import java.util.List;
import java.util.Optional;

public final class EqualPaymentMethod implements RepaymentMethod {

    /*
     * 원리금균등상환: 매월 내는 금액(원금 + 이자)이 같다.
     *
     * 이자의 원 미만을 버리면 그만큼 원금을 더 갚게 되고, 이 차이가 매월 쌓인다.
     * 금액이 작고 만기가 길면 마지막 회차 전에 원금이 바닥날 수 있다.
     * 그런 경우에는 월 납입액을 1원 낮춰 처음부터 다시 계산한다.
     * 월 납입액이 낮아지면 매월 갚는 원금이 줄어 원금이 만기까지 남는다.
     *
     * 예) 10,000원, 연 5%, 600개월
     *     월 납입액 45원(공식 값 45.41원의 버림): 597회차에 원금이 바닥난다.
     *     월 납입액 44원(1원 낮춤): 599회차까지 44원씩 내고 마지막 회차에 원금 2,533원을 갚는다.
     */
    @Override
    public List<Installment> schedule(Loan loan) {
        long monthlyPayment = formulaPayment(loan);
        Optional<List<Installment>> installments = amortize(loan, monthlyPayment);
        while (installments.isEmpty()) {
            monthlyPayment--;
            installments = amortize(loan, monthlyPayment);
        }
        return installments.get();
    }

    private long formulaPayment(Loan loan) {
        return new MonthlyPaymentFormula(loan.amount(), loan.termMonths(), loan.annualRate()).payment();
    }

    private Optional<List<Installment>> amortize(Loan loan, long monthlyPayment) {
        return new EqualPaymentAmortization(loan, monthlyPayment).installments();
    }
}
