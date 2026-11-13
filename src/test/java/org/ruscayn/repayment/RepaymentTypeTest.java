package org.ruscayn.repayment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("상환방식 이름")
class RepaymentTypeTest {

    @Test
    @DisplayName("계산 방식이 바뀌는 지점: 소문자로 적은 이름도 같은 상환방식으로 해석한다")
    void parsesNameIgnoringCase() {
        assertEquals(RepaymentType.EQUAL_PAYMENT, RepaymentType.parse("equal_payment"));
    }

    @Test
    @DisplayName("잘못된 입력: 등록되지 않은 상환방식 이름을 거부하고 쓸 수 있는 이름을 알려 준다")
    void rejectsUnknownName() {
        IllegalArgumentException rejection =
                assertThrows(IllegalArgumentException.class, () -> RepaymentType.parse("ANNUITY"));
        assertTrue(rejection.getMessage().contains("[EQUAL_PAYMENT, BULLET]"), rejection.getMessage());
    }
}
