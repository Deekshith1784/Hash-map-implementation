import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long timestamp;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, int ttl) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.timestamp = System.currentTimeMillis();
        this.expiryTime = timestamp + (ttl * 1000);
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private final int MAX_SIZE = 5; // cache capacity

    private LinkedHashMap<String, DNSEntry> cache =
            new LinkedHashMap<String, DNSEntry>(16, 0.75f, true) {

                protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                    return size() > MAX_SIZE; // LRU eviction
                }
            };

    private int hits = 0;
    private int misses = 0;

    public DNSCache() {

        // Background thread to clean expired entries
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    removeExpiredEntries();
                } catch (Exception e) {
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Simulated upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        try { Thread.sleep(100); } catch (Exception e) {}

        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long time = (System.nanoTime() - start) / 1000000;

                System.out.println("Cache HIT → " + entry.ipAddress +
                        " (retrieved in " + time + " ms)");

                return entry.ipAddress;
            } else {
                System.out.println("Cache EXPIRED → querying upstream");
                cache.remove(domain);
            }
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry entry = new DNSEntry(domain, ip, 300);
        cache.put(domain, entry);

        System.out.println("Cache MISS → Query upstream → " + ip + " (TTL:300s)");

        return ip;
    }

    private synchronized void removeExpiredEntries() {

        Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, DNSEntry> e = it.next();

            if (e.getValue().isExpired()) {
                it.remove();
                System.out.println("Removed expired entry: " + e.getKey());
            }
        }
    }

    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws Exception {

        DNSCache dns = new DNSCache();

        dns.resolve("google.com");
        dns.resolve("google.com");

        Thread.sleep(2000);

        dns.resolve("amazon.com");
        dns.resolve("google.com");

        dns.getCacheStats();
    }
}