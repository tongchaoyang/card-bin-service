package home.tong.card.bin.file.parser;

/**
 * Field Information interface
 *
 * Each field in the First Data Compass records will have a description
 * and a length, in characters
 */
public interface FieldInfo {
    String getDescription();
    int getLength();
}
