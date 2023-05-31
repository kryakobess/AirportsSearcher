package org.example.services.searcher;

import org.example.services.filter.Filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CsvTrieQueryProcessor implements FileQueryProcessor {

    private final PrefixStorage<Integer> trie = new Trie<>();

    @Override
    public void preprocessFile(String filePath, String delimiter) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int byteCount = 0;
            while ((line = reader.readLine()) != null) {
                var row = line.split(delimiter);
                trie.add(row[1].replaceAll("\"", "").toLowerCase(), byteCount);
                byteCount += line.getBytes().length + 1;
            }
        }
    }

    @Override
    public List<String> answerQuery(String query, String filePath, Filter filter) {
        var unfilteredResult = trie.findRowsByPrefix(query);
        List<String> result = new ArrayList<>();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
            for (int bytePosition : unfilteredResult) {
                randomAccessFile.seek(bytePosition);
                var foundLine = randomAccessFile.readLine();
                try {
                    if (filter.filter(foundLine)) {
                        result.add(foundLine);
                    }
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            System.out.println("Cannot open file!");
        }
        return result;
    }
}
