package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Issuing Network
 *
 * For Discover card types
 */
public enum IssuingNetwork {
    DISCOVER("00"),
    DINERS("01"),
    JCB("02"), // Japanese Credit Bank
    CUP("03"), // China Union Pay
    PAYPAL("04"),
    DEFAULT(StringUtils.SPACE);

    private static Map<String, IssuingNetwork> map = Arrays.stream(values())
            .collect(toMap(IssuingNetwork::toString, o -> o));

    private String value;

    IssuingNetwork(final String value) {
        this.value = value;
    }

    public static IssuingNetwork fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotBlank(upper) && map.containsKey(upper) ? map.get(upper) : DEFAULT;
    }

    @Override
    public String toString() {
        return value;
    }
}
