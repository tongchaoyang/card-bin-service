package home.tong.card.bin.search.tree;

import home.tong.card.bin.file.parser.BinDetailRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Deque;
import java.util.Optional;

/**
 * Tree-based data structures for storing and looking up data with a string key
 * data are sorted by the natural order of their keys
 */
@Slf4j
public abstract class SearchTree {
    protected int nodeCount = 1; // root node
    protected int compressExponent = 4;

    protected SearchTree(final int compressExponent) {
        this.compressExponent = compressExponent;
    }

    public int size() {
        return nodeCount;
    }

    public abstract boolean isEmpty();

    public abstract boolean insert(final String key, final BinDetailRecord data);

    public abstract boolean delete(final String key);

    public boolean contains(final String key) {
        return find(key).isPresent();
    }

    public abstract Optional<BinDetailRecord> find(final String key);

    public boolean isInRange(final String key, final BinDetailRecord iinbo) {
        final String lowBin = iinbo.getLowBin();
        if (key.length() > lowBin.length()) {
            return false;
        }
        final String highBin = iinbo.getHighBin();
        final String paddedKey = StringUtils.rightPad(key, lowBin.length(), '0');
        return lowBin.compareTo(paddedKey) <= 0 && paddedKey.compareTo(highBin) <= 0;
    }

    public boolean isPrefix(final String key, final BinDetailRecord iinbo) {
        final String lowBin = iinbo.getLowBin();
        if (key.length() > lowBin.length()) {
            return false;
        }
        final String highBin = iinbo.getHighBin();
        return lowBin.startsWith(key) || highBin.startsWith(key);
    }

    public synchronized boolean insertBinRange(final int compressFactor, final BinDetailRecord data) {
        if (!insert(data.getLowBin(), data)) {
            log.warn("There is an existing BIN range with low BIN: {}", data.getLowBin());
            return false;
        }

        int count = 1;
        final int length = data.getLowBin().length();
        final long binLow = Long.parseLong(data.getLowBin());
        final long binHigh = Long.parseLong(data.getHighBin());
        if (binHigh - binLow > compressFactor) {
            long bin;
            for (bin = binLow + 1; bin <= binHigh; ++bin) {
                if (bin % compressFactor == 0) {
                    final String binStr = String.valueOf(bin);
                    if (!insert(StringUtils.leftPad(binStr, length, '0'), data)) {
                        log.warn("Duplicate bin record: {}", data);
                        return false;
                    }
                    count++;
                    break;
                }
            }

            for (bin += compressFactor; bin <= binHigh; bin += compressFactor) {
                final String binStr = String.valueOf(bin);
                if (!insert(StringUtils.leftPad(binStr, length, '0'), data)) {
                    log.warn("Duplicate bin record: {}", data);
                    return false;
                }
                count++;
            }

            if (count > 1000) { // to reduce logging
                log.info("Inserted {} nodes in search tree for BIN range [{}, {}]", count, binLow, binHigh);
            }
        }
        return true;
    }

    protected abstract <T extends TrieNode> Optional<BinDetailRecord> backtrack(final Deque<T> stack, final String key, final int index);

    protected <T extends TrieNode> Optional<BinDetailRecord> getData(final String key, final Deque<T> stack, final int index, final TrieNode leaf) {
        if (isInRange(key, leaf.record)) {
            return Optional.of(leaf.record());
        }
        // The previous entry in the tree could be better match than this one. Here is a corner case:
        // search with key 222899, found a match (2228995, 2228999), this seems a good match. Actually,
        // there is a previous entry (2228980, 2228994) that is a better match
        final Optional<BinDetailRecord> r = backtrack(stack, key, index);
        if (r.isPresent() && isInRange(key, r.get())) {
            return r;
        }
        if (isPrefix(key, leaf.record)) {
            return Optional.of(leaf.record);
        }
        if (r.isPresent() && isPrefix(key, r.get())) {
            return r;
        }
        return Optional.empty();
    }
}
