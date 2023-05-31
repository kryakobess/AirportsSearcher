package org.example.services.searcher;

import java.util.List;

public interface PrefixStorage<V> {
    void add(String line, V rowId);

    List<V> findRowsByPrefix(String prefix);
}
