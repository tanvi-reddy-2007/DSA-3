package algorithms;
import java.util.*;

public class AhoCorasick {
    static class Node {
        Map<Character, Integer> next = new HashMap<>();
        int fail = 0;
        List<String> output = new ArrayList<>();
    }

    private final List<Node> trie = new ArrayList<>();

    public AhoCorasick() {
        trie.add(new Node());
    }

    public void addPattern(String pattern) {
        int cur = 0;
        for (char ch : pattern.toLowerCase().toCharArray()) {
            if (!trie.get(cur).next.containsKey(ch)) {
                trie.get(cur).next.put(ch, trie.size());
                trie.add(new Node());
            }
            cur = trie.get(cur).next.get(ch);
        }
        trie.get(cur).output.add(pattern.toLowerCase());
    }

    public void buildFailureLinks() {
        Queue<Integer> q = new ArrayDeque<>();

        for (int child : trie.get(0).next.values()) {
            trie.get(child).fail = 0;
            q.add(child);
        }

        while (!q.isEmpty()) {
            int u = q.remove();

            for (Map.Entry<Character, Integer> e : trie.get(u).next.entrySet()) {
                char ch = e.getKey();
                int v = e.getValue();

                int f = trie.get(u).fail;
                while (f != 0 && !trie.get(f).next.containsKey(ch)) {
                    f = trie.get(f).fail;
                }

                if (trie.get(f).next.containsKey(ch) && trie.get(f).next.get(ch) != v) {
                    trie.get(v).fail = trie.get(f).next.get(ch);
                } else {
                    trie.get(v).fail = 0;
                }

                trie.get(v).output.addAll(trie.get(trie.get(v).fail).output);
                q.add(v);
            }
        }
    }

    public List<String> search(String text) {
        List<String> result = new ArrayList<>();
        int state = 0;

        for (char raw : text.toLowerCase().toCharArray()) {
            char ch = raw;
            while (state != 0 && !trie.get(state).next.containsKey(ch)) {
                state = trie.get(state).fail;
            }
            if (trie.get(state).next.containsKey(ch)) {
                state = trie.get(state).next.get(ch);
            } else {
                state = 0;
            }
            result.addAll(trie.get(state).output);
        }
        return result;
    }
}
