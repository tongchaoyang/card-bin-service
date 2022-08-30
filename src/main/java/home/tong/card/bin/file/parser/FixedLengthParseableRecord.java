package home.tong.card.bin.file.parser;

import org.apache.commons.lang3.StringUtils;

/**
 * Fixed length parseable record
 */
public abstract class FixedLengthParseableRecord extends ParseableRecord {
    private int readStart;
    private int readEnd;

    public FixedLengthParseableRecord() {
    }

    public FixedLengthParseableRecord(final String record) {
        if (StringUtils.isEmpty(record)) {
            throw new IllegalArgumentException("Invalid input data. Unable to parse an empty BIN File record");
        }
        setRecord(record);
    }

    protected void reset() {
        // Reset the reading position
        readStart = 0;
        readEnd = 0;
    }

    protected String getNextField(final FieldInfo fieldInfo) {
        return StringUtils.stripEnd(getNextField(fieldInfo.getLength()), null);
    }

    protected String getNextField(final int fieldSize) {
        advance(fieldSize);
        return getRecord().substring(readStart, readEnd);
    }

    private void advance(final int fieldSize) {
        readStart = readEnd;
        readEnd += fieldSize;
    }
}
