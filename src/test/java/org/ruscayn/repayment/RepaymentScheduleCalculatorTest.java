package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상환스케줄 계산 진입점")
class RepaymentScheduleCalculatorTest {

    @Test
    @DisplayName("요건 규칙: 모든 상환방식에서 돌려준 목록은 호출한 쪽이 고칠 수 없다")
    void returnsUnmodifiableList() {
        for (RepaymentType type : RepaymentType.values()) {
            List<Installment> schedule = Schedules.of(1_200_000L, 3, "12", type);
            assertThrows(UnsupportedOperationException.class, schedule::clear, type.name());
        }
    }

    @Test
    @DisplayName("잘못된 입력: 금리나 상환방식이 null이면 거부한다")
    void rejectsNullRateOrType() {
        assertThrows(NullPointerException.class, () -> RepaymentScheduleCalculator.calculate(
                1_200_000L, 3, null, RepaymentType.BULLET));
        assertThrows(NullPointerException.class, () -> RepaymentScheduleCalculator.calculate(
                1_200_000L, 3, new BigDecimal("12"), null));
    }
}
