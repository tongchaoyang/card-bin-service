package home.tong.card.bin.file.parser;

import com.neovisionaries.i18n.CountryCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CountryCodeUtil {

    // List of country codes encountered in BIN file that were incorrect
    private static final String INCORRECT_ROMANIA_CODE = "ROM";
    private static final String INCORRECT_KOSOVO_CODE = "QZZ";

    /**
     * Translate alpha-3 or numberic-3 code to CountryCode type, default to 'US' when country code is not found.
     *
     * @param sCountryCode: could either 3-digit numeric or alpha 3-char strings
     * @return CountryCode
     */

    public static CountryCode getCountryCode(final String sCountryCode) {
        CountryCode countryCode;

        if (StringUtils.isNumeric(sCountryCode)) {
            // to translate ISO numeric country code to CountryCode type
            countryCode = CountryCode.getByCode(Integer.parseInt(sCountryCode));
        } else {
            final String ucCountryCode = StringUtils.defaultString(StringUtils.upperCase(sCountryCode));
            switch (ucCountryCode) {
                case INCORRECT_ROMANIA_CODE:
                    return CountryCode.RO;
                case INCORRECT_KOSOVO_CODE:
                    return CountryCode.XK;
                default:
                    break;
            }
            countryCode = CountryCode.getByCode(FormatUtil.optionalField(ucCountryCode, BinDetailRecord.BinFileDetailFieldInfo.COUNTRY_CODE));
        }

        return Optional.ofNullable(countryCode).orElse(CountryCode.US);
    }
}
