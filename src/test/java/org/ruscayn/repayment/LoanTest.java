package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("경계값: 대출액과 만기의 허용 범위")
class LoanTest {

    private static final AnnualInterestRate RATE = new AnnualInterestRate(new BigDecimal("5"));

    @ParameterizedTest(name = "{0}원")
    @ValueSource(longs = {1L, 1_000_000_000_000L})
    @DisplayName("대출액은 최솟값 1원과 최댓값 1조 원을 받는다")
    void acceptsLoanAmountAtBoundaries(long loanAmount) {
        assertDoesNotThrow(() -> new Loan(loanAmount, 12, RATE));
    }

    @ParameterizedTest(name = "{0}원")
    @ValueSource(longs = {0L, 1_000_000_000_001L})
    @DisplayName("대출액은 범위 바로 바깥인 0원과 1조 1원을 거부한다")
    void rejectsLoanAmountOutsideBoundaries(long loanAmount) {
        assertThrows(IllegalArgumentException.class, () -> new Loan(loanAmount, 12, RATE));
    }

    @ParameterizedTest(name = "{0}개월")
    @ValueSource(ints = {1, 600})
    @DisplayName("만기는 최솟값 1개월과 최댓값 600개월을 받는다")
    void acceptsTermAtBoundaries(int termMonths) {
        assertDoesNotThrow(() -> new Loan(1_000_000L, termMonths, RATE));
    }

    @ParameterizedTest(name = "{0}개월")
    @ValueSource(ints = {0, 601})
    @DisplayName("만기는 범위 바로 바깥인 0개월과 601개월을 거부한다")
    void rejectsTermOutsideBoundaries(int termMonths) {
        assertThrows(IllegalArgumentException.class, () -> new Loan(1_000_000L, termMonths, RATE));
    }
}
