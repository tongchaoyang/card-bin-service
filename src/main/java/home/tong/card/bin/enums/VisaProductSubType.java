package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Visa Product Sub-Type
 */
public enum VisaProductSubType {
    BRAZIL_AGRICULTURE_MAINTENANCE_ACCOUNT("AC"),
    BRAZIL_AGRICULTURE_DEBIT_ACCOUNT_ELECTRON("AE"),
    BRAZIL_AGRICULTURE("AG"),
    BRAZIL_AGRICULTURE_INVESTMENT_LOAN("AI"),
    BRAZIL_CARGO("CG"),
    CONSTRUCTION("CS"),
    DISTRIBUTION("DS"),
    LARGE_MARKET_ENTERPRISE("EN"),
    SMALL_MARKET_EXPENSES("EX"),
    HEALTHCARE("HC"),
    VISA_LARGE_PURCHASE_ADVANTAGE("LP"),
    VISA_MOBILE_AGENT("MA"),
    INTEROPERABLE_MOBILE_BRANCHLESS_BANKING("MB"),
    VISA_MOBILE_GENERAL("MG"),
    BRAZIL_FOOD_OR_SUPERMARKET("VA"),
    BRAZIL_FUEL("VF"),
    VISA_VALE_MEAL_VOUCHER("VM"),
    VISA_VALE_FOOD_VOUCHER("VN"),
    BRAZIL_FOOD_OR_RESTAURANT("VR"),
    DEFAULT(StringUtils.SPACE);

    private static Map<String, VisaProductSubType> map = Arrays.stream(values())
            .collect(toMap(VisaProductSubType::toString, o -> o));

    private String value;

    VisaProductSubType(final String value) {
        this.value = value;
    }

    public static VisaProductSubType fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
