package home.tong.card.bin.search.tree;

import home.tong.card.bin.file.parser.BinDetailRecord;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

@EqualsAndHashCode
class TrieNode {
    static final int RADIX = 10;
    @Nullable
    protected TrieNode[] children;
    @Nullable
    protected BinDetailRecord record;

    public TrieNode() {
    }

    public TrieNode(final BinDetailRecord r) {
        record = r;
    }

    BinDetailRecord record() {
        return record;
    }

    static int getIndex(final char c) {
        return c - '0';
    }

    public boolean hasChild(final char c) {
        return children != null && children[getIndex(c)] != null;
    }

    public boolean addChild(final char c) {
        if (children == null) {
            children = new TrieNode[RADIX];
        }
        if (hasChild(c)) {
            return false;
        } else {
            children[getIndex(c)] = new TrieNode();
            return true;
        }
    }

    public boolean addChild(final char c, final BinDetailRecord r) {
        if (children == null) {
            children = new TrieNode[RADIX];
        }
        if (hasChild(c)) {
            return false;
        } else {
            children[getIndex(c)] = new TrieNode(r);
            return true;
        }
    }

    public @Nullable TrieNode getChild(final char c) {
        return children == null ? null : children[getIndex(c)];
    }

    public @Nullable TrieNode getGreatestLeaf() {
        if (children == null) {
            return null;
        }

        for (int i = children.length - 1; i >= 0; i--) {
            final TrieNode child = children[i];
            if (child != null) {
                if (child.isLeaf()) {
                    return child;
                } else {
                    return child.getGreatestLeaf();
                }
            }
        }
        return null;
    }

    public boolean isLeaf() {
        if (children == null)
            return true;
        for (final TrieNode n : children) {
            if (n != null) {
                return false;
            }
        }
        return true;
    }

    public @Nullable TrieNode getLeastLeaf() {
        if (children == null) {
            return null;
        }

        for (final TrieNode child : children) {
            if (child != null) {
                if (child.isLeaf()) {
                    return child;
                } else {
                    return child.getLeastLeaf();
                }
            }
        }
        return null;
    }
}
