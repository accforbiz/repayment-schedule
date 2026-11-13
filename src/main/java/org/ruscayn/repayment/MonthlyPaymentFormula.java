package org.ruscayn.repayment;

import java.math.BigInteger;

final class MonthlyPaymentFormula {

    private final long loanAmount;
    private final int termMonths;
    private final AnnualInterestRate annualRate;

    MonthlyPaymentFormula(long loanAmount, int termMonths, AnnualInterestRate annualRate) {
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;
        this.annualRate = annualRate;
    }

    /*
     * 금리가 0%이면 아래 공식의 나누는 수가 0이 되어 계산할 수 없다.
     * 이자가 없으므로 대출액을 회차 수로 똑같이 나누고 원 미만을 버린다.
     * 예) 1,000,000원, 3개월 -> 333,333원 (남는 1원은 마지막 회차가 갚는다)
     */
    long payment() {
        if (annualRate.isZero()) {
            return loanAmount / termMonths;
        }
        return amortizedPayment();
    }

    /*
     * 월 납입액은 "매월 똑같이 내면 만기에 남은 원금이 정확히 0원이 되는 금액"이다.
     *
     *   월 납입액 = 대출액 x 월 금리 x (1 + 월 금리)^회차 / ((1 + 월 금리)^회차 - 1)
     *
     * 월 금리는 분수 m / D 로 둔다 (m: 0.001퍼센트 단위의 연 금리, D: 1,200,000).
     * 분자와 분모에 D^회차 를 곱해 정리하면 모두 정수가 된다. A = D + m 이라 할 때
     *
     *   월 납입액 = 대출액 x m x A^회차 / (D x (A^회차 - D^회차))
     *
     * 정수끼리만 계산하고 마지막 나눗셈에서 한 번만 원 미만을 버리므로
     * 소수 계산에서 생기는 오차가 금액에 섞이지 않는다.
     *
     * 예) 1,200,000원, 연 12%, 3개월 (m = 12,000, D = 1,200,000, A = 1,212,000)
     *     정확한 값은 408,026.53...원이고 버림하면 408,026원이다.
     */
    private long amortizedPayment() {
        BigInteger numerator = annualRate.monthlyNumerator();
        BigInteger denominator = annualRate.monthlyDenominator();
        BigInteger grown = denominator.add(numerator).pow(termMonths);
        BigInteger flat = denominator.pow(termMonths);
        BigInteger dividend = BigInteger.valueOf(loanAmount).multiply(numerator).multiply(grown);
        BigInteger divisor = denominator.multiply(grown.subtract(flat));
        return dividend.divide(divisor).longValueExact();
    }
}
