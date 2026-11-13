package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("금리")
class AnnualInterestRateTest {

    @ParameterizedTest(name = "연 {0}%")
    @ValueSource(strings = {"0", "20", "5.125"})
    @DisplayName("경계값: 최솟값 0%, 최댓값 20%, 소수 셋째 자리 금리를 받는다")
    void acceptsRateAtBoundaries(String percent) {
        assertDoesNotThrow(() -> rate(percent));
    }

    @ParameterizedTest(name = "연 {0}%")
    @ValueSource(strings = {"-0.001", "20.001"})
    @DisplayName("경계값: 범위 바로 바깥인 -0.001%와 20.001%를 거부한다")
    void rejectsRateOutsideBoundaries(String percent) {
        assertThrows(IllegalArgumentException.class, () -> rate(percent));
    }

    @Test
    @DisplayName("잘못된 입력: 소수 넷째 자리 금리 5.1255%를 거부한다")
    void rejectsFourDecimalPlaces() {
        assertThrows(IllegalArgumentException.class, () -> rate("5.1255"));
    }

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 5.2500%처럼 끝에 0이 붙은 표기는 5.25%와 같은 값으로 받는다")
    void treatsTrailingZerosAsSameValue() {
        assertEquals(rate("5.25"), rate("5.2500"));
    }

    @Test
    @DisplayName("검산한 정답: 1,000,000원에 연 0.01%의 한 달 이자 8.33원은 원 미만을 버려 8원이다")
    void truncatesMonthlyInterestBelowOneWon() {
        assertEquals(8L, rate("0.01").monthlyInterestOn(1_000_000L));
    }

    private static AnnualInterestRate rate(String percent) {
        return new AnnualInterestRate(new BigDecimal(percent));
    }
}
