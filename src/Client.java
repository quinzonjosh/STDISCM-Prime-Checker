import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    // CONSTANTS
    private static final String DEFAULT_MASTER_ADDRESS = "localhost";

    public static void main(String[] args) {
        String masterAddress = DEFAULT_MASTER_ADDRESS;

        // Check if a custom address was provided as an argument
        if (args.length > 0) { // Check if there is at least one argument
            masterAddress = args[0];
        }

        try (Socket socket = new Socket(masterAddress, 4999)) {
            System.out.println("Connected to Master Server at " + masterAddress + ":4999");
            try(DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter start point: ");
                int startPoint = scanner.nextInt();
                System.out.print("Enter end point: ");
                int endPoint = scanner.nextInt();
                System.out.print("Enter the number of threads to use: ");
                int nThreads = scanner.nextInt();

                // Start time is when the request is sent
                long startTime = System.currentTimeMillis();
                // Send task to Master Server
                dos.writeInt(startPoint);
                dos.writeInt(endPoint);
                dos.writeInt(nThreads);

                System.out.println("Sent task: Find primes between " + startPoint + " and " + endPoint + " using " + nThreads + " threads.");

                // Waiting for response from Master Server
                int numberOfPrimes = dis.readInt();
                // End time is when the response is received.
                long endTime = System.currentTimeMillis();
                System.out.println("Master Server responded with prime count: " + numberOfPrimes);
                System.out.println("Time taken: " + (endTime - startTime) + " ms");

//                // SANITY CHECK
//                if (isPrimeCountPlausible(startPoint, endPoint, numberOfPrimes)) {
//                    System.out.println("SANITY CHECK: Using Prime Number Theorem, the prime count is plausible.");
//                } else {
//                    System.out.println("SANITY CHECK: Using Prime Number Theorem, the prime count is NOT plausible.");
//                }
            }
        } catch (IOException e) {
            System.err.println("Could not connect to Master Server on " + masterAddress + ":4999");
            e.printStackTrace();
            return; // Exits the client if it fails to connect to the MasterServer
        }
    }

//    // SANITY CHECK METHODS
//    // Check if the reported prime count is plausible
//    public static boolean isPrimeCountPlausible(int start, int end, int reportedPrimeCount) {
//        double estimatedPrimeCount = (estimatePrimesUpTo(end) - estimatePrimesUpTo(start));
//        // Allow some tolerance
//        double tolerance = 0.05 * estimatedPrimeCount;
//        System.out.println("Estimated prime count: " + estimatedPrimeCount);
//        return Math.abs(reportedPrimeCount - estimatedPrimeCount) <= tolerance;
//    }
//    // Estimating the number of primes less than or equal to n using the Prime Number Theorem
//    public static double estimatePrimesUpTo(int n) {
//        return n / Math.log(n);
//    }
}
