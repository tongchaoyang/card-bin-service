package home.tong.card.bin;

import home.tong.card.bin.search.tree.RadixTree;
import org.junit.jupiter.api.Test;

public class RadixTreeTests {
    private final RadixTree tree = new RadixTree(null);

    @Test
    public void insert() {
        assertThat(tree.size()).isEqualTo(1);
        final IINBO bo1 = new IINBO();
        final String bin1 = "1010";
        bo1.setLowBin(bin1);
        bo1.setHighBin("1111");
        tree.insert(bin1, bo1);
        assertThat(tree.size()).isEqualTo(2);

        final IINBO bo2 = new IINBO();
        final String bin2 = "2345";
        bo2.setLowBin(bin2);
        bo2.setHighBin("2400");
        tree.insert(bin2, bo2);
        assertThat(tree.size()).isEqualTo(3);

        final IINBO bo3 = new IINBO();
        final String bin3 = "30";
        bo3.setLowBin(bin3);
        bo3.setHighBin("40");
        tree.insert(bin3, bo3);
        assertThat(tree.size()).isEqualTo(4);

        final IINBO bo4 = new IINBO();
        final String bin4 = "101264";
        bo4.setLowBin(bin4);
        bo4.setHighBin("102400");
        tree.insert(bin4, bo4);
        // inner node will be added
        assertThat(tree.size()).isEqualTo(6);

        final IINBO bo5 = new IINBO();
        final String bin5 = "101";
        bo5.setLowBin(bin5);
        bo5.setHighBin("120");
        tree.insert(bin5, bo5);
        // no new node is added, this might not be a valid use case for BIN file
        assertThat(tree.size()).isEqualTo(6);

        final IINBO bo6 = new IINBO();
        final String bin6 = "10126";
        bo5.setLowBin(bin6);
        bo5.setHighBin("10200");
        // this caused string out of bound exception
        tree.insert(bin6, bo6);
        assertThat(tree.size()).isEqualTo(7);
    }
}
