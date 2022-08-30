package home.tong.card.bin.file.parser;



import home.tong.card.bin.enums.RecordTypeIndicator;

import java.text.ParseException;

/**
 * First Data Global BIN File header record
 */
public class BinHeaderRecord extends BinRecord {
    private static final RecordTypeIndicator RECORD_TYPE_INDICATOR = RecordTypeIndicator.HEADER;
    private static final String DASH_FILLER = "-";
    // YYYY
    private String year;
    // MM
    private String month;
    // DD
    private String day;
    private String filler;

    enum BinFileHeaderFieldInfo implements FieldInfo {
        RECORD_TYPE("Record Type", 1),
        YEAR("Header Century and Year", 4),
        MONTH("Header Month", 2),
        DAY("Header Day", 2),
        DASH("Dash Filler", 1),
        FILLER("Filler", 239);

        private final String description;
        private final int length;

        BinFileHeaderFieldInfo(final String description, final int length) {
            this.description = "Global BIN File Header: " + description;
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

    public BinHeaderRecord() {
        super();
    }

    public BinHeaderRecord(final String record) throws ParseException {
        super(record);
        parse();
    }

    @Override
    protected RecordTypeIndicator getDefaultRecordTypeIndicator() {
        return RECORD_TYPE_INDICATOR;
    }

    @Override
    protected void parseExtended() throws ParseException {
        setYear(getNextField(BinFileHeaderFieldInfo.YEAR));
        getNextField(BinFileHeaderFieldInfo.DASH);
        setMonth(getNextField(BinFileHeaderFieldInfo.MONTH));
        getNextField(BinFileHeaderFieldInfo.DASH);
        setDay(getNextField(BinFileHeaderFieldInfo.DAY));
        setFiller(getNextField(BinFileHeaderFieldInfo.FILLER));
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getDefaultRecordTypeIndicator())
                .append(FormatUtil.format(getYear(), BinFileHeaderFieldInfo.YEAR))
                .append(DASH_FILLER)
                .append(FormatUtil.formatPadZero(getMonth(), BinFileHeaderFieldInfo.MONTH))
                .append(DASH_FILLER)
                .append(FormatUtil.formatPadZero(getDay(), BinFileHeaderFieldInfo.DAY))
                .append(FormatUtil.format(getFiller(), BinFileHeaderFieldInfo.FILLER))
                .append(System.lineSeparator())
                .toString();
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = FormatUtil.optionalField(year, BinFileHeaderFieldInfo.YEAR);
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(final String month) {
        this.month = FormatUtil.optionalField(month, BinFileHeaderFieldInfo.MONTH);
    }

    public String getDay() {
        return day;
    }

    public void setDay(final String day) {
        this.day = FormatUtil.optionalField(day, BinFileHeaderFieldInfo.DAY);
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(final String filler) {
        this.filler = FormatUtil.optionalField(filler, BinFileHeaderFieldInfo.FILLER);
    }
}
