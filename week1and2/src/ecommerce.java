import java.util.*;

class Ecommerce {

    // productId -> stock
    private HashMap<String, Integer> stock = new HashMap<>();

    // productId -> waiting users (FIFO)
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingList = new HashMap<>();

    // Add product with initial stock
    public void addProduct(String productId, int quantity) {
        stock.put(productId, quantity);
        waitingList.put(productId, new LinkedHashMap<>());
    }

    // Check stock availability
    public void checkStock(String productId) {

        if (!stock.containsKey(productId)) {
            System.out.println("Product not found");
            return;
        }

        System.out.println(productId + " → " + stock.get(productId) + " units available");
    }

    // Purchase item (thread safe)
    public synchronized void purchaseItem(String productId, int userId) {

        if (!stock.containsKey(productId)) {
            System.out.println("Product not found");
            return;
        }

        int currentStock = stock.get(productId);

        if (currentStock > 0) {

            stock.put(productId, currentStock - 1);

            System.out.println("User " + userId +
                    " → Success, " + (currentStock - 1) + " units remaining");

        } else {

            LinkedHashMap<Integer, Integer> queue = waitingList.get(productId);

            int position = queue.size() + 1;
            queue.put(userId, position);

            System.out.println("User " + userId +
                    " → Added to waiting list, position #" + position);
        }
    }

    // Main method to test system
    public static void main(String[] args) {

        Ecommerce system = new Ecommerce();

        system.addProduct("IPHONE15_256GB", 100);

        system.checkStock("IPHONE15_256GB");

        system.purchaseItem("IPHONE15_256GB", 12345);
        system.purchaseItem("IPHONE15_256GB", 67890);

        // simulate many users
        for (int i = 1; i <= 105; i++) {
            system.purchaseItem("IPHONE15_256GB", 10000 + i);
        }
    }
}