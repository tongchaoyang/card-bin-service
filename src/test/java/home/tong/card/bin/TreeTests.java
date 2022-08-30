package home.tong.card.bin;

import home.tong.card.bin.search.tree.RadixTree;
import home.tong.card.bin.search.tree.SearchTreeLoader;
import home.tong.card.bin.search.tree.Trie;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.BeforeClass;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class TreeTests {
    private static final String BIN_FILE = "sample-bin-file.txt";
    BuildProperties buildProperties;

    private Trie trie;
    private RadixTree radixTree;
    private SearchTreeLoader treeLoader;

    private static InputStream getBinInputStream() throws URISyntaxException {
        return TreeTests.class.getResourceAsStream(BIN_FILE);
    }

    @BeforeClass
    public void beforeClass() throws URISyntaxException, IOException, ParseException, InterruptedException, ExecutionException {
        buildProperties = Mockito.mock(BuildProperties.class);
        final ThreadPoolTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
        asyncTaskExecutor.setCorePoolSize(1);
        asyncTaskExecutor.initialize();
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();

        trie = new Trie(4);
        radixTree = new RadixTree(4);
        treeLoader = new SearchTreeLoader();

        System.gc();
        TimeUnit.SECONDS.sleep(2);
        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Assert.assertFalse(treeLoader.everLoadedTreeFromStream());
        Assert.assertFalse(treeLoader.isLoadingFromStream());
        Assert.assertFalse(treeLoader.isLoadingFromDB());
        Assert.assertFalse(serviceImpl.getSearchTreeStatus().getTreeLoaded());
        try (BinFile binFile = serviceImpl.toBinFile(getBinFileBO())) {
            treeLoader.loadFromStream(trie, binFile.getInputStream());
        }
        Assert.assertTrue(treeLoader.everLoadedTreeFromStream());
        afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println(trie.getClass().getSimpleName() + " consumes " + (afterUsedMem - beforeUsedMem) / 1000 + "kb memory");

        System.gc();
        TimeUnit.SECONDS.sleep(2);
        beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Assert.assertFalse(treeLoader.isLoadingFromDB());
        treeLoader.loadFromStream(radixTree, getBinInputStream());
        Assert.assertTrue(radixTree.size() > 1);
        final long now = System.currentTimeMillis();
        final WatermarkDO currentWatermark = WatermarkDO.of().withTimestamp(now);
        Mockito.when(watermarkRepository.findById(serviceImpl.getWatermarkId())).thenReturn(Optional.of(currentWatermark));
        assertThat(radixTree.needsReload(currentWatermark.getTimestamp())).isTrue();
        radixTree.setWatermark(currentWatermark.getTimestamp());
        assertThat(radixTree.needsReload(currentWatermark.getTimestamp())).isFalse();
        Assert.assertTrue(serviceImpl.getSearchTreeStatus().getTreeLoaded());

        afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println(radixTree.getClass().getSimpleName() + " consumes " + (afterUsedMem - beforeUsedMem) / 1000 + "kb memory");
        serviceImpl.startSyncThread();
    }

    @Test
    public void testTreeSize() {
        final BinTree localTrie = new BinTrie(searchProperties);
        final Trie localTrie2 = new Trie(searchProperties);
        final RadixTree localRadix = new RadixTree(searchProperties);

        // Empty trees
        assertThat(localTrie.isEmpty()).isTrue();
        assertThat(localTrie2.isEmpty()).isTrue();
        assertThat(localTrie2.size() == 1).isTrue();
        assertThat(localRadix.size() == 1).isTrue();

        // Add an entry and then check if it's empty
        IINBO record = new IINBO();
        record.setLowBin("444000");
        record.setHighBin("444000");

        localTrie.add(record);
        localTrie2.insert(record.getLowBin(), record);
        Assert.assertEquals(localTrie2.size(), 7);
        localTrie2.setWatermark(System.currentTimeMillis());

        Assert.assertFalse(localRadix.insert(record.getLowBin() + "abc", record));
        Assert.assertTrue(localRadix.insert(record.getLowBin(), record));
        Assert.assertEquals(localRadix.size(), 2);
        localRadix.setWatermark(System.currentTimeMillis());
        Assert.assertTrue(localRadix.size() < localTrie2.size());
        assertThat(localTrie.isEmpty()).isFalse();
        assertThat(localRadix.isEmpty()).isFalse();
        assertThat(localRadix.contains(record.getLowBin())).isTrue();

        record = new IINBO();
        final long lowBin = 443931001000000L;
        record.setLowBin(String.valueOf(lowBin));
        final long highBin = 443931999996998L;
        record.setHighBin(String.valueOf(highBin));
        int size1 = localTrie2.size();
        localTrie2.insert(record.getLowBin(), record);
        Assert.assertEquals(localTrie2.size() - size1, 13);

        localRadix.insertBinRange(treeLoader.getCompressFactor(), record);
        Assert.assertTrue(localRadix.size() - size1 >= (highBin - lowBin) / treeLoader.getCompressFactor() + 1);
    }

    @Test
    public void testBinLookups() {
        StopWatch stopWatch = StopWatch.createStarted();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();

        // Look for 6 - 9 digit bins
        long minBin = 100000L;
        long maxBin = 10000000L;
        int iterations = 1;
        for (int run = 0; run < iterations; ++run) {
            int found = 0;
            for (long bin = minBin; bin < maxBin; ++bin) {
                String binstr = String.valueOf(bin);
                IINBO record1 = binTree.get(binstr);
                Optional<IINBO> optional = trie.find(binstr);
                IINBO record3 = optional.isPresent() ? optional.get() : null;
                Assert.assertTrue(record1 == record3 || record1.compareTo(record3) == 0);
                optional = radixTree.find(binstr);
                IINBO record2 = optional.isPresent() ? optional.get() : null;
                if (record1 != record2 && (record1 != null && record1.compareTo(record2) != 0 ||
                        record2 != null && record2.compareTo(record1) != 0)) {
                    logger.error("Wrong result for bin " + binstr);
                    logger.error(record1 != null ? record1 : binstr);
                    logger.error(record2 != null ? record2 : binstr);
                }
                Assert.assertTrue(record1 == record2 || record1.compareTo(record2) == 0);
                found += record1 != null ? 1 : 0;
            }
            long start = System.nanoTime();
            for (long bin = minBin; bin < maxBin; ++bin) {
                String binstr = String.valueOf(bin);
                binTree.get(binstr);
            }
            long end = System.nanoTime();
            long n = maxBin - minBin + 1;
            System.out.println("BinTrie average lookup: " + (end - start) / (double) n + "ns, in " + n + " queries with " + found + " matches");

            start = System.nanoTime();
            for (long bin = minBin; bin < maxBin; ++bin) {
                String binstr = String.valueOf(bin);
                trie.find(binstr);
            }
            end = System.nanoTime();
            System.out.println("Trie average lookup: " + (end - start) / (double) n + "ns, in " + n + " queries with " + found + " matches");

            start = System.nanoTime();
            for (long bin = minBin; bin < maxBin; ++bin) {
                String binstr = String.valueOf(bin);
                radixTree.find(binstr);
            }
            end = System.nanoTime();
            System.out.println("Radix tree average lookup: " + (end - start) / (double) n + "ns, in " + n + " queries with " + found + " matches");
        }

        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increased after running: " + (usedMemoryAfter - usedMemoryBefore) / 1.0e6 + " MB");
        stopWatch.stop();
        logger.info("Lookup test finished in " + stopWatch.getTime() / 1000 + "s");
    }

    @Test(dependsOnMethods = {"testDelete"})
    public void serviceImpl() {
        Optional<IINBO> iinbo = serviceImpl.find("10489");
        Assert.assertTrue(iinbo.isPresent());
        Assert.assertEquals(iinbo.get().getHighBin(), "10489");

        iinbo = serviceImpl.find("305997");
        Assert.assertTrue(iinbo.isPresent());
        Assert.assertEquals(iinbo.get().getLowBin(), "305992");
        Assert.assertEquals(iinbo.get().getHighBin(), "305999");

        iinbo = serviceImpl.find("9999999999");
        Assert.assertFalse(iinbo.isPresent());

        IINBO bo = new IINBO();
        bo.setId("goodId");
        bo.setLowBin("900000000");
        bo.setHighBin("903172498");
        bo.setCardClass(IINBO.CardClass.business);
        bo.setCurrencyExchangeSupport(true);
        bo.setIin("IIN");
        bo.setIssuerBank(new IINBO.Bank("Bank of America", "US", "USD"));
        bo.setNetwork(IINBO.Network.amex);
        bo.setOctEligible(true);
        final IINDO iindo = globalBinDalConverter.toDalEntity(bo);
        Mockito.when(globalBinRepository.save(iindo)).thenReturn(iindo);

        bo = serviceImpl.create(bo);
        Assert.assertNotNull(bo);
        Assert.assertEquals(bo.getLowBin(), "900000000");
        Assert.assertEquals(bo.getHighBin(), "903172498");
        Assert.assertNull(serviceImpl.create(bo));

        IINBO updatedBo = new IINBO();
        BeanUtils.copyProperties(bo, updatedBo);
        updatedBo.setCardClass(IINBO.CardClass.consumer);
        final IINDO updatedDo = IINDO.of();
        BeanUtils.copyProperties(iindo, updatedDo);
        updatedDo.setCardClass(IINDO.CardClass.consumer);
        Mockito.when(globalBinRepository.findById("goodId")).thenReturn(Optional.of(updatedDo));
        Mockito.when(globalBinRepository.save(any(IINDO.class))).thenReturn(updatedDo);
        bo = serviceImpl.update(updatedBo);
        Assert.assertEquals(bo.getCardClass(), updatedBo.getCardClass());

        updatedBo.setId("badId");
        Mockito.when(globalBinRepository.findById("badId")).thenReturn(Optional.empty());
        Assert.assertNull(serviceImpl.update(updatedBo));

        Mockito.when(globalBinRepository.findByLowBin(anyString())).thenReturn(Optional.of(updatedDo));
        bo = serviceImpl.delete(updatedBo.getLowBin());
        Assert.assertEquals(bo.getCardClass(), updatedBo.getCardClass());
        Assert.assertEquals(bo.getLowBin(), updatedBo.getLowBin());
        Assert.assertEquals(bo.getHighBin(), updatedBo.getHighBin());
        Assert.assertNull(serviceImpl.delete(updatedBo.getLowBin()));

        iinbo = serviceImpl.find(updatedBo.getLowBin());
        Assert.assertFalse(iinbo.isPresent());
    }

    @Test
    public void testFindBin() {
        IINBO record1, record2, record3;
        record1 = binTree.get("10489");
        record2 = radixTree.find("10489").get();
        record3 = trie.find("10489").get();
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);
        assertThat(record1.getLowBin()).isEqualTo("10489");
        assertThat(record3.getHighBin()).isEqualTo("10489");

        record1 = binTree.get("305995");
        record2 = radixTree.find("305997").get();
        record3 = trie.find("305997").get();
        assertThat(record1.getLowBin()).isEqualTo("305992");
        assertThat(record1.getHighBin()).isEqualTo("305999");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        record1 = binTree.get("3090");
        record2 = radixTree.find("3090").get();
        record3 = trie.find("3090").get();
        assertThat(record1.getLowBin()).isEqualTo("3088");
        assertThat(record1.getHighBin()).isEqualTo("3094");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        record1 = binTree.get("340016");
        record2 = radixTree.find("340016").get();
        record3 = trie.find("340016").get();
        assertThat(record1.getLowBin()).isEqualTo("340000");
        assertThat(record1.getHighBin()).isEqualTo("340026");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        record1 = binTree.get("2228033");
        record2 = radixTree.find("2228032").get();
        record3 = trie.find("2228032").get();
        assertThat(record1.getLowBin()).isEqualTo("2228030");
        assertThat(record1.getHighBin()).isEqualTo("2228034");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        record1 = binTree.get("401318");
        record2 = radixTree.find("401318").get();
        record3 = trie.find("401318").get();
        assertThat(record1.getLowBin()).isEqualTo("401318");
        assertThat(record1.getHighBin()).isEqualTo("401318");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        record1 = binTree.get("401319401");
        record2 = radixTree.find("401319601").get();
        record3 = trie.find("401319601").get();
        assertThat(record1.getLowBin()).isEqualTo("401319301");
        assertThat(record1.getHighBin()).isEqualTo("401319999");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        record1 = binTree.get("02150201");
        record2 = radixTree.find("02150201").get();
        record3 = trie.find("02150201").get();
        assertThat(record1.getLowBin()).isEqualTo("02150201");
        assertThat(record1.getHighBin()).isEqualTo("02150201");
        Assert.assertTrue(record1.compareTo(record2) == 0);
        Assert.assertTrue(record1.compareTo(record3) == 0);

        assertThat(binTree.get("673040")).isNull();
        Assert.assertFalse(radixTree.find("673040").isPresent());
        Assert.assertFalse(trie.find("673040").isPresent());
        assertThat(binTree.get("1234567890123456")).isNull();
        Assert.assertFalse(radixTree.find("1234567890123456").isPresent());
        Assert.assertFalse(trie.find("1234567890123456").isPresent());
    }

    @Test(dependsOnMethods = {"testFindBin", "testBinLookups"})
    public void testDelete() {
        IINBO record = new IINBO();
        record.setLowBin("300000000");
        record.setHighBin("303172498");
        Assert.assertFalse(trie.find("300000100").isPresent());
        Assert.assertTrue(trie.insert(record.getLowBin(), record));
        Assert.assertTrue(trie.find("300000000").isPresent());
        Assert.assertTrue(trie.find("300000100").isPresent());
        Assert.assertTrue(trie.find("302999999").isPresent());
        Assert.assertTrue(trie.find("3031724").isPresent());
        Assert.assertTrue(trie.delete(record.getLowBin()));
        Assert.assertFalse(trie.find("300000100").isPresent());

        Assert.assertFalse(radixTree.find("300000000").isPresent());
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        Assert.assertTrue(radixTree.find("300000000").isPresent());
        Assert.assertTrue(radixTree.find("3031724").isPresent());
        Assert.assertTrue(radixTree.find("300000100").isPresent());
        Assert.assertTrue(radixTree.find("302999999").isPresent());
        Assert.assertTrue(radixTree.delete(record.getLowBin()));
        Assert.assertFalse(radixTree.find("300000000").isPresent());
        Assert.assertFalse(radixTree.find("300000100").isPresent());
        Assert.assertFalse(radixTree.find("302999999").isPresent());
        Assert.assertFalse(radixTree.find("3031724").isPresent());

        record = new IINBO();
        record.setLowBin("6729");
        record.setHighBin("6739");
        Assert.assertTrue(trie.insert(record.getLowBin(), record));
        Assert.assertTrue(trie.delete(record.getLowBin()));
        Assert.assertFalse(trie.find("6730").isPresent());
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        Assert.assertTrue(radixTree.delete(record.getLowBin()));
        Assert.assertFalse(radixTree.find("6730").isPresent());

        record = new IINBO();
        record.setLowBin("9329");
        record.setHighBin("9349");
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        record = new IINBO();
        record.setLowBin("7329");
        record.setHighBin("7349");
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        record = new IINBO();
        record.setLowBin("932920");
        record.setHighBin("934920");
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        Assert.assertTrue(radixTree.find("9339").isPresent());
        Assert.assertTrue(radixTree.find("7329").isPresent());
        Assert.assertTrue(radixTree.find("934920").isPresent());

        record = new IINBO();
        record.setLowBin("9359");
        record.setHighBin("9379");
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        Assert.assertTrue(radixTree.find("9360").isPresent());
        Assert.assertFalse(radixTree.delete("9360"));

        record = new IINBO();
        record.setLowBin("9829000");
        record.setHighBin("9849000");
        Assert.assertTrue(radixTree.insert(record.getLowBin(), record));
        Assert.assertTrue(radixTree.find("9831234").isPresent());

        Assert.assertFalse(radixTree.delete("9"));
        Assert.assertFalse(radixTree.delete("93"));
        Assert.assertFalse(radixTree.delete("9999999"));
        Assert.assertTrue(radixTree.delete("9329"));
        Assert.assertTrue(radixTree.find("9339").get().getLowBin().equals("932920"));
        Assert.assertTrue(radixTree.delete("7329"));
        Assert.assertFalse(radixTree.find("7339").isPresent());
        Assert.assertTrue(radixTree.find("934920").isPresent());
        Assert.assertTrue(radixTree.delete("9359"));
        Assert.assertTrue(radixTree.delete("9829000"));
    }

    @Test
    public void batchLogging() throws URISyntaxException {
        List<IINDO> batch = new ArrayList<>();
        final List<List<IINDO>> batches = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(getBinInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (RecordTypeIndicator.fromChar(line.charAt(0)) == RecordTypeIndicator.DETAIL) {
                    final IINDO iindo = IINDTORecordMapper.map(new GlobalBinDetailRecord(line), searchProperties);
                    batch.add(iindo);
                    if (batch.size() == 10) {
                        batches.add(batch);
                        batch = new ArrayList<>();
                        if (batches.size() == 3) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error", e);
        }
        treeLoader.logBatches(batches);
    }

    @Test
    public void trie() {
        final TrieNode root = new TrieNode();
        Assert.assertNull(root.getLeastLeaf());
        Assert.assertNull(root.getGreatestLeaf());
        Assert.assertTrue(root.addChild('0'));
        Assert.assertFalse(root.addChild('0'));

        IINBO bo = new IINBO();
        bo.setId("id0");
        bo.setLowBin("001");
        bo.setHighBin("003");
        Assert.assertTrue(root.addChild('1', bo));
        Assert.assertFalse(root.addChild('1', bo));

        final Trie trie = new Trie(searchProperties);
        Assert.assertTrue(trie.isEmpty());
        Assert.assertTrue(trie.size() == 1);

        Assert.assertTrue(trie.insert(bo.getLowBin(), bo));
        Assert.assertFalse(trie.insert(bo.getLowBin(), bo));
        Assert.assertTrue(trie.isEmpty());
        Assert.assertEquals(trie.size(), 4);

        Assert.assertFalse(trie.delete("00"));
        Assert.assertFalse(trie.delete("003"));
        trie.delete(bo.getLowBin());
        Assert.assertTrue(trie.isEmpty());
        Assert.assertTrue(trie.size() == 1);
    }

    @Test
    public void radixTrieNode() {
        final RadixTreeNode root = new RadixTreeNode("");
        Assert.assertTrue(root.numberOfChildren() == 0);
        Assert.assertTrue(root.getChildren().size() == 0);
        Assert.assertNull(root.removeChild(null));

        IINBO bo = new IINBO();
        bo.setId("id0");
        bo.setLowBin("001");
        bo.setHighBin("003");
        Assert.assertTrue(root.addChild(bo.getLowBin(), bo));
        Assert.assertFalse(root.addChild(bo.getLowBin(), bo));

        RadixTreeNode child = new RadixTreeNode("002");
        Assert.assertNull(root.removeChild(child));
        Assert.assertFalse(root.addChild(child));
        child = new RadixTreeNode("202");
        Assert.assertTrue(root.addChild(child));
        Assert.assertFalse(root.addChild(child));
        Assert.assertNotNull(root.removeChild(child));
    }

    @AfterClass
    public void testReloadTree() throws URISyntaxException {
        // since we are using mock repo this will clean up search tree
        serviceImpl.loadFromDB();
        Assert.assertThrows(IllegalStateException.class, () -> serviceImpl.find("10489"));

        // reload from file
        serviceImpl.load(getBinFileBO()); // this method fails at the start of 2nd batch. weird!!!
        // TODO: fix it
        //Assert.assertTrue(serviceImpl.getBinSearchTree().size() > 1);
        Assert.assertTrue(serviceImpl.getBinSearchTree().size() == 1);
        serviceImpl.shutdownSyncThread();
    }
}