import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4999)) {
            System.out.println("Connected to Master Server at localhost:4999");
            try(DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream())) {

                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter start point: ");
                int startPoint = scanner.nextInt();
                System.out.print("Enter end point: ");
                int endPoint = scanner.nextInt();
                System.out.print("Enter the number of threads to use: ");
                int nThreads = scanner.nextInt();

                dos.writeInt(startPoint);
                dos.writeInt(endPoint);
                dos.writeInt(nThreads);

                System.out.println("Sent task: Find primes between " + startPoint + " and " + endPoint + " using " + nThreads + " threads.");

                // Waiting for response from Master Server
                int numberOfPrimes = dis.readInt();
                System.out.println("Master Server responded with prime count: " + numberOfPrimes);
            }
        } catch (IOException e) {
            System.err.println("Could not connect to Master Server on localhost:4999");
            e.printStackTrace();
            return; // Exits the client if it fails to connect to the MasterServer
        }
    }
}
