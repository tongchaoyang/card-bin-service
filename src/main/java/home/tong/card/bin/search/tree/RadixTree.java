package home.tong.card.bin.search.tree;

import home.tong.card.bin.file.parser.BinDetailRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class RadixTree extends SearchTree {
    protected final RadixTreeNode root = new RadixTreeNode("");

    public RadixTree(int compressExponent) {
        super(compressExponent);
    }

    @Override
    public boolean isEmpty() {
        return Objects.isNull(root.children) || Arrays.stream(root.children).allMatch(Objects::isNull);
    }

    @Override
    public synchronized boolean insert(final String key, final BinDetailRecord record) {
        log.info("inserting key {} into tree", key);
        if (!StringUtils.isNumeric(key)) {
            log.error("Invalid key {} is ignored", key);
            return false;
        }
        RadixTreeNode node = root;
        RadixTreeNode parent = null;
        int i = 0;
        while (i < key.length()) {
            final char c = key.charAt(i);
            if (!node.hasChild(c)) {
                final String label = key.substring(i);
                node.addChild(label, record);
                nodeCount++;
                return true;
            } else {
                parent = node;
                node = node.getChild(c);
                if (key.startsWith(node.label, i)) { //NOSONAR
                    i += node.label.length();
                } else {
                    int j = 0;
                    while (j < node.label.length() && i + j < key.length() && node.label.charAt(j) == key.charAt(i + j)) {
                        j++;
                    }
                    parent.removeChild(node);
                    final RadixTreeNode innerNode = new RadixTreeNode(node.label.substring(0, j));
                    parent.addChild(innerNode);
                    nodeCount++;
                    node.label = node.label.substring(j);
                    innerNode.addChild(node);
                    final String label = key.substring(i + j);
                    if (label.isEmpty()) {
                        innerNode.record = record;
                    } else {
                        innerNode.addChild(label, record);
                        nodeCount++;
                    }
                    return true;
                }
            }
        }

        if (node.record != null) {
            log.warn("Duplicate key {}", record);
            return false;
        }
        node.record = record;
        return true;
    }

    @Override
    public synchronized boolean delete(final String key) {
        log.info("deleting key {} from tree", key);
        RadixTreeNode node = root;
        final Deque<RadixTreeNode> stack = new ArrayDeque<>();
        int i = 0;
        while (i < key.length()) {
            final char c = key.charAt(i);
            stack.push(node);
            node = node.getChild(c);
            if (node == null) {
                log.warn("BIN {} does not exists in search tree", key);
                return false;
            }
            i += node.label.length();
        }

        if (!node.isLeaf() && node.record == null) {
            log.warn("BIN {} points to a non-leaf node. Only leaf node can be deleted", key);
            return false;
        }

        node.record = null;
        final RadixTreeNode parent;
        if (node.isLeaf()) {
            parent = stack.pop();
            parent.removeChild(node);
            nodeCount--;
        } else {
            parent = node;
        }

        if (parent.numberOfChildren() == 1) {
            combine(parent);
        }
        return true;
    }

    private synchronized void combine(final RadixTreeNode node) {
        final RadixTreeNode child = node.getChildren().get(0);
        node.label = node.label + child.label;
        node.removeChild(child);
        nodeCount--;
        if (child.isLeaf()) {
            node.record = child.record;
        } else {
            child.getChildren().forEach(node::addChild);
        }
    }

    @Override
    public Optional<BinDetailRecord> find(final String key) {
        RadixTreeNode node = root;
        final Deque<RadixTreeNode> stack = new ArrayDeque<>();
        String remainder = key;
        int index = 0;
        boolean brokenOut = false;
        boolean incrementedByLabel = false;
        while (index < key.length()) {
            final char c = key.charAt(index);
            stack.push(node);
            node = node.getChild(c);
            if (node == null) {
                brokenOut = true;
                break;
            }
            remainder = key.substring(index);
            if (remainder.startsWith(node.label)) {
                index += node.label.length();
                incrementedByLabel = true;
            } else if (node.label.startsWith(remainder)) {
                index += remainder.length();
                incrementedByLabel = false;
            } else {
                brokenOut = true;
                break;
            }
        }

        if (!brokenOut) {
            if (incrementedByLabel) {
                index -= node.label.length();
            } else {
                index -= remainder.length();
            }
        }

        if (node != null) {
            RadixTreeNode leaf = node;
            if (remainder.compareTo(node.label) <= 0) {
                if (!node.isLeaf()) {
                    leaf = (RadixTreeNode) node.getLeastLeaf();
                }
            } else {
                if (!node.isLeaf()) {
                    leaf = (RadixTreeNode) node.getGreatestLeaf();
                }
            }
            return getData(key, stack, index, leaf);//NOSONAR
        }

        return backtrack(stack, key, index);
    }

    protected <T extends TrieNode> Optional<BinDetailRecord> backtrack(final Deque<T> stack, final String key, final int index) {
        int tries = Math.min(index - 1, compressExponent);
        int i = index;
        while (tries > 0 && !stack.isEmpty()) {
            final RadixTreeNode node = (RadixTreeNode) stack.pop();
            if (node.isLeaf() || node.record != null) {
                if (isInRange(key, node.record)) {
                    return Optional.of(node.record);
                }
                if (node.isLeaf()) {
                    return Optional.empty();
                }
            }

            tries--;
            final char c = key.charAt(i);
            i -= node.label.length();
            int j = c - '1'; // don't repeat previous path
            while (j >= 0) {
                final RadixTreeNode child = (RadixTreeNode) node.children[j]; //NOSONAR
                if (child != null) {
                    if ((child.isLeaf() || child.record != null) &&
                        (isInRange(key, child.record) || isPrefix(key, child.record))) {
                        return Optional.of(child.record);
                    }
                    if (!child.isLeaf()){
                        final RadixTreeNode greatest = (RadixTreeNode) child.getGreatestLeaf();
                        if (greatest != null && (isInRange(key, greatest.record) || isPrefix(key, greatest.record))) {
                            return Optional.of(greatest.record);
                        }
                    }
                    return Optional.empty();
                }
                j--;
            }
        }
        return Optional.empty();
    }
}
