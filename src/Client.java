import java.io.*;
import java.net.*;
import java.util.Scanner;

// LAST STOP: WRONG PRIME COUNT. DOUBLE CHECK EVEN SERVER SENDING AND RECEIVING DATA

public class Client {
    // CONSTANTS
    private static final String DEFAULT_MASTER_ADDRESS = "localhost";

    public static void main(String[] args) {
        String masterAddress = DEFAULT_MASTER_ADDRESS;

        // Check if a custom address was provided as an argument
        if (args.length > 0) { // Check if there is at least one argument
            masterAddress = args[0];
        }

        // trials for each thread count
        for (int loop=1; loop<=5; loop++) {
            // Connect to master
            try (Socket socket = new Socket(masterAddress, 4999)) {
                System.out.println("Connected to Master Server at " + masterAddress + ":4999");

                // setup writer & reader
                try(DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                    // Get user input
                    Scanner scanner = new Scanner(System.in);
    //                System.out.print("Enter start point: ");
                    int startPoint = 1;
    //                System.out.print("Enter end point: ");
                    int endPoint = 10_000_000;
    //                System.out.print("Enter the number of threads to use: ");
                    int nThreads = 1024;

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
                    System.out.println();

                }
            } catch (IOException e) {
                System.err.println("Could not connect to Master Server on " + masterAddress + ":4999");
                e.printStackTrace();
            }
        }
    }
}