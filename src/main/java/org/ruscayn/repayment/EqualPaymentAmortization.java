package org.ruscayn.repayment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class EqualPaymentAmortization {

    private final Loan loan;
    private final long monthlyPayment;
    private final List<Installment> installments = new ArrayList<>();
    private long balance;

    EqualPaymentAmortization(Loan loan, long monthlyPayment) {
        this.loan = loan;
        this.monthlyPayment = monthlyPayment;
        this.balance = loan.amount();
    }

    /*
     * 주어진 월 납입액으로 만기까지 상환해 본다.
     * 마지막 회차 전에 원금이 바닥나면 빈 값을 돌려주어, 이 월 납입액은 쓸 수 없음을 알린다.
     */
    Optional<List<Installment>> installments() {
        for (int count = 1; count < loan.termMonths(); count++) {
            if (!repayRegularInstallment()) {
                return Optional.empty();
            }
        }
        repayLastInstallment();
        return Optional.of(List.copyOf(installments));
    }

    /*
     * 마지막이 아닌 회차: 이자 = 남은 원금에 대한 한 달 이자, 원금 = 월 납입액 - 이자.
     * 예) 남은 원금 803,974원, 연 12%, 월 납입액 408,026원
     *     이자 8,039원, 원금 408,026 - 8,039 = 399,987원, 남은 원금 403,987원
     *
     * 갚을 원금이 남은 원금 이상이면 마지막 회차에 갚을 원금이 남지 않으므로 실패로 본다.
     */
    private boolean repayRegularInstallment() {
        long interest = loan.monthlyInterestOn(balance);
        long principal = monthlyPayment - interest;
        if (principal >= balance) {
            return false;
        }
        repay(principal, interest);
        return true;
    }

    /*
     * 마지막 회차: 남은 원금을 전액 갚는다.
     * 원 미만을 버리며 쌓인 오차가 모두 여기서 정리되므로 원금의 총합이 대출액과 정확히 같아진다.
     */
    private void repayLastInstallment() {
        repay(balance, loan.monthlyInterestOn(balance));
    }

    private void repay(long principal, long interest) {
        installments.add(new Installment(principal, interest));
        balance -= principal;
    }
}
