package home.tong.card.bin.enums;

import com.neovisionaries.i18n.CountryCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Original Credit Indicator
 *
 * Visas Only
 */
public enum OriginalCreditIndicator {
    US_ISSUER_REJECT(StringUtils.SPACE),
    US_ISSUER_ACCEPT("A"),
    NON_US_ISSUER_ACCEPT(StringUtils.SPACE),
    NON_US_ISSUER_REJECT("N"),
    DEFAULT(StringUtils.SPACE);

    private static final String ACCEPT = "A";
    private static final String REJECT = "N";
    private String value;

    OriginalCreditIndicator(final String value) {
        this.value = value;
    }

    public static OriginalCreditIndicator fromString(
            final String val, final CountryCode countryCode, final DetailCardProduct detailCardProduct) {
        if (val == null) {
            return DEFAULT;
        }
        // This applies to Visa only
        if (detailCardProduct == null || detailCardProduct != DetailCardProduct.VISA) {
            return DEFAULT;
        }

        /*
         * From Global BIN spec:
         * US Issuer:
         *     Space = Reject MT Transaction
         *     A - Accept MT Transaction
         * Non-US Issuer:
         *     Space = Accept MT Transaction
         *     N - Reject MT Transaction
         */
        if (countryCode == CountryCode.US) {
            switch (val.toUpperCase(Locale.US)) {
                case ACCEPT:
                    return US_ISSUER_ACCEPT;
                case StringUtils.EMPTY:
                case StringUtils.SPACE:
                    return US_ISSUER_REJECT;
                default:
                    return DEFAULT;
            }
        } else {
            switch (val.toUpperCase(Locale.US)) {
                case REJECT:
                    return NON_US_ISSUER_REJECT;
                case StringUtils.EMPTY:
                case StringUtils.SPACE:
                    return NON_US_ISSUER_ACCEPT;
                default:
                    return DEFAULT;
            }
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
