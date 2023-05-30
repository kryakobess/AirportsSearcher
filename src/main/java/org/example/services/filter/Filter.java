package org.example.services.filter;

public interface Filter {
    boolean filterByExpression(String row, String expression);
}
