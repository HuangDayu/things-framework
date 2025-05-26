package cn.huangdayu.things.camel.test;

/**
 * @author huangdayu
 */
import cn.huangdayu.things.common.annotation.ThingsBean;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ThingsBean
public class ThingsMqttTopicValidator {

    private static class TrieNode {
        Map<String, TrieNode> children = new HashMap<>();
        boolean isWildcardPlus = false;
        boolean isWildcardSharp = false;
        boolean isEnd = false;
    }

    private final TrieNode root = new TrieNode();

    private final Map<String, Boolean> cache = new LinkedHashMap<>(1000, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 10_000;
        }
    };

    // ------------------- 核心方法 -------------------

    public synchronized void addTopic(String topic) {
        validateTopic(topic);
        cache.clear(); // 清空缓存保证数据一致性
        String[] parts = topic.split("/");
        TrieNode currentNode = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if ("#".equals(part)) {
                currentNode.isWildcardSharp = true;
                currentNode.isEnd = true;
                return; // # 必须是最后一级
            }

            if ("+".equals(part)) {
                currentNode.isWildcardPlus = true;
                currentNode = currentNode.children.computeIfAbsent("+", k -> new TrieNode());
                continue;
            }

            if (!currentNode.children.containsKey(part)) {
                currentNode.children.put(part, new TrieNode());
            }
            currentNode = currentNode.children.get(part);
        }
        currentNode.isEnd = true;
    }

    public boolean isCanSubscribe(String topic) {
        if (!isDuplicateSubscription(topic)) {
            addTopic(topic);
            return true;
        }
        return false;
    }

    public boolean isDuplicateSubscription(String topic) {
        return cache.computeIfAbsent(topic, this::checkWithTrie);
    }

    // ------------------- 私有方法 -------------------

    private boolean checkWithTrie(String topic) {
        String[] parts = topic.split("/");
        return checkNode(root, parts, 0);
    }

    private boolean checkNode(TrieNode node, String[] parts, int index) {
        if (node == null) return false;

        // 当前层级有 # 通配符
        if (node.isWildcardSharp) return true;

        // 已遍历完所有层级
        if (index == parts.length) {
            return node.isEnd;
        }

        String currentPart = parts[index];
        boolean found = false;

        // 1. 精确匹配检查
        if (node.children.containsKey(currentPart)) {
            found = checkNode(node.children.get(currentPart), parts, index + 1);
        }

        // 2. + 通配符检查
        if (!found && node.isWildcardPlus) {
            found = checkNode(node.children.get("+"), parts, index + 1);
        }

        // 3. 递归检查父级 # 通配符
        if (!found) {
            TrieNode parent = findParentWithSharp(node);
            found = (parent != null);
        }

        return found;
    }

    private TrieNode findParentWithSharp(TrieNode node) {
        while (node != null) {
            if (node.isWildcardSharp) {
                return node;
            }
            // 通过 children 反向查找父节点（实际需要维护父指针）
            // 此处简化实现，仅做逻辑演示
            node = getParent(root, node);
        }
        return null;
    }

    // 简化版父节点查找（实际需要维护父指针）
    private TrieNode getParent(TrieNode root, TrieNode target) {
        // 需要实现真正的父节点查找逻辑
        return null;
    }

    private static void validateTopic(String topic) {
        if (topic == null || topic.isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        String[] parts = topic.split("/");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (part.contains("#")) {
                if (part.length() > 1 || i != parts.length - 1) {
                    throw new IllegalArgumentException("# must be last level");
                }
            }

            if (part.contains("+") && part.length() > 1) {
                throw new IllegalArgumentException("Invalid + wildcard");
            }
        }
    }
}
