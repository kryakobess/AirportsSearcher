package org.example;

import org.example.services.filter.Filter;
import org.example.services.filter.FilterImpl;
import org.example.services.searcher.CsvTSTQueryProcessor;
import org.example.services.searcher.FileQueryProcessor;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String DELIMITER = ",";
    private static final String FILE_PATH = "src/main/resources/airports.csv";
    private static final String ASK_FOR_NEXT_QUERY =
            "Enter filtering expression (press Enter for no filtering) or '!quit' to finish querying :";
    private static final FileQueryProcessor processor = new CsvTSTQueryProcessor();


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        try {
            processor.preprocessFile(FILE_PATH, DELIMITER);
            System.gc();
        } catch (Exception e) {
            System.out.println("Cannot open file");
            return;
        }

        String expression = askForQuery(in, ASK_FOR_NEXT_QUERY);
        while (!expression.equals("!quit")) {
            Filter filter = createFilter(expression);
            if (filter == null) {
                expression = askForQuery(in, ASK_FOR_NEXT_QUERY);
                System.out.flush();
                continue;
            }

            String prefix = askForQuery(in, "Enter search prefix down here:").toLowerCase();

            var begin = System.currentTimeMillis();
            var res = processor.answerQuery(prefix, FILE_PATH, filter);
            var duration = System.currentTimeMillis() - begin;

            printSearchResult(res, duration);
            System.gc();
            expression = askForQuery(in, ASK_FOR_NEXT_QUERY);
        }

    }

    private static String askForQuery(Scanner in, String message) {
        System.out.println(message);
        return in.nextLine();
    }

    private static Filter createFilter(String expression) {
        try {
            return new FilterImpl(expression);
        } catch (Exception e) {
            System.out.println("Invalid filter format");
            return null;
        }
    }

    private static void printSearchResult(List<String> rows, long duration) {
        for (var row : rows) {
            System.out.printf("%s [%s]\n", row.split(DELIMITER)[1], row);
        }
        System.out.printf("Количество найденных строк: %d\nВремя, затраченное на поиск: %dмс\n", rows.size(), duration);
    }
}