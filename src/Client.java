import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4999);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter start point: ");
            int startPoint = scanner.nextInt();
            System.out.print("Enter end point: ");
            int endPoint = scanner.nextInt();

            dos.writeInt(startPoint);
            dos.writeInt(endPoint);

            // Waiting for response from Master Server
            int numberOfPrimes = dis.readInt();
            System.out.println("Number of primes calculated: " + numberOfPrimes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}