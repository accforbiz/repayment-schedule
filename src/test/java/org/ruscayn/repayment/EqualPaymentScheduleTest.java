package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("원리금균등상환")
class EqualPaymentScheduleTest {

    @Test
    @DisplayName("검산한 정답: 1,200,000원, 연 12%, 3개월의 회차별 원금과 이자가 손으로 계산한 값과 같다")
    void matchesHandCalculatedSchedule() {
        List<Installment> schedule = equalPayment(1_200_000L, 3, "12");
        assertEquals(List.of(
                new Installment(396_026L, 12_000L),
                new Installment(399_987L, 8_039L),
                new Installment(403_987L, 4_039L)), schedule);
    }

    @Test
    @DisplayName("검산한 정답: 3억 원, 연 5%, 360개월처럼 만기가 길어도 월 납입액 1,610,464원이 정확하다")
    void keepsExactPaymentOnLongTerm() {
        List<Installment> schedule = equalPayment(300_000_000L, 360, "5");
        assertEquals(new Installment(360_464L, 1_250_000L), schedule.getFirst());
        assertEquals(new Installment(1_604_100L, 6_683L), schedule.getLast());
    }

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 금리 0%이면 대출액을 똑같이 나누고 남는 금액은 마지막 회차가 갚는다")
    void splitsEvenlyWhenRateIsZero() {
        List<Installment> schedule = equalPayment(1_000_000L, 3, "0");
        assertEquals(List.of(
                new Installment(333_333L, 0L),
                new Installment(333_333L, 0L),
                new Installment(333_334L, 0L)), schedule);
    }

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 만기 전에 원금이 바닥나면 월 납입액을 1원 낮춰 44원으로 계산한다")
    void lowersPaymentWhenPrincipalRunsOutEarly() {
        List<Installment> schedule = equalPayment(10_000L, 600, "5");
        assertEquals(new Installment(3L, 41L), schedule.getFirst());
        assertEquals(new Installment(34L, 10L), schedule.get(598));
        assertEquals(new Installment(2_533L, 10L), schedule.getLast());
    }

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 만기 1개월이면 한 회차에 원금 전액과 한 달 이자를 갚는다")
    void repaysEverythingAtOnceWhenTermIsOneMonth() {
        List<Installment> schedule = equalPayment(1_200_000L, 1, "12");
        assertEquals(List.of(new Installment(1_200_000L, 12_000L)), schedule);
    }

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 월 납입액이 0원이 되는 작은 대출은 마지막 회차에 원금 전액을 갚는다")
    void repaysAtLastWhenPaymentIsZero() {
        List<Installment> schedule = equalPayment(10L, 12, "12");
        assertEquals(Collections.nCopies(11, new Installment(0L, 0L)), schedule.subList(0, 11));
        assertEquals(new Installment(10L, 0L), schedule.getLast());
    }

    private static List<Installment> equalPayment(long loanAmount, int termMonths, String rate) {
        return Schedules.of(loanAmount, termMonths, rate, RepaymentType.EQUAL_PAYMENT);
    }
}
