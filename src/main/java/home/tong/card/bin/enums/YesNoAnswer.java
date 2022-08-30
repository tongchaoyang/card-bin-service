package home.tong.card.bin.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Yes / No Answer
 */
public enum YesNoAnswer {
    Y("Y"),
    N("N"),
    NEITHER(StringUtils.SPACE);

    private static Map<String, YesNoAnswer> map = Arrays.stream(values())
            .collect(toMap(YesNoAnswer::toString, o -> o));

    private final String answer;

    YesNoAnswer(final String answer) {
        this.answer = answer;
    }

    public static YesNoAnswer fromString(final String val) {
        final String upper = StringUtils.upperCase(val);
        return StringUtils.isNotEmpty(upper) && map.containsKey(upper) ? map.get(upper) : NEITHER;
    }

    @Override
    public String toString() {
        return answer;
    }
}
