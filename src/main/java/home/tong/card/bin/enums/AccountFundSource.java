package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Account Fund Source
 */
public enum AccountFundSource {
    CREDIT("C"),
    DEBIT("D"),
    PREPAID("P"),
    CHARGE("H"),
    DEFERRED_DEBIT("R"),
    DEFAULT(StringUtils.SPACE);

    private static Map<String, AccountFundSource> map = Arrays.stream(values())
            .collect(toMap(AccountFundSource::toString, o -> o));

    private String value;

    AccountFundSource(final String value) {
        this.value = value;
    }

    public static AccountFundSource fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
