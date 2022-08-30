package home.tong.card.bin.enums;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Regulator Indicator - Applies to US issued cards only (Visa, MasterCard, and Discover)
 */
public enum RegulatorIndicator {
    ISS_REGULATED("B"),
    ISS_NONREGULATED("N"), // Default
    ISS_REGULATED_FRAUD("1");

    private static Map<String, RegulatorIndicator> map = Arrays.stream(values())
            .collect(toMap(RegulatorIndicator::toString, o -> o));

    private String value;

    RegulatorIndicator(final String value) {
        this.value = value;
    }

    public static RegulatorIndicator fromString(final String value) {
        final String upper = StringUtils.upperCase(value);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : ISS_NONREGULATED;
    }

    @Override
    public String toString() {
        return value;
    }
}
