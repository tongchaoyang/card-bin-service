package home.tong.card.bin.search.tree;

import home.tong.card.bin.file.parser.BinDetailRecord;
import lombok.EqualsAndHashCode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class RadixTreeNode extends TrieNode{
    @NonNull
    protected String label;

    public RadixTreeNode(final String label) {
        this.label = label;
    }

    private RadixTreeNode(final String label, final BinDetailRecord r) {
        this.label = label;
        record = r;
    }

    @Override
    public @Nullable RadixTreeNode getChild(final char c) {
        return children == null ? null : (RadixTreeNode) children[getIndex(c)];
    }

    boolean addChild(final RadixTreeNode child) {
        if (children == null) {
            children = new TrieNode[RADIX];
        }
        if (!hasChild(child.label.charAt(0))) {
            children[getIndex(child.label.charAt(0))] = child;
            return true;
        }
        return false;
    }

    boolean addChild(final String label, final BinDetailRecord r) {
        if (children == null) {
            children = new TrieNode[RADIX];
        }
        if (hasChild(label.charAt(0))) {
            return false;
        } else {
            children[getIndex(label.charAt(0))] = new RadixTreeNode(label, r);
            return true;
        }
    }

    @Nullable public RadixTreeNode removeChild(final RadixTreeNode node) {
        if (children == null) {
            return null;
        }

        final RadixTreeNode n = (RadixTreeNode) children[node.label.charAt(0) - '0'];
        if (n.label.equals(node.label)) {
            children[node.label.charAt(0) - '0'] = null;
            return n;
        } else {
            return null;
        }
    }

    public int numberOfChildren() {
        if (children == null)
            return 0;
        int c = 0;
        for (final TrieNode n : children) {
            if (n != null) {
                c++;
            }
        }
        return c;
    }

    public List<RadixTreeNode> getChildren() {
        final List<RadixTreeNode> r = new ArrayList<>();
        if (children == null) {
            return r;
        }
        for (final TrieNode n : children) {
            if (n != null) {
                r.add((RadixTreeNode) n);
            }
        }
        return r;
    }
}
