import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    static int nStartPoint = 200;
    static int nEndPoint = 300;

    public static void main(String[] args) {
//        getUserInput();
        sendDataToMasterServer();
        waitForResponse();
    }

    private static void waitForResponse(){

        try(ServerSocket clientServerSocket = new ServerSocket(4998)){

            System.out.println("Please wait for master server to process your data...");

            // continuously accept incoming master server response
            while(true){
                Socket masterServerSocket = clientServerSocket.accept();

                DataInputStream dataInputStream = new DataInputStream(masterServerSocket.getInputStream());

                // receive the total number of primes from the master server
                int primesListSize = dataInputStream.readInt();

                System.out.println("Primes list");
                // print primes list
                for(int i=0; i<primesListSize; i++){
                    System.out.println(dataInputStream.readInt() + " ");
                }

            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private static void sendDataToMasterServer() {
        // ServerSocket constructor params:
        // param 1: server IP address
        //      - on the computer of the server, open cmd, type ipconfig and get the ipv4 address
        // param 2: port number to use

        // create socket to connect client to master server at port 4999
        try{
            // connect to master server
            Socket socket = new Socket("localhost", 4999);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeInt(nStartPoint);
            dataOutputStream.writeInt(nEndPoint);

            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void getUserInput() {
        Scanner scanner = new Scanner(System.in);

        // USER INPUT
        System.out.print("Input start point: ");
        nStartPoint = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline from nextInt()

        System.out.print("Input end point: ");
        nEndPoint = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline from nextInt()
    }

}
