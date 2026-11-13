package org.ruscayn.repayment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public record AnnualInterestRate(BigDecimal percent) {

    private static final BigDecimal MAX_PERCENT = new BigDecimal("20");
    private static final int MAX_DECIMAL_PLACES = 3;

    /*
     * 월 금리를 분수로 다루기 위한 분모이다.
     * 금리를 0.001퍼센트 단위의 정수로 바꾸면(연 5.5%는 5,500) 월 금리는
     * "그 정수 / (12개월 x 100퍼센트 x 1,000)" 이 된다.
     * 예) 연 5.5%의 월 금리 = 5,500 / 1,200,000 = 0.0045833...
     * 소수로 미리 나누면 0.0045833...에서 끊어야 해서 오차가 생기므로 분수 그대로 둔다.
     */
    private static final long MONTHLY_DENOMINATOR = 12L * 100L * 1_000L;

    public AnnualInterestRate {
        Objects.requireNonNull(percent, "percent");
        percent = percent.stripTrailingZeros();
        requireWithinRange(percent);
        requireAllowedDecimalPlaces(percent);
    }

    public boolean isZero() {
        return percent.signum() == 0;
    }

    /*
     * 한 달 이자 = 남은 원금 x 연 금리 / 12 이고, 원 미만은 버린다.
     * 곱셈을 먼저 하고 정수 나눗셈을 마지막에 한 번만 해서 버림이 한 번만 일어나게 한다.
     * 예) 남은 원금 803,974원, 연 12%(정수 12,000)
     *     803,974 x 12,000 / 1,200,000 = 8,039.74 -> 8,039원
     */
    public long monthlyInterestOn(long balance) {
        return Math.multiplyExact(balance, thousandthsOfPercent()) / MONTHLY_DENOMINATOR;
    }

    BigInteger monthlyNumerator() {
        return BigInteger.valueOf(thousandthsOfPercent());
    }

    BigInteger monthlyDenominator() {
        return BigInteger.valueOf(MONTHLY_DENOMINATOR);
    }

    private long thousandthsOfPercent() {
        return percent.movePointRight(MAX_DECIMAL_PLACES).longValueExact();
    }

    private static void requireWithinRange(BigDecimal percent) {
        if (percent.signum() < 0 || percent.compareTo(MAX_PERCENT) > 0) {
            throw new IllegalArgumentException("Annual interest rate must be between 0 and "
                    + MAX_PERCENT + " percent, but was " + percent.toPlainString() + ".");
        }
    }

    private static void requireAllowedDecimalPlaces(BigDecimal percent) {
        if (percent.scale() > MAX_DECIMAL_PLACES) {
            throw new IllegalArgumentException("Annual interest rate allows at most "
                    + MAX_DECIMAL_PLACES + " decimal places, but was " + percent.toPlainString() + ".");
        }
    }
}
