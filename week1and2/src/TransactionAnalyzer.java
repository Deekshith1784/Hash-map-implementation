import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    long time; // minutes from start of day

    Transaction(int id, int amount, String merchant, String account, long time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // 1️⃣ Classic Two-Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        System.out.println("Two-Sum pairs:");

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction other = map.get(complement);
                System.out.println("(id:" + other.id + ", id:" + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }

    // 2️⃣ Two-Sum within 1 hour window
    public void findTwoSumWithWindow(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        System.out.println("Two-Sum within 1 hour:");

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                if (Math.abs(t.time - other.time) <= 60) {
                    System.out.println("(id:" + other.id + ", id:" + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }
    }

    // 3️⃣ K-Sum (recursive)
    public void findKSum(int k, int target) {

        System.out.println("K-Sum results:");

        List<Transaction> result = new ArrayList<>();

        kSumHelper(0, k, target, result);
    }

    private void kSumHelper(int index, int k, int target, List<Transaction> current) {

        if (k == 0 && target == 0) {

            System.out.print("(");

            for (Transaction t : current)
                System.out.print("id:" + t.id + " ");

            System.out.println(")");

            return;
        }

        if (index >= transactions.size() || k < 0 || target < 0)
            return;

        Transaction t = transactions.get(index);

        current.add(t);

        kSumHelper(index + 1, k - 1, target - t.amount, current);

        current.remove(current.size() - 1);

        kSumHelper(index + 1, k, target, current);
    }

    // 4️⃣ Duplicate Detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        System.out.println("Duplicate transactions:");

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                System.out.print("{amount:" + list.get(0).amount +
                        ", merchant:" + list.get(0).merchant +
                        ", accounts:[");

                for (Transaction t : list)
                    System.out.print(t.account + " ");

                System.out.println("]}");
            }
        }
    }

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "StoreA", "acc1", 600));
        analyzer.addTransaction(new Transaction(2, 300, "StoreB", "acc2", 615));
        analyzer.addTransaction(new Transaction(3, 200, "StoreC", "acc3", 630));
        analyzer.addTransaction(new Transaction(4, 500, "StoreA", "acc4", 640));

        analyzer.findTwoSum(500);

        analyzer.findTwoSumWithWindow(500);

        analyzer.findKSum(3, 1000);

        analyzer.detectDuplicates();
    }
}