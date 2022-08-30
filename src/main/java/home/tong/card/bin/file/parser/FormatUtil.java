package home.tong.card.bin.file.parser;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Slf4j
public final class FormatUtil {

    public static final char SPACE_CHAR = ' ';
    public static final char ZERO_CHAR = '0';
    public static final String MMDDYY_DATE_FORMAT = "MMddyy";
    public static final String MMDDYYYY_DATE_FORMAT = "MMddyyyy";
    public static final String YYMMDD_DATE_FORMAT = "yyMMdd";
    public static final String YYYYMMDD_DATE_FORMAT = "yyyyMMdd";
    public static final String TIME_FORMAT = "HHmmss";

    private static final int PHONE_NUMBER_LENGTH = 10;
    private static final String PHONE_NUMBER_3PART_US_REGEX = "(\\d{3})(\\d{3})(\\d+)";
    private static final String PHONE_NUMBER_2PART_US_REGEX = "(\\d{3})(\\w+)";

    private FormatUtil() {
    }

    public static void validate(final String value, final FieldInfo fieldInfo) {
        if (value == null || fieldInfo == null) {
            throw new IllegalArgumentException("Null arguments not allowed.");
        }

        if (value.length() > fieldInfo.getLength()) {
            // Log the violation but allow large values
            log.warn("{} value too large. Expected: {}, Actual: {}",
                    fieldInfo.getDescription(), fieldInfo.getLength(), value.length());
        }
    }

    public static void validate(final Number value, final FieldInfo fieldInfo) {
        if (value == null || fieldInfo == null) {
            throw new IllegalArgumentException("Null arguments not allowed.");
        }

        if (value.longValue() < 0) {
            throw new IllegalArgumentException("Negative numbers not allowed.");
        }
        validate(String.valueOf(value), fieldInfo);
    }

    public static String validateDateTime(final Calendar cal, final FieldInfo fieldInfo, final String format) {
        if (cal == null || fieldInfo == null || StringUtils.isBlank(format)) {
            throw new IllegalArgumentException("Null arguments not allowed.");
        }

        final DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setCalendar(cal);

        final String value = dateFormat.format(cal.getTime());
        FormatUtil.validate(value, fieldInfo);
        return value;
    }

    public static void validateDateString(final String dateString, final FieldInfo fieldInfo, final String format) throws ParseException {
        if (StringUtils.isBlank(dateString) || fieldInfo == null || StringUtils.isBlank(format)) {
            throw new IllegalArgumentException("Null or empty arguments not allowed");
        }
        // Parse the string to see if the date is valid
        new SimpleDateFormat(format).parse(dateString);
        validate(dateString, fieldInfo);
    }

    public static String format(final String value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padEnd(
                StringUtils.left(value.toUpperCase(Locale.US), fieldInfo.getLength()),
                fieldInfo.getLength(),
                FormatUtil.SPACE_CHAR);
    }

    public static String formatKeepCase(final String value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padEnd(StringUtils.left(value, fieldInfo.getLength()), fieldInfo.getLength(), FormatUtil.SPACE_CHAR);
    }

    public static String formatRightJustified(final String value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padStart(
                StringUtils.left(value.toUpperCase(Locale.US), fieldInfo.getLength()),
                fieldInfo.getLength(),
                FormatUtil.SPACE_CHAR);
    }

    public static String formatFillZero(final String value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padEnd(StringUtils.left(value, fieldInfo.getLength()), fieldInfo.getLength(), FormatUtil.ZERO_CHAR);
    }

    public static String formatPadZero(final String value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padStart(StringUtils.left(value, fieldInfo.getLength()), fieldInfo.getLength(), FormatUtil.ZERO_CHAR);
    }

    public static String format(final Number value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padStart(StringUtils.left(String.valueOf(value), fieldInfo.getLength()), fieldInfo.getLength(), FormatUtil.ZERO_CHAR);
    }

    public static String formatLeftJustified(final Number value, final FieldInfo fieldInfo) {
        FormatUtil.validate(value, fieldInfo);
        return Strings.padEnd(StringUtils.left(String.valueOf(value), fieldInfo.getLength()), fieldInfo.getLength(), FormatUtil.SPACE_CHAR);
    }

    public static String formatDateTime(final Calendar cal, final FieldInfo fieldInfo, final String format) {
        return validateDateTime(cal, fieldInfo, format);
    }

    public static String formatPhoneNumber(final String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            return StringUtils.EMPTY;
        } else if (phoneNumber.length() == PHONE_NUMBER_LENGTH) {
            if (StringUtils.isNumeric(phoneNumber)) {
                // Example: 800-483-9493
                return phoneNumber.replaceFirst(PHONE_NUMBER_3PART_US_REGEX, "$1-$2-$3");
            } else {
                // Example: 800-EXAMPLE
                final String phoneUpperCase = phoneNumber.toUpperCase(Locale.US);
                return phoneUpperCase.replaceFirst(PHONE_NUMBER_2PART_US_REGEX, "$1-$2");
            }
        } else {
            return phoneNumber;
        }
    }

    public static Calendar convertToCalendar(final String dateString, final FieldInfo fieldInfo, final String format) throws ParseException {
        validateDateString(dateString, fieldInfo, format);

        // Parse the string to see if the date is valid
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat(format).parse(dateString));
        return cal;
    }

    public static String optionalField(final String value, final FieldInfo fieldInfo) {
        final String v = StringUtils.defaultString(value);
        FormatUtil.validate(v, fieldInfo);
        return v;
    }

    public static <T extends Number> T optionalField(final T value, final FieldInfo fieldInfo) {
        // Optional field, allow null
        if (value != null) {
            FormatUtil.validate(value, fieldInfo);
        }
        return value;
    }

    public static String optionalDateStringField(final String value, final FieldInfo fieldInfo, final String format) throws ParseException {
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }
        FormatUtil.validateDateString(value, fieldInfo, format);
        return value;
    }

    public static String removeLineBreaks(final String data) {
        return StringUtils.replaceChars(data, "\n\r", StringUtils.EMPTY);
    }

    public static String removeNonAscii(final String data) {
        return (data == null) ? null : Normalizer
                .normalize(data, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", StringUtils.EMPTY);
    }

    public static String clean(final String data) {
        return removeNonAscii(removeLineBreaks(data));
    }
}
