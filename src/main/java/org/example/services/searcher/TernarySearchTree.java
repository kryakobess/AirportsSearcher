package org.example.services.searcher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class TernarySearchTree<T> implements PrefixStorage<T>{

    Node<T> root;

    @Override
    public void add(String line, T rowId) {
        root = this.insert(root, line, rowId, 0);
    }

    private Node<T> insert(Node<T> node, String line, T rowId, int index) {
        if (node == null) {
            node = new Node<>(line.charAt(index));
        }
        if (index < line.length() && line.charAt(index) < node.key) {
            node.left = insert(node.left, line, rowId, index);
        }
        else if (index < line.length() && line.charAt(index) > node.key) {
            node.right = insert(node.right, line, rowId, index);
        } else {
            if (index + 1 < line.length()) {
                node.eq = insert(node.eq, line, rowId, index + 1);
            }
            else {
                node.rowIds.add(rowId);
            }
        }
        return node;
    }

    @Override
    public List<T> findRowsByPrefix(String prefix) {
        var lastSymbolNode = getLastSymbolNode(root, prefix, 0);
        if (lastSymbolNode == null) return new ArrayList<>();
        List<T> result = new ArrayList<>(lastSymbolNode.rowIds);
        dfs(lastSymbolNode.eq, result);
        return result;
    }

    private Node<T> getLastSymbolNode(Node<T> node, String prefix, int index) {
        if (index < prefix.length()) {
            if (node.key > prefix.charAt(index)) {
                return getLastSymbolNode(node.left, prefix, index);
            } else if (node.key < prefix.charAt(index)) {
                return getLastSymbolNode(node.right, prefix, index);
            } else {
                if (index == prefix.length()-1) {
                    if (node.key == prefix.charAt(index)) return node;
                    else return null;
                }
                else return getLastSymbolNode(node.eq, prefix, index + 1);
            }
        }
        return null;
    }

    private void dfs(Node<T> node, List<T> result) {
        if (node == null) return;
        if (!node.rowIds.isEmpty()) {
            result.addAll(node.rowIds);
        }
        dfs(node.left, result);
        dfs(node.eq, result);
        dfs(node.right, result);
    }


    private static class Node<T> {
        Node<T> left;
        Node<T> eq;
        Node<T> right;
        char key;
        List<T> rowIds = new ArrayList<>();

        public Node(char key) {
            this.key = key;
        }
    }
}
