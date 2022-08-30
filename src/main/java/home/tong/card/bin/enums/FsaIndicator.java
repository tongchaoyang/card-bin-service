package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * FSA Indicator
 */
public enum FsaIndicator {
    FSA("F"),
    DEFAULT(StringUtils.SPACE);

    private String value;

    FsaIndicator(final String value) {
        this.value = value;
    }

    public static FsaIndicator fromString(final String value) {
        return StringUtils.equalsIgnoreCase(value, FSA.toString()) ? FSA : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
