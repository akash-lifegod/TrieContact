import java.util.*;

public class Trie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        Set<Contact> contacts = new HashSet<>();
    }

    private final TrieNode root = new TrieNode();

    public void insert(String key, Contact contact) {
        if (key == null) return;
        key = key.toLowerCase();
        TrieNode node = root;
        node.contacts.add(contact);
        for (char ch : key.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieNode());
            node = node.children.get(ch);
            node.contacts.add(contact);
        }
        node.isEnd = true;
    }

    public List<Contact> searchPrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) return Collections.emptyList();
        prefix = prefix.toLowerCase();
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) return Collections.emptyList();
        }
        List<Contact> results = new ArrayList<>(node.contacts);
        results.sort(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));
        return results;
    }
}
