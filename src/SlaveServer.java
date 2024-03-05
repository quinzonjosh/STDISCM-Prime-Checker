import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SlaveServer {
    // CONSTANTS
    private static final String DEFAULT_MASTER_ADDRESS = "localhost";
    private static final int MASTER_REGISTRATION_PORT = 5001; // Port for registering with the master
    private static final int DEFAULT_SLAVE_SERVICE_PORT = 5002; // Change for each slave if running on the same machine


    public static void main(String[] args) {
        String masterAddress = DEFAULT_MASTER_ADDRESS;
        int slaveServicePort = DEFAULT_SLAVE_SERVICE_PORT;

        // Process command-line arguments for custom parameters
        // Determine the number of arguments
        switch (args.length) {
            case 1:
                // One argument - could be either masterAddress or slaveServicePort
                // Example:
                // - java SlaveServer 192.168.1.5
                // - java SlaveServer 5003
                if (args[0].matches("\\d+")) { // Simple check to see if arg is numeric
                    slaveServicePort = Integer.parseInt(args[0]);
                } else {
                    masterAddress = args[0];
                }
                break;
            case 2:
                // Two arguments - first is masterAddress, second is slaveServicePort
                // Example:
                // - java SlaveServer 192.168.1.5 5003
                masterAddress = args[0];
                try {
                    slaveServicePort = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number provided. Using default port: " + DEFAULT_SLAVE_SERVICE_PORT);
                }
                break;
        }

        System.out.println("Using masterAddress: " + masterAddress);
        System.out.println("Using slaveServicePort: " + slaveServicePort);

        // First, register with the MasterServer
        if (!registerWithMaster(masterAddress, slaveServicePort)) {
            System.exit(1); // Exit if registration with MasterServer fails
        }

        // Now, listen for tasks from the MasterServer
        try (ServerSocket serverSocket = new ServerSocket(slaveServicePort)) {
            System.out.println("Listening for tasks from Master Server on port " + slaveServicePort);

            while (true) {
                Socket masterSocket = serverSocket.accept();
                System.out.println("Connected to Master Server for a task.");
                handleTask(masterSocket);
            }
        } catch (IOException ex) {
            System.err.println("Could not listen on port " + slaveServicePort);
            ex.printStackTrace();
        }
    }

    // Slave Server Registration with Master
    private static boolean registerWithMaster(String masterAddress, int listeningPort) {
        // Connect to the MasterServer's registration port and send this server's details
        System.out.println("Attempting to register with MasterServer...");
        try (Socket socket = new Socket(masterAddress, MASTER_REGISTRATION_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
//            System.out.println("My IP Address: " + InetAddress.getLocalHost().getHostAddress());
//            out.println(InetAddress.getLocalHost().getHostAddress()); // Send slave server's IP address
            out.println(listeningPort); // Send the port on which this slave server is listening
            System.out.println("Successfully registered with MasterServer.");
            return true;
        } catch (IOException e) {
            System.err.println("Registration with Master Server (" + masterAddress + ":" + MASTER_REGISTRATION_PORT + ") failed. Exiting...");
            e.printStackTrace();
            return false;
        }
    }

    // Handle Task: Compute Prime Numbers in Received Range
    private static void handleTask(Socket masterSocket) {
        try (DataInputStream dis = new DataInputStream(masterSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(masterSocket.getOutputStream())) {

            // read number of threads and size of numbers sent to slave from master
            int nThreads = dis.readInt();
            int size = dis.readInt();

            // read all numbers sent from master to slave
            ArrayList<Integer> numbersList = new ArrayList<>();
            for(int num = 0; num < size; num++){
                numbersList.add(dis.readInt());
            }

//            System.out.println("received: " + numbersList);


            // This method should return the number of prime numbers found between startPoint and endPoint
            int primeCount = calculatePrimesWithThreads(numbersList, nThreads);
            System.out.println("Calculated " + primeCount + " primes. Sending result to Master Server.");
            dos.writeInt(primeCount); // Send the calculated prime count back to MasterServer
        } catch (IOException ex) {
            System.err.println("Failed to handle task from master: An error occurred with the MasterServer connection.");
            ex.printStackTrace();
        }
    }

    // Multithreaded Prime Calculation
    private static int calculatePrimesWithThreads(ArrayList<Integer> numbersList, int nThreads) {

        List<Integer> primes = new ArrayList<>();
        Lock primesLock = new ReentrantLock();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        List<List<Integer>> distributedNumbers = new ArrayList<>(nThreads);

        for(int i=0; i<nThreads; i++){
            distributedNumbers.add(new ArrayList<>());
        }

        // distribute the list of numbers evenly
        for (int i=0; i<numbersList.size(); i++){
            int threadIndex = i % nThreads;
            distributedNumbers.get(threadIndex).add(numbersList.get(i));
        }

        for (int i = 0; i < nThreads; i++) {
            executor.submit(new PrimeTask(distributedNumbers.get(i), primes, primesLock));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all tasks to finish
        }

        return primes.size();
    }

    // PrimeTask as a Runnable for Thread Execution
    static class PrimeTask implements Runnable {
        private final List<Integer> numbers;
        private final List<Integer> primes;
        private final Lock lock;

        PrimeTask(List<Integer> numbers, List<Integer> primes, Lock lock) {
            this.numbers = numbers;
            this.primes = primes;
            this.lock = lock;
        }

        @Override
        public void run() {
            for (Integer num: numbers) {
                if (isPrime(num)) {
                    lock.lock();
                    try {
                        primes.add(num);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }

        /*
        This function checks if an integer n is prime.

        Parameters:
        n : int - integer to check

        Returns true if n is prime, and false otherwise.
        */
        private boolean isPrime(int n) {
            if (n <= 1) return false;
            for (int i = 2; i * i <= n; i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }
}