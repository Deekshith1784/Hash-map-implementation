import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String id, String content) {
        this.videoId = id;
        this.content = content;
    }
}

public class MultiLevelCache {

    // L1 Cache (memory) - 10,000 videos
    private LinkedHashMap<String, VideoData> L1 =
            new LinkedHashMap<String, VideoData>(10000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 10000;
                }
            };

    // L2 Cache (SSD simulation)
    private LinkedHashMap<String, VideoData> L2 =
            new LinkedHashMap<String, VideoData>(100000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 100000;
                }
            };

    // L3 Database simulation
    private HashMap<String, VideoData> database = new HashMap<>();

    // Access counter
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Statistics
    int L1Hits = 0;
    int L2Hits = 0;
    int L3Hits = 0;

    public MultiLevelCache() {

        // preload database with sample videos
        for (int i = 1; i <= 1000; i++) {
            database.put("video_" + i,
                    new VideoData("video_" + i, "VideoContent_" + i));
        }
    }

    public VideoData getVideo(String videoId) {

        long start = System.currentTimeMillis();

        // L1 Cache
        if (L1.containsKey(videoId)) {

            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");

            updateAccess(videoId);

            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 Cache
        if (L2.containsKey(videoId)) {

            L2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData data = L2.get(videoId);

            promoteToL1(videoId, data);

            updateAccess(videoId);

            return data;
        }

        System.out.println("L2 Cache MISS");

        // L3 Database
        if (database.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData data = database.get(videoId);

            L2.put(videoId, data);

            updateAccess(videoId);

            return data;
        }

        System.out.println("Video not found");
        return null;
    }

    // promote video to L1
    private void promoteToL1(String videoId, VideoData data) {

        int count = accessCount.getOrDefault(videoId, 0);

        if (count > 2) {
            L1.put(videoId, data);
            System.out.println("Promoted " + videoId + " to L1");
        }
    }

    private void updateAccess(String videoId) {
        accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
    }

    public void invalidate(String videoId) {

        L1.remove(videoId);
        L2.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        System.out.println("\nCache Statistics:");

        System.out.println("L1 Hit Rate: " +
                (total == 0 ? 0 : (L1Hits * 100.0 / total)) + "%");

        System.out.println("L2 Hit Rate: " +
                (total == 0 ? 0 : (L2Hits * 100.0 / total)) + "%");

        System.out.println("L3 Hit Rate: " +
                (total == 0 ? 0 : (L3Hits * 100.0 / total)) + "%");

        System.out.println("Overall Requests: " + total);
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video_123");
        System.out.println();

        cache.getVideo("video_123");
        System.out.println();

        cache.getVideo("video_999");
        System.out.println();

        cache.getVideo("video_123");
        System.out.println();

        cache.getStatistics();
    }
}