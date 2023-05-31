package org.example.services.searcher;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class Trie<T> implements PrefixStorage<T>{
    private final Node<T> root;

    public Trie() {
        root = new Node<>(new TreeMap<>(), null);
    }

    @Override
    public void add(String line, T rowId) {
        var cur = root;
        var symbols = line.toCharArray();
        for (var symbol : symbols) {
            if (!cur.to.containsKey(symbol)) {
                cur.to.put(symbol, new Node<T>(new TreeMap<>(), new ArrayList<>()));
            }
            cur = cur.to.get(symbol);
        }
        cur.rowIds.add(rowId);
    }

    @Override
    public List<T> findRowsByPrefix(String prefix) {
        var cur = root;
        List<T> result = new ArrayList<>();
        for (var letter : prefix.toCharArray()) {
            if (!cur.getTo().containsKey(letter)) {
                return result;
            }
            cur = cur.to.get(letter);
        }
        dfs(cur, result);
        return result;
    }

    private void dfs(Node<T> node, List<T> result) {
        if (!node.rowIds.isEmpty()) {
            result.addAll(node.rowIds);
        }
        for (var key : node.to.keySet()) {
            dfs(node.to.get(key), result);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Node<T> {
        Map<Character, Node<T>> to;
        List<T> rowIds;
    }
}
