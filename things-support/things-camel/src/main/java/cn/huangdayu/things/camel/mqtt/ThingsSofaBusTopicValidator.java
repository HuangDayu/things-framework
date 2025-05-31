package cn.huangdayu.things.camel.mqtt;

/**
 * @author huangdayu
 */

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThingsSofaBusTopicValidator {


    private static class TrieNode {
        Map<String, TrieNode> children = new HashMap<>();
        boolean isWildcardPlus = false;
        boolean isWildcardSharp = false;
        int subscribeCount = 0;
        boolean isEnd = false;
    }

    private final TrieNode root = new TrieNode();

    private final Map<String, Boolean> cache = new LinkedHashMap<>(1000, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 10_000;
        }
    };

    public synchronized void addTopic(String topic) {
        validateTopic(topic);
        String[] parts = topic.split("/");
        TrieNode currentNode = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            currentNode.subscribeCount++;

            if (part.equals("#")) {
                // 创建#通配符节点
                TrieNode sharpNode = currentNode.children.computeIfAbsent("#", k -> new TrieNode());
                sharpNode.subscribeCount++;
                sharpNode.isEnd = true;
                return;
            }

            if (part.equals("+")) {
                // 创建+通配符节点
                currentNode = currentNode.children.computeIfAbsent("+", k -> new TrieNode());
                continue;
            }

            // 精确匹配节点
            currentNode = currentNode.children.computeIfAbsent(part, k -> new TrieNode());
        }

        currentNode.subscribeCount++;
        currentNode.isEnd = true;
    }

    public synchronized boolean removeTopic(String topic) {
        validateTopic(topic);
        cache.clear();
        String[] parts = topic.split("/");
        return removeNode(root, parts, 0);
    }

    private boolean removeNode(TrieNode node, String[] parts, int index) {
        if (node == null || index > parts.length) return false;

        // 到达路径终点
        if (index == parts.length) {
            if (node.subscribeCount > 0) {
                node.subscribeCount--;
                if (node.subscribeCount == 0) {
                    node.isEnd = false;
                    return true;
                }
            }
            return false;
        }

        String part = parts[index];
        boolean removed = false;

        // 处理所有可能的匹配路径
        if (node.children.containsKey(part)) { // 精确匹配
            removed = removeNode(node.children.get(part), parts, index + 1);
        }
        if (!removed && node.children.containsKey("+")) { // +通配符匹配
            removed = removeNode(node.children.get("+"), parts, index + 1);
        }
        if (!removed && index == parts.length - 1 && node.children.containsKey("#")) { // #通配符匹配
            removed = removeNode(node.children.get("#"), parts, parts.length);
        }

        // 清理空节点
        if (removed) {
            node.subscribeCount--;
            if (node.subscribeCount == 0 && node.children.isEmpty()) {
                node.children.remove(part);
            }
        }
        return removed;
    }

    public boolean isDuplicateSubscription(String topic) {
        return cache.computeIfAbsent(topic, this::checkWithTrie);
    }

    private boolean checkWithTrie(String topic) {
        String[] parts = topic.split("/");
        return checkNode(root, parts, 0);
    }

    private boolean checkNode(TrieNode node, String[] parts, int index) {
        if (node == null) return false;

        // 遇到#通配符且存在有效订阅
        if (node.children.containsKey("#") && node.children.get("#").subscribeCount > 0) {
            return true;
        }

        // 已遍历完所有层级
        if (index >= parts.length) {
            return node.isEnd && node.subscribeCount > 0;
        }

        String part = parts[index];

        // 检查精确匹配路径
        if (node.children.containsKey(part) && checkNode(node.children.get(part), parts, index + 1)) {
            return true;
        }

        // 检查+通配符路径
        if (node.children.containsKey("+") && checkNode(node.children.get("+"), parts, index + 1)) {
            return true;
        }

        return false;
    }

    // 以下为辅助方法
    private void handleSharpWildcard(TrieNode node) {
        node.isWildcardSharp = true;
        node.isEnd = true;
        node.subscribeCount++;
    }

    private TrieNode handlePlusWildcard(TrieNode node) {
        node.isWildcardPlus = true;
        return node.children.computeIfAbsent("+", k -> new TrieNode());
    }

    private TrieNode handleExactMatch(TrieNode node, String part) {
        return node.children.computeIfAbsent(part, k -> new TrieNode());
    }

    private boolean handleLastLevel(TrieNode node) {
        if (node.subscribeCount > 0) {
            node.subscribeCount--;
            if (node.subscribeCount == 0) {
                node.isEnd = false;
                return true;
            }
        }
        return false;
    }

    private void cleanEmptyNode(TrieNode node, String part) {
        if (node.children.isEmpty()) {
            node.children.remove(part);
            if (part.equals("+")) node.isWildcardPlus = false;
            if (part.equals("#")) node.isWildcardSharp = false;
        }
    }

    private boolean checkExactMatch(TrieNode node, String[] parts, int index, String currentPart) {
        return node.children.containsKey(currentPart) &&
                checkNode(node.children.get(currentPart), parts, index + 1);
    }

    private boolean checkPlusWildcard(TrieNode node, String[] parts, int index) {
        return node.isWildcardPlus &&
                checkNode(node.children.get("+"), parts, index + 1);
    }

    private boolean checkSharpWildcard(TrieNode node) {
        return node.children.containsKey("#") &&
                node.children.get("#").subscribeCount > 0;
    }

    // 保留原有校验方法
    private static void validateTopic(String topic) {
        // 保持原有实现不变
    }
}
