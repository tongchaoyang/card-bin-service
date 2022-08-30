package home.tong.card.bin.file.parser;


import home.tong.card.bin.enums.RecordTypeIndicator;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;

/**
 * First Data Global BIN File trailer record
 */
public class BinTrailerRecord extends BinRecord {
    private static final RecordTypeIndicator RECORD_TRAILER = RecordTypeIndicator.TRAILER;

    private int total;

    enum BinFileTrailerFieldInfo implements FieldInfo {
        RECORD_TYPE("Record Type", 1),
        TOTAL_COUNT("BIN Total Count", 10),
        FILLER("Filler", 239);

        private final String description;
        private final int length;

        BinFileTrailerFieldInfo(final String description, final int length) {
            this.description = "Global BIN File Trailer: " + description;
            this.length = length;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public int getLength() {
            return length;
        }
    }

    public BinTrailerRecord() {
        super();
    }

    public BinTrailerRecord(final String record) throws ParseException {
        super(record);
        parse();
    }

    @Override
    protected RecordTypeIndicator getDefaultRecordTypeIndicator() {
        return RECORD_TRAILER;
    }

    @Override
    protected void parseExtended() throws ParseException {
        setTotal(Integer.parseInt(getNextField(BinFileTrailerFieldInfo.TOTAL_COUNT)));
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(RECORD_TRAILER)
                .append(FormatUtil.format(total, BinFileTrailerFieldInfo.TOTAL_COUNT))
                .append(FormatUtil.format(StringUtils.SPACE, BinFileTrailerFieldInfo.FILLER))
                .append(System.lineSeparator())
                .toString();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = FormatUtil.optionalField(total, BinFileTrailerFieldInfo.TOTAL_COUNT);
    }
}
