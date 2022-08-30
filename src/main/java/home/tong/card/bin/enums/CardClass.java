package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Card Class
 *
 * Categorizes the BIN as a Business card, Corporate T&E card, Purchase card or Consumer card.
 * Assists the POS device with prompting decisions - to collect addenda or not.
 * Visa, MasterCard and Discover only.
 */
public enum CardClass {
    BUSINESS("B"),
    CONSUMER("C"),
    PURCHASE("P"),
    CORPORATE("T"),
    DEFAULT(StringUtils.SPACE);

    private static Map<String, CardClass> map = Arrays.stream(values())
            .collect(toMap(CardClass::toString, o -> o));

    private String value;

    CardClass(final String value) {
        this.value = value;
    }

    public static CardClass fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
