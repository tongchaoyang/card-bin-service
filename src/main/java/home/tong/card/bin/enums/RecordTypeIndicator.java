package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Record Type indicator of a record
 */
public enum RecordTypeIndicator {
    HEADER("H"),
    DETAIL("D"),
    TRAILER("T"),
    DEFAULT(StringUtils.SPACE);

    private String value;

    RecordTypeIndicator(final String value) {
        this.value = value;
    }

    public static RecordTypeIndicator fromChar(final char c) {
        switch (c) {
            case 'H':
                return HEADER;
            case 'D':
                return DETAIL;
            case 'T':
                return TRAILER;
            default:
                return DEFAULT;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
