import java.util.*;

class PlagiarismDetector {

    // n-gram size
    private static final int N = 5;

    // ngram -> set of document IDs
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    // documentId -> list of ngrams
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();


    // Break text into words
    private List<String> tokenize(String text) {
        text = text.toLowerCase().replaceAll("[^a-zA-Z ]", "");
        return Arrays.asList(text.split("\\s+"));
    }

    // Generate n-grams
    private List<String> generateNgrams(String text) {

        List<String> words = tokenize(text);
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.size() - N; i++) {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < N; j++) {
                sb.append(words.get(i + j)).append(" ");
            }

            ngrams.add(sb.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());

            ngramIndex.get(gram).add(docId);
        }
    }

    // Analyze a document
    public void analyzeDocument(String docId) {

        List<String> ngrams = documentNgrams.get(docId);

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String otherDoc : ngramIndex.get(gram)) {

                    if (!otherDoc.equals(docId)) {

                        matchCount.put(otherDoc,
                                matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        for (String otherDoc : matchCount.keySet()) {

            int matches = matchCount.get(otherDoc);

            double similarity = (matches * 100.0) / ngrams.size();

            System.out.println("Found " + matches +
                    " matching n-grams with \"" + otherDoc + "\"");

            System.out.println("Similarity: " +
                    String.format("%.2f", similarity) + "%");

            if (similarity > 60) {
                System.out.println("PLAGIARISM DETECTED");
            } else if (similarity > 10) {
                System.out.println("Suspicious similarity");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "Artificial intelligence is transforming the world "
                + "by improving healthcare education and automation.";

        String essay2 = "Artificial intelligence is transforming the world "
                + "by improving healthcare and education systems.";

        String essay3 = "Sports and fitness are important for maintaining "
                + "a healthy lifestyle and mental well being.";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay3);

        detector.analyzeDocument("essay_092.txt");
    }
}
