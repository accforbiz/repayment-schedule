package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

@DisplayName("요건 규칙: 과제의 구현포인트가 여러 대출 조건에서 성립한다")
class RepaymentRuleTest {

    private static final String CONDITION_NAME = "{0}원, {1}개월, 연 {2}%";

    static final List<Arguments> CONDITIONS = List.of(
            arguments(1_200_000L, 3, "12"),
            arguments(300_000_000L, 360, "5"),
            arguments(123_456_789L, 37, "7.125"),
            arguments(1_000_000L, 3, "0"),
            arguments(10_000L, 600, "5"),
            arguments(10L, 12, "12"),
            arguments(10_000_000L, 600, "20"),
            arguments(1L, 1, "0"),
            arguments(1_000_000_000_000L, 600, "20"),
            arguments(1L, 600, "20"),
            arguments(1_000_000_000_000L, 1, "0"),
            arguments(1_000_000_000_000L, 600, "0"),
            arguments(1L, 1, "20"),
            arguments(1_000_000_000_000L, 1, "20"),
            arguments(1L, 600, "0"));

    @ParameterizedTest(name = CONDITION_NAME)
    @FieldSource("CONDITIONS")
    @DisplayName("모든 상환방식에서 원금의 총합은 대출액과 같다")
    void principalSumEqualsLoanAmount(long loanAmount, int termMonths, String rate) {
        for (RepaymentType type : RepaymentType.values()) {
            List<Installment> schedule = Schedules.of(loanAmount, termMonths, rate, type);
            long principalSum = schedule.stream().mapToLong(Installment::principal).sum();
            assertEquals(loanAmount, principalSum, type.name());
        }
    }

    @ParameterizedTest(name = CONDITION_NAME)
    @FieldSource("CONDITIONS")
    @DisplayName("모든 상환방식에서 회차 수는 만기와 같다")
    void installmentCountEqualsTerm(long loanAmount, int termMonths, String rate) {
        for (RepaymentType type : RepaymentType.values()) {
            List<Installment> schedule = Schedules.of(loanAmount, termMonths, rate, type);
            assertEquals(termMonths, schedule.size(), type.name());
        }
    }

    @ParameterizedTest(name = CONDITION_NAME)
    @FieldSource("CONDITIONS")
    @DisplayName("모든 상환방식에서 원금과 이자는 음수가 아니다")
    void amountsAreNotNegative(long loanAmount, int termMonths, String rate) {
        for (RepaymentType type : RepaymentType.values()) {
            List<Installment> schedule = Schedules.of(loanAmount, termMonths, rate, type);
            assertTrue(schedule.stream().allMatch(RepaymentRuleTest::isNotNegative), type.name());
        }
    }

    @ParameterizedTest(name = CONDITION_NAME)
    @FieldSource("CONDITIONS")
    @DisplayName("원리금균등상환은 마지막 회차를 뺀 모든 회차의 원금과 이자의 합이 같다")
    void equalPaymentKeepsSamePaymentExceptLast(long loanAmount, int termMonths, String rate) {
        List<Installment> schedule = Schedules.of(loanAmount, termMonths, rate, RepaymentType.EQUAL_PAYMENT);
        long distinctPayments = schedule.stream().limit(termMonths - 1L)
                .mapToLong(installment -> installment.principal() + installment.interest())
                .distinct().count();
        assertTrue(distinctPayments <= 1, "서로 다른 합계의 개수: " + distinctPayments);
    }

    @ParameterizedTest(name = CONDITION_NAME)
    @FieldSource("CONDITIONS")
    @DisplayName("만기일시상환은 마지막 회차 전까지 원금을 갚지 않고 마지막 회차에 전액을 갚는다")
    void bulletRepaysWholePrincipalAtLast(long loanAmount, int termMonths, String rate) {
        List<Installment> schedule = Schedules.of(loanAmount, termMonths, rate, RepaymentType.BULLET);
        long principalBeforeLast = schedule.stream().limit(termMonths - 1L)
                .mapToLong(Installment::principal).sum();
        assertEquals(0L, principalBeforeLast);
        assertEquals(loanAmount, schedule.getLast().principal());
    }

    private static boolean isNotNegative(Installment installment) {
        return installment.principal() >= 0 && installment.interest() >= 0;
    }
}
