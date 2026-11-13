package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("만기일시상환")
class BulletScheduleTest {

    @Test
    @DisplayName("검산한 정답: 1,200,000원, 연 12%, 3개월은 매월 이자 12,000원을 내고 마지막에 원금을 갚는다")
    void matchesHandCalculatedSchedule() {
        List<Installment> schedule = Schedules.of(1_200_000L, 3, "12", RepaymentType.BULLET);
        assertEquals(List.of(
                new Installment(0L, 12_000L),
                new Installment(0L, 12_000L),
                new Installment(1_200_000L, 12_000L)), schedule);
    }

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 만기 1개월이면 이자만 내는 회차 없이 한 회차에 원금과 이자를 갚는다")
    void repaysEverythingAtOnceWhenTermIsOneMonth() {
        List<Installment> schedule = Schedules.of(1_200_000L, 1, "12", RepaymentType.BULLET);
        assertEquals(List.of(new Installment(1_200_000L, 12_000L)), schedule);
    }
}
