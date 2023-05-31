package org.example.services.searcher;

import org.example.services.filter.Filter;

import java.util.List;

public interface FileQueryProcessor {
    void preprocessFile(String filePath, String delimiter) throws Exception;
    List<String> answerQuery(String query, String filePath, Filter filter);
}
