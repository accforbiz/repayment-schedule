package org.ruscayn.repayment;

import java.util.Locale;
import java.util.Objects;

public record Loan(long amount, int termMonths, AnnualInterestRate annualRate) {

    private static final long MIN_AMOUNT = 1L;
    private static final long MAX_AMOUNT = 1_000_000_000_000L;
    private static final long MIN_TERM_MONTHS = 1L;
    private static final long MAX_TERM_MONTHS = 600L;

    public Loan {
        Objects.requireNonNull(annualRate, "annualRate");
        requireWithinRange("Loan amount", amount, MIN_AMOUNT, MAX_AMOUNT, "won");
        requireWithinRange("Loan term", termMonths, MIN_TERM_MONTHS, MAX_TERM_MONTHS, "months");
    }

    public long monthlyInterestOn(long balance) {
        return annualRate.monthlyInterestOn(balance);
    }

    private static void requireWithinRange(String label, long value, long min, long max, String unit) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(String.format(Locale.ROOT,
                    "%s must be between %,d and %,d %s, but was %,d.", label, min, max, unit, value));
        }
    }
}
