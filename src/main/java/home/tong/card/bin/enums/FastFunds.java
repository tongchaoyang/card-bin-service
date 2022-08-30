package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Fast Funds
 *
 * This indicator determines if the Visa account can receive the transfer of funds within 30 minutes.
 * Visa Only
 */
public enum FastFunds {
    DOMESTIC_AND_CROSS_BORDER("Y"),
    CROSS_BORDER("C"),
    DOMESTIC("D"),
    NO_PARTICIPATION(StringUtils.SPACE);

    private static Map<String, FastFunds> map = Arrays.stream(values())
            .collect(toMap(FastFunds::toString, o -> o));

    private String value;

    FastFunds(final String value) {
        this.value = value;
    }

    public static FastFunds fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotEmpty(upper) && map.containsKey(upper) ? map.get(upper) : NO_PARTICIPATION;
    }

    @Override
    public String toString() {
        return value;
    }
}
