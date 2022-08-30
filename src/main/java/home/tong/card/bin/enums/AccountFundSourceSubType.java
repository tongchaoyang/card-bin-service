package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Account Fund Source Sub-Type
 * - Identifies the Account Fund Source Sub-Type.
 * - MasterCard only
 */
public enum AccountFundSourceSubType {
    PREPAID_CARD_RELOADABLE("R"),
    PREPAID_CARD_NON_RELOADABLE("N"),
    NOT_APPLICABLE(StringUtils.SPACE);

    private String value;

    AccountFundSourceSubType(final String value) {
        this.value = value;
    }

    public static AccountFundSourceSubType fromString(final String val) {
        final String upper = StringUtils.defaultString(StringUtils.upperCase(val));
        switch (upper) {
            case "R": return PREPAID_CARD_RELOADABLE;
            case "N": return PREPAID_CARD_NON_RELOADABLE;
            default: return NOT_APPLICABLE;
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
