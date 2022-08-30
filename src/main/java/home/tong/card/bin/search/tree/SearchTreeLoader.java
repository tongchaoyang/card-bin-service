package home.tong.card.bin.search.tree;

import home.tong.card.bin.enums.RecordTypeIndicator;
import home.tong.card.bin.file.parser.BinDetailRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Slf4j
public class SearchTreeLoader {
    private boolean loadingFromStream;
    private boolean everLoaded;
    private volatile int progressRatio;

    public SearchTreeLoader() {
    }

    public synchronized void loadFromStream(final SearchTree tree, final InputStream inputStream) {
        loadingFromStream = true;
        progressRatio = 0;
        log.info("Loading BINs for {} from input stream", tree.getClass().getSimpleName());
        final StopWatch stopWatch = StopWatch.createStarted();
        final int[] counter = {0};
        final int compressFactor = getCompressFactor();
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            bis.mark(Integer.MAX_VALUE);
            final BufferedReader br0 = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
            final int total = (int) br0.lines().count() - 2;
            bis.reset();
            final BufferedReader br = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8));
            br.lines().filter(line -> RecordTypeIndicator.fromChar(line.charAt(0)) == RecordTypeIndicator.DETAIL).
                    forEach(record -> {
                        try {
                            final BinDetailRecord rec = new BinDetailRecord(record);
                            if (tree.insertBinRange(compressFactor, rec)) {
                            } else {
                                log.warn("Failed to insert {} into search tree", rec);
                            }
                        } catch (ParseException e) {
                            log.error("After {} records are loaded into database, aborted loading due to exception", counter[0], e);
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            progressRatio = 100;
            loadingFromStream = false;
        }
        stopWatch.stop();
        log.info("Loaded {} BIN records for {} from input stream in {} minutes. Tree size is {}", counter[0], tree.getClass().getSimpleName(),
                stopWatch.getTime() / 1000 / 60, tree.size());
        everLoaded = true;
    }

    public int getCompressFactor() {
        int compressFactor = 1;
        for (int i = 0; i < compressFactor; i++) {
            compressFactor *= 10;
        }
        return compressFactor;
    }

    public boolean everLoadedTreeFromStream() {
        return everLoaded;
    }

    public boolean isLoadingFromStream() {
        return loadingFromStream;
    }

    public int getProgressRatio() {
        return progressRatio;
    }
}
