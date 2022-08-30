package home.tong.card.bin.file.parser;

import home.tong.card.bin.enums.RecordTypeIndicator;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

/**
 * Global BIN parseable record
 */
public abstract class BinRecord extends FixedLengthParseableRecord {

    public BinRecord() {
        super();
    }

    public BinRecord(final String record) {
        super(record);
    }

    /**
     * Identifies the record type of the file record, e.g. H=Header, D=Detail, T=Trailer, etc.
     */
    @Override
    public RecordTypeIndicator getRecordType() {
        return StringUtils.isNotBlank(getRecord()) ? getRecordIndicator(getRecord()) : getDefaultRecordTypeIndicator();
    }

    public static RecordTypeIndicator getRecordIndicator(final String record) {
        return StringUtils.isNotBlank(record) ? RecordTypeIndicator.fromChar(record.charAt(0)) : RecordTypeIndicator.DEFAULT;
    }

    @Override
    protected final void parse() throws ParseException {
        // Reset the reading position
        reset();

        final String actualType = getNextField(1);
        final String expectedType = getDefaultRecordTypeIndicator().toString();
        if (!StringUtils.equals(actualType, expectedType)) {
            throw new ParseException("Invalid Record Type Indicator encountered. Expecting " + expectedType, 0);
        }
        parseExtended();
    }
}
