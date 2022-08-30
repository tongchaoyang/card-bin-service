package home.tong.card.bin.file.parser;

import home.tong.card.bin.enums.RecordTypeIndicator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.ParseException;

/**
 * General parseable record
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class ParseableRecord {
    private String record;

    public ParseableRecord(final String record) {
        this.record = record;
    }

    /**
     * Identifies the default record type of the file record. This is what the record type
     * ought to be, not what was parsed from input data.
     */
    protected abstract RecordTypeIndicator getDefaultRecordTypeIndicator();

    /**
     * Allows derived classes to extend the parsing logic
     */
    protected abstract void parseExtended() throws ParseException;

    /**
     * Identifies the record type of the file record, e.g. H=Header, D=Detail, T=Trailer, etc.
     */
    protected abstract RecordTypeIndicator getRecordType();

    /**
     * Parses a record
     *
     * @throws ParseException
     */
    protected abstract void parse() throws ParseException;

    protected abstract void reset();

}
