package home.tong.card.bin.search.tree;

import home.tong.card.bin.file.parser.BinDetailRecord;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class Trie extends SearchTree {
    protected final TrieNode root = new TrieNode();

    public Trie(int compressExponent) {
        super(compressExponent);
    }

    /**
     * Returns true if root has no children
     */
    @Override
    public boolean isEmpty() {
        return Objects.isNull(root.children) || size() == 1;
    }

    @Override
    public synchronized boolean insert(final String key, final BinDetailRecord iinbo) {
        TrieNode node = root;
        for (int i = 0; i < key.length(); ++i) {
            final char c = key.charAt(i);
            if (!node.hasChild(c)) { //NOSONAR
                if (i == key.length() - 1) {
                    // only store data at leafs
                    node.addChild(c, iinbo);
                    nodeCount++;
                    return true;
                } else {
                    node.addChild(c);
                    nodeCount++;
                }
            }
            node = node.getChild(c);
        }
        return false;
    }

    @Override
    public synchronized boolean delete(final String key) {
        TrieNode node = root;
        final Deque<TrieNode> stack = new ArrayDeque<>();
        char c;
        int i = 0;
        for (; i < key.length(); ++i) {
            c = key.charAt(i);
            stack.push(node);
            node = node.getChild(c);
            if (node == null) {
                log.warn("BIN {} does not exists in search tree", key);
                return false;
            }
        }

        if (!node.isLeaf()) {
            log.warn("BIN {} points to a non-leaf node. Only leaf node can be deleted", key);
            return false;
        }
        TrieNode parent;
        do {
            parent = stack.pop();
            c = key.charAt(--i);
            parent.children[c - '0'] = null; //NOSONAR
            nodeCount--;
        } while (!stack.isEmpty() && parent.isLeaf());
        return true;
    }

    @Override
    public Optional<BinDetailRecord> find(final String key) {
        TrieNode node = root;
        final Deque<TrieNode> stack = new ArrayDeque<>();
        int i = 0;
        while (i < key.length() && node != null) {
            final char c = key.charAt(i++);
            stack.push(node);
            node = node.getChild(c);
        }
        --i;

        if (node != null) {
            TrieNode leaf = node;
            if (!node.isLeaf()) {
                leaf = node.getLeastLeaf();
            }
            return getData(key, stack, i, leaf); //NOSONAR
        }

        return backtrack(stack, key, i);
    }

    protected <T extends TrieNode> Optional<BinDetailRecord> backtrack(final Deque<T> stack, final String key, final int index) {
        int tries = Math.min(index - 1, compressExponent);
        int i = index;
        while (tries > 0 && !stack.isEmpty()) {
            tries--;
            final TrieNode node = stack.pop();
            if (node.isLeaf()) {
                if (isInRange(key, node.record)) {
                    return Optional.of(node.record);
                }
                return Optional.empty();
            }

            final char c = key.charAt(i--);
            int j = c - '1'; // don't repeat previous path
            while (j >= 0) {
                final TrieNode child = node.children[j]; //NOSONAR
                if (child != null) {
                    if (child.isLeaf()) {
                        if (isInRange(key, child.record) || isPrefix(key, child.record)) {
                            return Optional.of(child.record);
                        }
                    } else {
                        final TrieNode greatest = child.getGreatestLeaf();
                        if (greatest != null && (isInRange(key, greatest.record) || isPrefix(key, greatest.record))) {
                            return Optional.of(greatest.record);
                        }
                    }
                    break;
                }
                j--;
            }
        }
        return Optional.empty();
    }
}
