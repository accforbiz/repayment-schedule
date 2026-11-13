package org.ruscayn.repayment;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum RepaymentType implements RepaymentMethod {

    EQUAL_PAYMENT(new EqualPaymentMethod()),
    BULLET(new BulletRepaymentMethod());

    private final RepaymentMethod method;

    RepaymentType(RepaymentMethod method) {
        this.method = method;
    }

    @Override
    public List<Installment> schedule(Loan loan) {
        return method.schedule(loan);
    }

    public static RepaymentType parse(String name) {
        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException unknownName) {
            throw new IllegalArgumentException("Repayment type must be one of "
                    + Arrays.toString(values()) + ", but was " + name + ".");
        }
    }
}
