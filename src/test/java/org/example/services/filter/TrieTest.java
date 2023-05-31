package org.example.services.filter;

import org.example.services.searcher.PrefixStorage;
import org.example.services.searcher.Trie;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrieTest {

    @Test
    public void findAnyByPrefix() {
        var names = new ArrayList<>(List.of("cat", "cute", "cut", "cbb", "cbbd", "to", "bee", "be"));
        PrefixStorage<Integer> prefixStorage = new Trie<>();
        for (int i = 0; i < names.size(); ++i) {
            prefixStorage.add(names.get(i), i);
        }
        assertEquals(5, prefixStorage.findRowsByPrefix("c").size());
        assertEquals(2, prefixStorage.findRowsByPrefix("be").size());
        assertEquals(1, prefixStorage.findRowsByPrefix("t").size());
    }
}
