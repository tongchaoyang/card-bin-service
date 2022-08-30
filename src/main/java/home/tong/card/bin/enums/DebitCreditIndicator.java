package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * ACH Funded File: Debit Credit Indicator
 */
public enum DebitCreditIndicator {
    CREDIT("C"),
    DEBIT("D"),
    DEFAULT(StringUtils.SPACE);

    private static Map<String, DebitCreditIndicator> map = Arrays.stream(values())
            .collect(toMap(DebitCreditIndicator::toString, o -> o));

    private final String value;

    DebitCreditIndicator(final String value) {
        this.value = value;
    }

    /**
     * Given a string, return the corresponding DebitCreditIndicator
     */
    public static DebitCreditIndicator fromString(final String value) {
        final String upper = StringUtils.upperCase(value);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
