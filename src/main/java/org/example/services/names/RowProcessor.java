package org.example.services.names;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RowProcessor {
    private final Trie<Integer> trie = new Trie<>();
    private final List<RowName> rowNames = new ArrayList<>();

    public void preprocessCsv(String filePath, String delimiter) throws Exception {
        saveAllRowNames(filePath, delimiter);
        for (var rowName : rowNames) {
            trie.add(rowName.getName(), rowName.getRowByte());
        }
    }

    public List<Integer> getRowsByPrefix(String prefix) {
        return trie.findAllRowsByPrefix(prefix);
    }

    private void saveAllRowNames(String filePath, String delimiter) throws Exception{
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int byteCount = 0;
            while ((line = reader.readLine()) != null) {
                var row = line.split(delimiter);
                rowNames.add(new RowName(row[1].replaceAll("\"", ""), byteCount));
                byteCount += line.getBytes().length + 1;
            }
        }
        rowNames.sort(RowName::compareTo);
    }
}
