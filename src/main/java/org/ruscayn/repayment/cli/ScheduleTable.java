package org.ruscayn.repayment.cli;

import java.util.List;
import java.util.Locale;
import org.ruscayn.repayment.Installment;

final class ScheduleTable {

    private static final String HEADER_FORMAT = "%4s  %17s  %17s\n";
    private static final String ROW_FORMAT = "%4d  %,17d  %,17d\n";

    private final List<Installment> installments;

    ScheduleTable(List<Installment> installments) {
        this.installments = List.copyOf(installments);
    }

    String render() {
        StringBuilder table = new StringBuilder(header());
        for (int index = 0; index < installments.size(); index++) {
            table.append(row(index + 1, installments.get(index)));
        }
        return table.toString();
    }

    private String header() {
        return String.format(Locale.ROOT, HEADER_FORMAT, "No.", "Principal", "Interest");
    }

    private String row(int number, Installment installment) {
        return String.format(Locale.ROOT, ROW_FORMAT,
                number, installment.principal(), installment.interest());
    }
}
