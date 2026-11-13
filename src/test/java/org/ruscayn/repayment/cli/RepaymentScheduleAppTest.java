package org.ruscayn.repayment.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("명령 인자 실행")
class RepaymentScheduleAppTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ByteArrayOutputStream error = new ByteArrayOutputStream();

    @Test
    @DisplayName("올바른 인자 네 개를 넣으면 표를 출력하고 종료 코드 0을 낸다")
    void printsScheduleForValidArguments() {
        int exitCode = run("1200000 3 12 BULLET");
        assertEquals(0, exitCode);
        assertTrue(text(output).contains("1,200,000             12,000"), text(output));
        assertEquals("", text(error));
    }

    @ParameterizedTest(name = "인자: {0}")
    @ValueSource(strings = {"1200000 3 12", "1200000 3 12 BULLET EXTRA"})
    @DisplayName("잘못된 입력: 인자가 네 개보다 적거나 많으면 사용법만 출력하고 종료 코드 2를 낸다")
    void printsUsageWhenArgumentCountIsWrong(String arguments) {
        int exitCode = run(arguments);
        assertEquals(2, exitCode);
        assertTrue(text(error).startsWith("Usage:"), text(error));
        assertEquals("", text(output));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "1000.5 3 12 BULLET, Loan amount must be a whole number",
            "1200000 4294967297 12 BULLET, Loan term must be between 1 and 600 months",
            "1200000 3 abc BULLET, Annual interest rate must be a number",
            "1200000 3 5.2555 BULLET, at most 3 decimal places",
            "1200000 3 12 ANNUITY, Repayment type must be one of"})
    @DisplayName("잘못된 입력: 종류별로 이유를 알리는 오류 메시지만 출력하고 종료 코드 1을 낸다")
    void printsErrorForInvalidInput(String arguments, String expectedReason) {
        int exitCode = run(arguments);
        assertEquals(1, exitCode);
        assertTrue(text(error).startsWith("Error: "), text(error));
        assertTrue(text(error).contains(expectedReason), text(error));
        assertEquals("", text(output));
    }

    private int run(String arguments) {
        return RepaymentScheduleApp.run(arguments.split(" "), printStream(output), printStream(error));
    }

    private static PrintStream printStream(ByteArrayOutputStream target) {
        return new PrintStream(target, true, StandardCharsets.UTF_8);
    }

    private static String text(ByteArrayOutputStream target) {
        return target.toString(StandardCharsets.UTF_8);
    }
}
