package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Visa Large Ticket Indicator
 */
public enum VisaLargeTicketIndicator {
    VISA_LARGE_TICKET("L"),
    DEFAULT(StringUtils.SPACE);

    private String value;

    VisaLargeTicketIndicator(final String value) {
        this.value = value;
    }

    public static VisaLargeTicketIndicator fromString(final String value) {
        return StringUtils.equalsIgnoreCase(value, VISA_LARGE_TICKET.toString()) ? VISA_LARGE_TICKET : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
