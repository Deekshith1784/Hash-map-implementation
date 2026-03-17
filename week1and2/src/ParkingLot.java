class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        licensePlate = null;
        entryTime = 0;
        occupied = false;
    }
}

public class ParkingLot {

    private static final int SIZE = 500;

    ParkingSpot[] table = new ParkingSpot[SIZE];

    int occupiedSpots = 0;
    int totalProbes = 0;
    int operations = 0;

    public ParkingLot() {
        for (int i = 0; i < SIZE; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % SIZE;
    }

    // Park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % SIZE;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        occupiedSpots++;

        totalProbes += probes;
        operations++;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" +
                index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].occupied) {

            if (plate.equals(table[index].licensePlate)) {

                long exitTime = System.currentTimeMillis();

                long durationMs = exitTime - table[index].entryTime;

                double hours = durationMs / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                occupiedSpots--;

                System.out.printf("exitVehicle(\"%s\") → Spot #%d freed, Duration: %.2f h, Fee: $%.2f\n",
                        plate, index, hours, fee);

                return;
            }

            index = (index + 1) % SIZE;
        }

        System.out.println("Vehicle not found.");
    }

    // Find nearest available spot
    public int findNearestAvailable() {

        for (int i = 0; i < SIZE; i++) {
            if (!table[i].occupied)
                return i;
        }

        return -1;
    }

    // Statistics
    public void getStatistics() {

        double occupancy = (occupiedSpots * 100.0) / SIZE;

        double avgProbes = operations == 0 ? 0 :
                (double) totalProbes / operations;

        System.out.println("Occupancy: " + occupancy + "%");
        System.out.println("Avg Probes: " + avgProbes);
    }

    public static void main(String[] args) throws Exception {

        ParkingLot lot = new ParkingLot();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(3000);

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}