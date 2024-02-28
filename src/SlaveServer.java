import java.io.*;
import java.net.*;

public class SlaveServer {
    private static final String DEFAULT_MASTER_ADDRESS = "localhost";
    private static final int MASTER_REGISTRATION_PORT = 5001; // Port for registering with the master
    private static final int DEFAULT_SLAVE_SERVICE_PORT = 5002; // Change for each slave if running on the same machine

    public static void main(String[] args) {
        String masterAddress = DEFAULT_MASTER_ADDRESS;
        int slaveServicePort = DEFAULT_SLAVE_SERVICE_PORT;

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
            return; // Terminate if registration with MasterServer fails
        }

        // Now, listen for tasks from the MasterServer
        System.out.println("Listening for tasks from Master Server on port " + slaveServicePort);
        try (ServerSocket serverSocket = new ServerSocket(slaveServicePort)) {
            while (true) {
                try (Socket masterSocket = serverSocket.accept();
                     DataInputStream dis = new DataInputStream(masterSocket.getInputStream());
                     DataOutputStream dos = new DataOutputStream(masterSocket.getOutputStream())) {

                    System.out.println("Connected to Master Server for task.");
                    int startPoint = dis.readInt();
                    int endPoint = dis.readInt();
                    System.out.println("Received task: Calculate primes between " + startPoint + " and " + endPoint);

                    // This method should return the number of prime numbers found between startPoint and endPoint
                    int numberOfPrimes = calculatePrimes(startPoint, endPoint);
                    System.out.println("Calculated " + numberOfPrimes + " primes. Sending result to Master Server.");
                    dos.writeInt(numberOfPrimes);
                } catch (IOException ex) {
                    System.err.println("An error occurred with the MasterServer connection.");
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            System.err.println("Could not listen on port " + slaveServicePort);
            ex.printStackTrace();
        }
    }

    private static boolean registerWithMaster(String masterAddress, int listeningPort) {
        // Connect to the MasterServer's registration port and send this server's details
        System.out.println("Attempting to register with MasterServer...");
        try (Socket socket = new Socket(masterAddress, MASTER_REGISTRATION_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(InetAddress.getLocalHost().getHostAddress()); // Send slave server's IP address
            out.println(listeningPort); // Send the port on which this slave server is listening
            System.out.println("Successfully registered with MasterServer.");
            return true;
        } catch (IOException e) {
            System.err.println("Registration with Master Server (" + masterAddress + ":" + MASTER_REGISTRATION_PORT + ") failed. Exiting...");
            e.printStackTrace();
            return false;
        }
    }

    private static int calculatePrimes(int start, int end) {
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
        return count;
    }

    /*
    This function checks if an integer n is prime.

    Parameters:
    n : int - integer to check

    Returns true if n is prime, and false otherwise.
    */
    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
