package org.example.services.filter;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import java.util.Arrays;
import java.util.List;

public class FilterImpl implements Filter {
    private static final String COLUMN_QUERY_VALIDATION_FORMAT =
            "\\s*(\\()*column\\[(\\d+)\\]\\s*(=|<>|>|<)\\s*('[^']*'|\"[^\"]*\"|-?\\d*\\.{0,1}\\d+)\\s*(\\))*\\s*";

    private final String expression;

    public FilterImpl(String expression) throws Exception {
        if (!expression.isBlank() && !isValidExpression(expression)) throw new Exception("Invalid expression format");
        this.expression = rebuildExpression(expression);
    }

    @Override
    public boolean filter(String row) {
        if (expression.isBlank()) return true;

        String reformattedRow = row.replaceAll("\"", "");
        List<String> data = List.of(reformattedRow.split(","));

        JexlEngine jexl = new JexlBuilder().create();
        JexlContext context = mapData(data);
        JexlExpression filter = jexl.createExpression(expression);

        return (boolean) filter.evaluate(context);
    }

    boolean isValidExpression(String expression) {
        var linkers = expression.split(COLUMN_QUERY_VALIDATION_FORMAT);
        for (var linker : linkers) {
            if (!linker.isBlank() && Operation.getOperationFromEquivalent(linker.strip()) == null) {
                return false;
            }
        }
        return true;
    }

    private String rebuildExpression(String expression) {
        return expression
                .replaceAll("\\[", "")
                .replaceAll("\\]","")
                .replaceAll("=", "==")
                .replaceAll("&", "&&")
                .replaceAll("<>", "!=");
    }

    private JexlContext mapData(List<String> data) {
        JexlContext context = new MapContext();
        for (int i = 0; i < data.size(); ++i) {
            try {
                var val = Double.parseDouble(data.get(i));
                context.set(String.format("column%d", i + 1), val);
            } catch (Exception e) {
                context.set(String.format("column%d", i + 1), data.get(i));
            }
        }
        return context;
    }

    public enum Operation {
        EQUAL("="),
        GREATER(">"),
        LESS("<"),
        NOT_EQUAL("<>"),
        AND("&"),
        OR("||");

        private final String equivalent;
        Operation(String equivalent){
            this.equivalent = equivalent;
        }
        public String getEquivalent() {
            return this.equivalent;
        }

        public static Operation getOperationFromEquivalent(String equivalent) {
            return Arrays.stream(Operation.values())
                    .filter(op -> op.getEquivalent().equals(equivalent))
                    .findAny()
                    .orElse(null);
        }
    }


}
