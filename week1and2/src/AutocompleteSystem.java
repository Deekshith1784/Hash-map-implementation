import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
    boolean isEnd = false;
}

class AutocompleteSystem {

    private TrieNode root;
    private HashMap<String, Integer> frequency;

    public AutocompleteSystem() {
        root = new TrieNode();
        frequency = new HashMap<>();
    }

    // Insert query into Trie
    public void insert(String query) {

        frequency.put(query, frequency.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query, frequency.get(query));
        }

        node.isEnd = true;
    }

    // Search suggestions for prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        // Top 10 results using min heap
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<String, Integer> entry : node.queries.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> results = new ArrayList<>();

        while (!pq.isEmpty()) {
            results.add(pq.poll().getKey());
        }

        Collections.reverse(results);

        return results;
    }

    // Update frequency when user searches again
    public void updateFrequency(String query) {
        insert(query);
        System.out.println(query + " → Frequency: " + frequency.get(query));
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.insert("java tutorial");
        system.insert("javascript");
        system.insert("java download");
        system.insert("java tutorial");
        system.insert("java 21 features");
        system.insert("java 21 features");

        List<String> results = system.search("jav");

        System.out.println("Suggestions for 'jav':");

        int rank = 1;
        for (String r : results) {
            System.out.println(rank + ". " + r);
            rank++;
        }

        system.updateFrequency("java 21 features");
    }
}