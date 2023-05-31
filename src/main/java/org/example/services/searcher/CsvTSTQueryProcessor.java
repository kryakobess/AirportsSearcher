package org.example.services.searcher;

import org.example.services.filter.Filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CsvTSTQueryProcessor implements FileQueryProcessor{

    private final PrefixStorage<Integer> prefixStorage = new TernarySearchTree<>();

    @Override
    public void preprocessFile(String filePath, String delimiter) throws Exception {
        var rowNames = saveAllRowNames(filePath, delimiter);
        for (var rowName : rowNames) {
            prefixStorage.add(rowName.name, rowName.bytePosition);
        }
    }

    @Override
    public List<String> answerQuery(String query, String filePath, Filter filter) {
        var unfilteredResult = prefixStorage.findRowsByPrefix(query);
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

    private List<RowName> saveAllRowNames(String filePath, String delimiter) throws Exception{
        List<RowName> rowNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int byteCount = 0;
            while ((line = reader.readLine()) != null) {
                var row = line.split(delimiter);
                rowNames.add(new RowName(row[1].replaceAll("\"", "").toLowerCase(), byteCount));
                byteCount += line.getBytes().length + 1;
            }
        }
        rowNames.sort(RowName::compareTo);
        return rowNames;
    }

    private static class RowName implements Comparable<RowName>{
        String name;
        int bytePosition;

        public RowName(String name, int bytePosition) {
            this.name = name;
            this.bytePosition = bytePosition;
        }

        @Override
        public int compareTo(RowName o) {
            return this.name.compareTo(o.name);
        }
    }
}
