package org.example;

import org.example.services.names.RowProcessor;

import java.io.RandomAccessFile;

public class Main {

    private static final String DELIMITER = ",";
    private static final String FILE_PATH = "src/main/resources/airports.csv";
    private static final RowProcessor processor = new RowProcessor();

    public static void main(String[] args) {
        try {
            processor.preprocessCsv(FILE_PATH, DELIMITER);
            System.gc();
            var res = processor.getRowsByPrefix("Aba");
            try(RandomAccessFile randomAccessFile = new RandomAccessFile(FILE_PATH, "r")) {
                for (int bytePosition : res) {
                    randomAccessFile.seek(bytePosition);
                    var foundLine = randomAccessFile.readLine();
                    System.out.println(foundLine);
                    System.out.flush();
                }
            }
            long afterUsedMem = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()) / (1024*1024);
            System.out.println(afterUsedMem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}