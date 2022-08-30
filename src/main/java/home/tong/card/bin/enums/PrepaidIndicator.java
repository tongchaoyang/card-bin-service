package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Prepaid Indicator
 */
public enum PrepaidIndicator {
    PREPAID_CARD("P"),
    DEFAULT(StringUtils.SPACE);

    private String value;

    PrepaidIndicator(final String value) {
        this.value = value;
    }

    public static PrepaidIndicator fromString(final String value) {
        return StringUtils.equalsIgnoreCase(value, PREPAID_CARD.toString()) ? PREPAID_CARD : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
