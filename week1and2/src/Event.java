import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class AnalyticsDashboard {

    // pageUrl -> visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique visitors
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();


    // Process incoming event
    public void processEvent(Event e) {

        // Count page views
        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(e.url, new HashSet<>());
        uniqueVisitors.get(e.url).add(e.userId);

        // Track traffic sources
        trafficSources.put(e.source, trafficSources.getOrDefault(e.source, 0) + 1);
    }

    // Get top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<Map.Entry<String, Integer>> top = new ArrayList<>();

        int count = 0;

        while (!pq.isEmpty() && count < 10) {
            top.add(pq.poll());
            count++;
        }

        return top;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL TIME DASHBOARD =====");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        System.out.println("Top Pages:");

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" +
                    unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (String source : trafficSources.keySet()) {

            System.out.println(source + " : " + trafficSources.get(source));
        }
    }

    public static void main(String[] args) throws Exception {

        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        // Simulated incoming events
        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_456", "facebook"));
        dashboard.processEvent(new Event("/sports/championship", "user_111", "google"));
        dashboard.processEvent(new Event("/sports/championship", "user_222", "direct"));
        dashboard.processEvent(new Event("/sports/championship", "user_333", "google"));
        dashboard.processEvent(new Event("/tech/ai", "user_999", "twitter"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));

        // Update dashboard every 5 seconds
        while (true) {
            dashboard.getDashboard();
            Thread.sleep(5000);
        }
    }
}