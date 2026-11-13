package org.ruscayn.repayment.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ruscayn.repayment.Installment;

@DisplayName("상환스케줄 표")
class ScheduleTableTest {

    @Test
    @DisplayName("표 출력: 회차, 원금, 이자 세 열을 목록의 순서대로 보여 준다")
    void rendersNumberPrincipalAndInterestColumns() {
        List<Installment> installments =
                List.of(new Installment(396_026L, 12_000L), new Installment(1_200_000L, 0L));
        String expected = """
                 No.          Principal           Interest
                   1            396,026             12,000
                   2          1,200,000                  0
                """;
        assertEquals(expected, new ScheduleTable(installments).render());
    }
}
