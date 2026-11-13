package org.ruscayn.repayment.cli;

import java.io.PrintStream;
import java.util.Arrays;
import org.ruscayn.repayment.RepaymentType;

public final class RepaymentScheduleApp {

    private static final int SUCCESS = 0;
    private static final int INVALID_INPUT = 1;
    private static final int WRONG_USAGE = 2;

    private RepaymentScheduleApp() {
    }

    public static void main(String[] arguments) {
        System.exit(run(arguments, System.out, System.err));
    }

    static int run(String[] arguments, PrintStream output, PrintStream error) {
        if (arguments.length != LoanArguments.COUNT) {
            error.print(usage());
            return WRONG_USAGE;
        }
        return printSchedule(arguments, output, error);
    }

    private static int printSchedule(String[] arguments, PrintStream output, PrintStream error) {
        try {
            output.print(new ScheduleTable(new LoanArguments(arguments).schedule()).render());
            return SUCCESS;
        } catch (IllegalArgumentException invalidInput) {
            error.print("Error: " + invalidInput.getMessage() + "\n");
            return INVALID_INPUT;
        }
    }

    private static String usage() {
        return """
                Usage: <loan-amount> <term-months> <annual-rate-percent> <repayment-type>
                  loan-amount          whole number of won, 1 to 1,000,000,000,000
                  term-months          whole number of months, 1 to 600
                  annual-rate-percent  0 to 20, at most 3 decimal places (e.g. 5.5)
                  repayment-type       one of %s
                Example: 1200000 3 12 EQUAL_PAYMENT
                """.formatted(Arrays.toString(RepaymentType.values()));
    }
}
