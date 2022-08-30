package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Detail Card Product
 */
public enum DetailCardProduct {
    VISA("V"),
    MASTERCARD("M"),
    AMERICAN_EXPRESS("A"),
    DISCOVER("D"),
    PIN_ONLY("N"),
    DEFAULT(StringUtils.SPACE);

    private static final Map<String, DetailCardProduct> cache = Arrays.stream(values())
                        .collect(toMap(DetailCardProduct::toString, o -> o));
    private final String abbrev;

    DetailCardProduct(final String value) {
        this.abbrev = value;
    }

    public static DetailCardProduct fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return cache.getOrDefault(upper, DEFAULT);
    }

    @Override
    public String toString() {
        return abbrev;
    }
}
