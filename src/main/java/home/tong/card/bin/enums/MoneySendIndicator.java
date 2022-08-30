package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Money Send Indicator
 *
 * MoneySend is a set of a MasterCard Network transactions that facilitate fund transfers.
 * This indicator determines if the MasterCard account is eligible to receive a MoneySend Payment.
 *
 * MasterCard Only
 */
public enum MoneySendIndicator {
    ENABLED_DOMESTIC_AND_CROSS_BORDER("Y"),
    ENABLED_DOMESTIC_ONLY("D"),
    NOT_ENABLED("N"),
    UNKNOWN("U"),
    DEFAULT(StringUtils.SPACE);

    private static Map<String, MoneySendIndicator> map = Arrays.stream(values())
            .collect(toMap(MoneySendIndicator::toString, o -> o));

    private String value;

    MoneySendIndicator(final String value) {
        this.value = value;
    }

    public static MoneySendIndicator fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
